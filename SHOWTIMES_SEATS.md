# Sistema de Funciones y Asientos - CinePlus

## 📋 Descripción General

El sistema de Funciones (Showtimes) y Asientos gestiona las proyecciones de películas, horarios, disponibilidad de asientos y reservas temporales. Es el núcleo del proceso de compra de tickets.

---

## 🏗️ Estructura de Datos

### Entidad `Showtime` (Función)

```java
@Entity
@Table(name = "showtimes")
public class Showtime {
    private Long id;
    private Movie movie;              // Película que se proyecta
    private Theater theater;          // Sala donde se proyecta
    private LocalDate date;           // Fecha de la función
    private LocalTime time;           // Hora de inicio
    private FormatType format;        // Formato de proyección
    private int availableSeats;       // Asientos disponibles
    private BigDecimal price;         // Precio base por entrada
}
```

### Entidad `Seat` (Asiento)

```java
@Entity
@Table(name = "seats")
public class Seat {
    private Long id;
    private Showtime showtime;        // Función a la que pertenece
    private String seatIdentifier;    // Identificador (ej: "A1", "B10")
    private SeatStatus status;        // Estado del asiento
}
```

### Enum `FormatType` (Formato de Proyección)

```java
public enum FormatType {
    _2D,    // 2D estándar
    _3D,    // 3D (requiere lentes)
    XD      // Extreme Digital (pantalla y sonido premium)
}
```

### Enum `SeatStatus` (Estado de Asiento)

```java
public enum SeatStatus {
    AVAILABLE,              // Disponible para compra
    OCCUPIED,              // Ocupado (ticket vendido)
    TEMPORARILY_RESERVED   // Reservado temporalmente (en carrito)
}
```

---

## 🔗 Relaciones

```
Movie (1) ─────── (N) Showtime
Theater (1) ────── (N) Showtime
Showtime (1) ───── (N) Seat

Showtime
   │
   ├─ movie: Movie
   ├─ theater: Theater
   ├─ date: LocalDate
   ├─ time: LocalTime
   └─ format: FormatType
   
Seat
   │
   ├─ showtime: Showtime
   ├─ seatIdentifier: String
   └─ status: SeatStatus
```

---

## 🔌 API Endpoints

### **Showtimes (Funciones)**

#### Listar Funciones
```http
GET /api/showtimes?cinema={cinemaId}&movie={movieId}&date={date}&format={format}
```

**Casos de uso:**

1. **Todas las funciones de un cine**
```http
GET /api/showtimes?cinema=1
```

2. **Fechas disponibles para una película en un cine**
```http
GET /api/showtimes?cinema=1&movie=5
```
Retorna funciones agrupadas por fecha.

3. **Funciones de una película en una fecha específica**
```http
GET /api/showtimes?cinema=1&movie=5&date=2024-12-25
```
Retorna todos los horarios y formatos disponibles.

4. **Horarios específicos por formato**
```http
GET /api/showtimes?cinema=1&movie=5&date=2024-12-25&format=_3D
```

**Respuesta:**
```json
[
  {
    "id": 101,
    "movieId": 5,
    "movieTitle": "Avatar: The Way of Water",
    "theaterId": 3,
    "theaterName": "Sala 3D",
    "date": "2024-12-25",
    "time": "14:30:00",
    "format": "_3D",
    "availableSeats": 120,
    "price": 25.00
  },
  {
    "id": 102,
    "movieId": 5,
    "movieTitle": "Avatar: The Way of Water",
    "theaterId": 3,
    "theaterName": "Sala 3D",
    "date": "2024-12-25",
    "time": "18:00:00",
    "format": "_3D",
    "availableSeats": 95,
    "price": 28.00
  }
]
```

#### Obtener Función por ID
```http
GET /api/showtimes/{id}?cinema={cinemaId}
```

#### Crear Función (ADMIN)
```http
POST /api/showtimes
Authorization: Bearer {token}
Content-Type: application/json

{
  "movieId": 5,
  "theaterId": 3,
  "date": "2024-12-26",
  "time": "20:00:00",
  "format": "_3D",
  "price": 28.00
}
```

#### Actualizar Función (ADMIN)
```http
PUT /api/showtimes/{id}
Authorization: Bearer {token}
```

#### Eliminar Función (ADMIN)
```http
DELETE /api/showtimes/{id}
Authorization: Bearer {token}
```

---

### **Seats (Asientos)**

#### Listar Asientos de una Función
```http
GET /api/showtimes/{showtimeId}/seats
```
**Respuesta:**
```json
[
  {
    "id": 1001,
    "showtimeId": 101,
    "seatIdentifier": "A1",
    "status": "AVAILABLE"
  },
  {
    "id": 1002,
    "showtimeId": 101,
    "seatIdentifier": "A2",
    "status": "OCCUPIED"
  },
  {
    "id": 1003,
    "showtimeId": 101,
    "seatIdentifier": "A3",
    "status": "TEMPORARILY_RESERVED"
  }
]
```

