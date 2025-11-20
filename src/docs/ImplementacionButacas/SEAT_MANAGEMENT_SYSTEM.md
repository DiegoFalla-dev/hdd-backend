# 🎬 Sistema de Gestión de Butacas - CinePlus

## 📋 Resumen del Sistema

Este documento describe el **nuevo sistema de gestión de butacas** implementado para CinePlus, que incluye:

- ✅ **Sistema de sesiones** para reservas temporales con Spring Security
- ✅ **Temporizador automático** de 1 minuto para liberar asientos no confirmados
- ✅ **Estados de asientos**: AVAILABLE, TEMPORARILY_RESERVED, OCCUPIED, CANCELLED
- ✅ **Matrices de coordenadas** para organización espacial de asientos
- ✅ **Cancelaciones permanentes** con bloqueo de asientos
- ✅ **Liberación inteligente** de asientos ocupados (excepto cancelados permanentemente)

---

## 🏗️ Arquitectura del Sistema

### **1. Estados de Asientos**

```java
public enum SeatStatus {
    AVAILABLE,              // Disponible para reservar
    OCCUPIED,              // Ocupado después de compra confirmada
    TEMPORARILY_RESERVED,  // Reservado temporalmente (1 minuto)
    CANCELLED              // Cancelado permanentemente
}
```

#### **Transiciones de Estado:**

```
AVAILABLE → TEMPORARILY_RESERVED → OCCUPIED
           ↓ (1 min expira)
         AVAILABLE

OCCUPIED → CANCELLED (con purchaseNumber) → BLOQUEADO PERMANENTE
OCCUPIED → AVAILABLE (sin purchaseNumber)  → Liberado
```

### **2. Entidades Principales**

#### **`Seat` (Asiento)**
```java
- id: Long
- showtime: Showtime
- seatIdentifier: String           // ej: "A1", "B10"
- status: SeatStatus
- sessionId: String                // ID de sesión del usuario
- reservationTime: LocalDateTime   // Momento de reserva
- purchaseNumber: String           // Número de orden/compra
- rowPosition: Integer             // Fila (0-indexed)
- colPosition: Integer             // Columna (0-indexed)
- isCancelled: Boolean            // Cancelación permanente
```

#### **`SeatReservation` (Sesión de Reserva)**
```java
- id: Long
- sessionId: String (UUID único)
- showtime: Showtime
- user: User (opcional)
- createdAt: LocalDateTime
- expiryTime: LocalDateTime       // createdAt + 1 minuto
- isActive: Boolean
- isConfirmed: Boolean
- purchaseNumber: String
- seatIdentifiers: Set<String>    // Asientos en esta sesión
```

---

## 🔄 Flujo de Trabajo

### **Escenario 1: Reserva Exitosa**

```
1. Usuario selecciona asientos → POST /api/seat-reservations/{showtimeId}
   ↓
2. Sistema genera sessionId y marca asientos como TEMPORARILY_RESERVED
   ↓
3. Usuario completa compra → POST /api/seat-reservations/confirm
   ↓
4. Sistema confirma con purchaseNumber y cambia estado a OCCUPIED
```

### **Escenario 2: Abandono de Compra**

```
1. Usuario selecciona asientos → TEMPORARILY_RESERVED
   ↓
2. Usuario cierra ventana (sin confirmar)
   ↓
3. Scheduler detecta expiración (1 minuto)
   ↓
4. Sistema libera automáticamente → AVAILABLE
```

### **Escenario 3: Cancelación Permanente**

```
1. Compra confirmada → OCCUPIED con purchaseNumber
   ↓
2. Administrador cancela → POST /api/seat-reservations/cancel/{showtimeId}
   ↓
3. Asiento marcado como CANCELLED + isCancelled=true
   ↓
4. BLOQUEADO PERMANENTEMENTE (no puede volver a AVAILABLE)
```

### **Escenario 4: Liberación de Asientos Ocupados**

```
1. Asiento OCCUPIED sin purchaseNumber asociado
   ↓
2. Administrador libera → POST /api/seat-reservations/release-occupied/{showtimeId}
   ↓
3. Sistema verifica isCancelled=false
   ↓
4. Asiento vuelve a AVAILABLE
```

---

## 🚀 Endpoints API

### **1. Iniciar Reserva de Asientos**
```http
POST /api/seat-reservations/{showtimeId}
Content-Type: application/json

{
  "seatIdentifiers": ["A1", "A2", "A3"],
  "userId": 123  // Opcional
}

Respuesta:
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "message": "Seats reserved temporarily for 1 minute"
}
```

### **2. Confirmar Compra**
```http
POST /api/seat-reservations/confirm
Content-Type: application/json

{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "purchaseNumber": "ORD-2025-001"
}

Respuesta:
{
  "message": "Purchase confirmed successfully"
}
```

### **3. Liberar Reserva Manualmente**
```http
DELETE /api/seat-reservations/{sessionId}

Respuesta: 204 No Content
```

### **4. Cancelar Asientos Permanentemente**
```http
POST /api/seat-reservations/cancel/{showtimeId}
Content-Type: application/json

{
  "seatIdentifiers": ["A1", "A2"],
  "purchaseNumber": "ORD-2025-001"
}

Respuesta:
{
  "message": "Seats cancelled permanently"
}
```

### **5. Liberar Asientos Ocupados**
```http
POST /api/seat-reservations/release-occupied/{showtimeId}
Content-Type: application/json

{
  "seatIdentifiers": ["B5", "B6"]
}

Respuesta:
{
  "message": "Occupied seats released successfully"
}
```

### **6. Obtener Matriz de Asientos**
```http
GET /api/seat-reservations/{showtimeId}/matrix

Respuesta:
[
  {
    "id": 1,
    "seatIdentifier": "A1",
    "status": "AVAILABLE",
    "rowPosition": 0,
    "colPosition": 0,
    "isCancelled": false,
    "sessionId": null,
    "purchaseNumber": null
  },
  {
    "id": 2,
    "seatIdentifier": "A2",
    "status": "OCCUPIED",
    "rowPosition": 0,
    "colPosition": 1,
    "isCancelled": false,
    "sessionId": null,
    "purchaseNumber": "ORD-2025-001"
  },
  {
    "id": 3,
    "seatIdentifier": "A3",
    "status": "CANCELLED",
    "rowPosition": 0,
    "colPosition": 2,
    "isCancelled": true,
    "sessionId": null,
    "purchaseNumber": "ORD-2025-002"
  }
]
```

### **7. Obtener Asientos de una Sesión**
```http
GET /api/seat-reservations/{sessionId}/seats

Respuesta:
["A1", "A2", "A3"]
```

---

## ⏰ Tarea Programada (Scheduler)

El sistema incluye una tarea automática que se ejecuta cada **30 segundos**:

```java
@Scheduled(fixedRate = 30000)
public void releaseExpiredReservations() {
    // Libera automáticamente reservas expiradas (> 1 minuto)
}
```

**Funcionamiento:**
1. Busca reservas con `isActive=true` y `expiryTime < now()`
2. Para cada reserva expirada:
   - Cambia asientos a AVAILABLE (excepto los isCancelled=true)
   - Libera sessionId
   - Actualiza contador de asientos disponibles
   - Marca la reserva como inactiva

---

## 🗂️ Matrices y Coordenadas

### **Generación de Asientos**

Cuando se crea un showtime, los asientos se generan con coordenadas:

```java
Theater: { rowCount: 5, colCount: 10 }

Genera:
A1  (row=0, col=0)
A2  (row=0, col=1)
...
A10 (row=0, col=9)
B1  (row=1, col=0)
...
E10 (row=4, col=9)
```

### **Búsqueda por Coordenadas**

Puedes buscar bloques de asientos por rango de coordenadas:

```java
// Buscar asientos en filas 2-3, columnas 5-7
List<Seat> seats = seatRepository.findByShowtimeIdAndCoordinateRange(
    showtimeId, 2, 3, 5, 7
);
```

**Uso práctico:**
- Seleccionar secciones completas (VIP, general, etc.)
- Bloquear rangos de asientos para mantenimiento
- Aplicar precios diferenciados por zona

---

## 🔒 Reglas de Negocio

### **✅ Reglas de Liberación**

| Estado | isCancelled | Puede volver a AVAILABLE |
|--------|-------------|-------------------------|
| OCCUPIED | false | ✅ Sí (con endpoint release-occupied) |
| OCCUPIED | true | ❌ No (bloqueado permanente) |
| TEMPORARILY_RESERVED | false | ✅ Sí (automático tras 1 min) |
| CANCELLED | true | ❌ No (bloqueado permanente) |

### **✅ Reglas de Cancelación**

- Solo se pueden cancelar permanentemente asientos con `purchaseNumber`
- La cancelación requiere número de orden
- Asientos cancelados NO pueden ser reservados nuevamente
- `isCancelled=true` es permanente

### **✅ Reglas de Reserva**

- Solo asientos en estado AVAILABLE pueden ser reservados
- Asientos con `isCancelled=true` NO pueden ser reservados
- Cada reserva genera un `sessionId` único
- Las reservas expiran automáticamente después de 1 minuto

---

## 📊 Diagrama de Base de Datos

### **Tabla `seats`**
```sql
CREATE TABLE seats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    showtime_id BIGINT NOT NULL,
    seat_identifier VARCHAR(5) NOT NULL,
    status VARCHAR(20) NOT NULL,
    session_id VARCHAR(100),
    reservation_time DATETIME,
    purchase_number VARCHAR(50),
    row_position INT NOT NULL,
    col_position INT NOT NULL,
    is_cancelled BOOLEAN NOT NULL DEFAULT false,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id),
    INDEX idx_seat_session (session_id),
    INDEX idx_seat_showtime (showtime_id),
    INDEX idx_seat_coordinates (row_position, col_position)
);
```