#### Obtener Asientos Ocupados
```http
GET /api/showtimes/{showtimeId}/seats/occupied
```
**Respuesta:**
```json
["A2", "B5", "C10", "D3"]
```

#### Reservar Asientos Temporalmente
```http
POST /api/showtimes/{showtimeId}/seats/reserve
Content-Type: application/json

{
  "seatIdentifiers": ["A1", "A2", "A3"]
}
```
**Respuesta Exitosa (200):**
```json
[]
```
**Respuesta Parcial (409 Conflict):**
```json
["A2"]
```
Indica que A2 no se pudo reservar (ya ocupado).

#### Liberar Asientos Reservados
```http
POST /api/showtimes/{showtimeId}/seats/release
Content-Type: application/json

{
  "seatIdentifiers": ["A1", "A2", "A3"]
}
```

#### Confirmar Asientos (Compra Final)
```http
POST /api/showtimes/{showtimeId}/seats/confirm
Content-Type: application/json

{
  "seatIdentifiers": ["A1", "A2", "A3"]
}
```
Cambia el estado de `TEMPORARILY_RESERVED` a `OCCUPIED`.

#### Generar Asientos para Función (ADMIN)
```http
POST /api/showtimes/{showtimeId}/seats/generate
Authorization: Bearer {token}
```
Genera automáticamente todos los asientos basándose en la configuración del `Theater`.

---

## 🔄 Flujo de Compra de Tickets

```
┌─────────────────────────────────────────────────┐
│ 1. Usuario selecciona película, cine y fecha   │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 2. Sistema muestra funciones disponibles       │
│    GET /api/showtimes?cinema=1&movie=5&date=... │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 3. Usuario selecciona función y horario        │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 4. Sistema muestra mapa de asientos            │
│    GET /api/showtimes/{id}/seats                │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 5. Usuario selecciona asientos                 │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 6. Sistema reserva temporalmente                │
│    POST /api/showtimes/{id}/seats/reserve       │
│    Estado: AVAILABLE → TEMPORARILY_RESERVED     │
└────────────────┬────────────────────────────────┘
                 │
          ┌──────┴──────┐
          │             │
          ▼             ▼
    Usuario paga   Tiempo expira
          │             │
          │             ▼
          │      POST /seats/release
          │      Estado: TEMPORARILY_RESERVED → AVAILABLE
          │
          ▼
┌─────────────────────────────────────────────────┐
│ 7. Sistema confirma compra                      │
│    POST /api/showtimes/{id}/seats/confirm       │
│    Estado: TEMPORARILY_RESERVED → OCCUPIED      │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 8. Se genera OrderItem con el Seat             │
│    availableSeats -= 1                          │
└─────────────────────────────────────────────────┘
```

---

## ⏱️ Gestión de Reservas Temporales

### Tiempo de Expiración
Las reservas temporales deben expirar automáticamente después de un tiempo (ej: 10 minutos).

### Implementación Recomendada
```javascript
// Frontend
const RESERVATION_TIMEOUT = 10 * 60 * 1000; // 10 minutos

function reserveSeats(showtimeId, seats) {
  // Reservar asientos
  api.post(`/api/showtimes/${showtimeId}/seats/reserve`, { 
    seatIdentifiers: seats 
  });
  
  // Iniciar temporizador
  setTimeout(() => {
    // Liberar si no se completó la compra
    if (!purchaseCompleted) {
      api.post(`/api/showtimes/${showtimeId}/seats/release`, { 
        seatIdentifiers: seats 
      });
    }
  }, RESERVATION_TIMEOUT);
}
```

---

## 💡 Ejemplos de Uso

### Ejemplo 1: Crear Función 2D Matinée
```json
{
  "movieId": 10,
  "theaterId": 2,
  "date": "2024-12-20",
  "time": "11:00:00",
  "format": "_2D",
  "price": 15.00
}
```

### Ejemplo 2: Crear Función 3D Nocturna Premium
```json
{
  "movieId": 10,
  "theaterId": 4,
  "date": "2024-12-20",
  "time": "22:00:00",
  "format": "_3D",
  "price": 32.00
}
```

### Ejemplo 3: Crear Función XD
```json
{
  "movieId": 10,
  "theaterId": 5,
  "date": "2024-12-20",
  "time": "19:30:00",
  "format": "XD",
  "price": 38.00
}
```

---

## 📐 Nomenclatura de Asientos

### Patrón Estándar
- **Filas**: Letras (A, B, C, ..., Z)
- **Columnas**: Números (1, 2, 3, ..., N)
- **Formato**: `{Fila}{Columna}` (ej: A1, B10, F15)

### Ejemplos
- **Sala pequeña**: A1-A8, B1-B8, ..., F1-F8 (48 asientos)
- **Sala mediana**: A1-A12, B1-B12, ..., J1-J12 (120 asientos)
- **Sala grande**: A1-A20, B1-B20, ..., P1-P20 (320 asientos)

---

## 🎨 Visualización del Mapa de Asientos

### Representación Recomendada