### **Tabla `seat_reservations`**
```sql
CREATE TABLE seat_reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(100) NOT NULL UNIQUE,
    showtime_id BIGINT NOT NULL,
    user_id BIGINT,
    created_at DATETIME NOT NULL,
    expiry_time DATETIME NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_confirmed BOOLEAN NOT NULL DEFAULT false,
    purchase_number VARCHAR(50),
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_reservation_session (session_id),
    INDEX idx_reservation_showtime (showtime_id),
    INDEX idx_reservation_expiry (expiry_time)
);
```

### **Tabla `reservation_seat_identifiers`**
```sql
CREATE TABLE reservation_seat_identifiers (
    reservation_id BIGINT NOT NULL,
    seat_identifier VARCHAR(5) NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES seat_reservations(id)
);
```

---

## 🧪 Ejemplos de Uso

### **Ejemplo 1: Flujo Completo de Compra**

```javascript
// 1. Usuario selecciona asientos A1, A2, A3
const response1 = await fetch('http://localhost:8080/api/seat-reservations/1', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    seatIdentifiers: ['A1', 'A2', 'A3'],
    userId: 123
  })
});
const { sessionId } = await response1.json();
// sessionId: "550e8400-e29b-41d4-a716-446655440000"

// 2. Usuario completa el pago (antes de 1 minuto)
const response2 = await fetch('http://localhost:8080/api/seat-reservations/confirm', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    sessionId: sessionId,
    purchaseNumber: 'ORD-2025-12345'
  })
});
// Asientos ahora están OCCUPIED con purchaseNumber
```

### **Ejemplo 2: Usuario Abandona la Compra**

```javascript
// 1. Usuario reserva asientos
const response = await fetch('http://localhost:8080/api/seat-reservations/1', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    seatIdentifiers: ['B5', 'B6']
  })
});
const { sessionId } = await response.json();

// 2. Usuario cierra la ventana sin confirmar

// 3. Después de 1 minuto, el scheduler automáticamente:
//    - Cambia B5 y B6 a AVAILABLE
//    - Libera sessionId
//    - Marca reserva como inactiva
```

### **Ejemplo 3: Cancelación con Bloqueo Permanente**

```javascript
// 1. Cancelar asientos de una orden
const response = await fetch('http://localhost:8080/api/seat-reservations/cancel/1', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    seatIdentifiers: ['C10', 'C11'],
    purchaseNumber: 'ORD-2025-99999'
  })
});

// Ahora C10 y C11 están CANCELLED con isCancelled=true
// NO pueden volver a AVAILABLE nunca
```

---

## 🛠️ Configuración

### **1. Habilitar Scheduling**

El scheduling ya está habilitado en la aplicación principal:

```java
@SpringBootApplication
@EnableScheduling
public class ECommerceCineplusBackendApplication {
    // ...
}
```

### **2. Ajustar Tiempo de Expiración**

Para cambiar el tiempo de reserva (actualmente 1 minuto):

```java
// SeatReservationServiceImpl.java
private static final int RESERVATION_DURATION_MINUTES = 1; // Cambiar aquí
```

### **3. Ajustar Frecuencia del Scheduler**

Para cambiar la frecuencia de liberación (actualmente 30 segundos):

```java
// SeatReservationScheduler.java
@Scheduled(fixedRate = 30000) // 30000 ms = 30 segundos
```

---

## 📝 Notas Importantes

1. **Sesiones y Seguridad**: El `sessionId` debe ser guardado en el frontend (localStorage, sessionStorage) para permitir confirmación de compra.

2. **Concurrencia**: El sistema maneja conflictos automáticamente. Si dos usuarios intentan reservar el mismo asiento, solo uno tendrá éxito.

3. **Índices de Base de Datos**: Se agregaron índices en `session_id`, `showtime_id` y `coordinates` para optimizar consultas.

4. **Logs**: El sistema registra todas las operaciones importantes para auditoría y debugging.

5. **Transacciones**: Todas las operaciones críticas usan `@Transactional` para garantizar consistencia.

---

## 🔍 Servicios y Repositorios

### **Servicios Creados:**
- `SeatReservationService` - Lógica de negocio de reservas
- `SeatReservationServiceImpl` - Implementación

### **Repositorios Actualizados:**
- `SeatRepository` - Nuevos métodos para sesiones y coordenadas
- `SeatReservationRepository` - Gestión de sesiones de reserva

### **Controladores:**
- `SeatReservationController` - Endpoints REST para gestión de reservas
- `ShowtimeController` - Endpoints existentes (mantiene compatibilidad)

### **Configuración:**
- `SeatReservationScheduler` - Tarea programada para liberación automática

---

## ✨ Ventajas del Nuevo Sistema

✅ **Mejor experiencia de usuario**: Reserva temporal sin compromiso inmediato
✅ **Prevención de bloqueos**: Liberación automática de asientos no confirmados
✅ **Gestión de cancelaciones**: Bloqueo permanente de asientos problemáticos
✅ **Organización espacial**: Coordenadas para visualización y gestión por zonas
✅ **Auditoría completa**: Tracking de sessionId, purchaseNumber y timestamps
✅ **Escalabilidad**: Arquitectura preparada para alto volumen de transacciones

---

**Desarrollado para CinePlus Backend**  
**Fecha**: Noviembre 2025  
**Versión**: 2.0