```
        PANTALLA
   ╔════════════════╗
   
   A  O  O  O  O  X  X  O  O  O
   B  T  T  O  O  O  O  O  O  O
   C  O  O  O  O  O  O  O  X  X
   D  O  O  O  O  O  O  O  O  O
   E  O  O  O  O  O  O  O  O  O
   
   Leyenda:
   O = Disponible (AVAILABLE)
   X = Ocupado (OCCUPIED)
   T = Reservado temporalmente (TEMPORARILY_RESERVED)
```

---

## 🔒 Seguridad y Permisos

| Acción | Rol Requerido | Endpoint |
|--------|---------------|----------|
| Listar funciones | Público | `GET /api/showtimes` |
| Ver función específica | Público | `GET /api/showtimes/{id}` |
| Listar asientos | Público | `GET /api/showtimes/{id}/seats` |
| Ver asientos ocupados | Público | `GET /api/showtimes/{id}/seats/occupied` |
| Reservar asientos | Autenticado | `POST /api/showtimes/{id}/seats/reserve` |
| Liberar asientos | Autenticado | `POST /api/showtimes/{id}/seats/release` |
| Confirmar asientos | Autenticado | `POST /api/showtimes/{id}/seats/confirm` |
| Crear función | `ADMIN` | `POST /api/showtimes` |
| Actualizar función | `ADMIN` | `PUT /api/showtimes/{id}` |
| Eliminar función | `ADMIN` | `DELETE /api/showtimes/{id}` |
| Generar asientos | `ADMIN` | `POST /api/showtimes/{id}/seats/generate` |

---

## 🛡️ Validaciones

### Validaciones al Crear Showtime
- ✅ `movieId` debe existir
- ✅ `theaterId` debe existir
- ✅ `date` no puede ser en el pasado
- ✅ `time` debe ser válida
- ✅ `format` debe coincidir con los formatos disponibles del cinema
- ✅ `price` debe ser > 0
- ✅ No debe haber conflicto de horarios en la misma sala

### Validaciones de Asientos
- ✅ `seatIdentifier` debe seguir el patrón válido
- ✅ No se puede reservar un asiento `OCCUPIED`
- ✅ No se puede reservar un asiento ya `TEMPORARILY_RESERVED` por otro usuario
- ✅ No se puede confirmar un asiento que no está `TEMPORARILY_RESERVED`

---

## 📊 Consultas Útiles

### Funciones con más ventas
```sql
SELECT 
    s.id,
    m.title,
    s.date,
    s.time,
    (t.total_seats - s.available_seats) as tickets_sold,
    ((t.total_seats - s.available_seats) * 100.0 / t.total_seats) as occupancy_rate
FROM showtimes s
JOIN movies m ON s.movie_id = m.id
JOIN theaters t ON s.theater_id = t.id
ORDER BY tickets_sold DESC
LIMIT 10;
```

### Horarios más populares
```sql
SELECT 
    TIME_FORMAT(time, '%H:00') as hour_slot,
    COUNT(*) as showtime_count,
    AVG(t.total_seats - s.available_seats) as avg_tickets_sold
FROM showtimes s
JOIN theaters t ON s.theater_id = t.id
GROUP BY hour_slot
ORDER BY avg_tickets_sold DESC;
```

### Asientos más comprados
```sql
SELECT seat_identifier, COUNT(*) as purchase_count
FROM seats
WHERE status = 'OCCUPIED'
GROUP BY seat_identifier
ORDER BY purchase_count DESC
LIMIT 20;
```

### Tasa de ocupación por formato
```sql
SELECT 
    s.format,
    AVG((t.total_seats - s.available_seats) * 100.0 / t.total_seats) as avg_occupancy
FROM showtimes s
JOIN theaters t ON s.theater_id = t.id
GROUP BY s.format;
```

---

## 🚀 Mejoras Futuras

1. **Precios Dinámicos**
   - Precios por horario (matinée, nocturno, medianoche)
   - Precios por día (fin de semana más caro)
   - Demand-based pricing

2. **Asientos Premium**
   - Asientos VIP (centro, atrás)
   - Asientos regulares (laterales, adelante)
   - Asientos con descuento

3. **Selección Automática**
   - "Mejores asientos disponibles"
   - Grupos de asientos juntos
   - Asientos para discapacitados

4. **Notificaciones**
   - Recordatorio 1 hora antes de la función
   - Notificación cuando se liberen asientos deseados
   - Alertas de cambios de horario

5. **Estadísticas Avanzadas**
   - Mapa de calor de asientos
   - Predicción de ocupación
   - Recomendación de horarios menos concurridos

---

## 📚 Referencias

- **Entidad Showtime**: `domain/entity/Showtime.java`
- **Entidad Seat**: `domain/entity/Seat.java`
- **DTO Showtime**: `domain/dto/ShowtimeDto.java`
- **DTO Seat**: `domain/dto/SeatDto.java`
- **Service**: `persistence/service/impl/ShowtimeServiceImpl.java`
- **Controller**: `web/controller/ShowtimeController.java`
- **Enums**: `domain/entity/Showtime.FormatType`, `domain/entity/Seat.SeatStatus`

---

**Última actualización:** Diciembre 2025  
**Versión:** 1.0
