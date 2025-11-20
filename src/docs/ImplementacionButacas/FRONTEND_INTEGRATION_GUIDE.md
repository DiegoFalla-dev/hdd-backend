# 📘 Guía de Integración Frontend - Sistema de Butacas CinePlus v2.0

**Fecha:** 20 de Noviembre 2025  
**Backend:** Spring Boot 3.2.5 + MySQL 8  
**API Base URL:** `http://localhost:8080`

---

## 🎯 PROBLEMA ACTUAL Y SOLUCIÓN

### ❌ Problema
Actualmente el frontend genera un `showtimeId` temporal:
```typescript
const temporaryShowtimeId = `${selectedCinemaData.id}_${pelicula.id}_${selectedDay}_${selectedTime}_${selectedFormat}`;
```

**Este ID NO EXISTE en el backend** y causará errores 404 en todas las llamadas de reserva.

### ✅ Solución
Debes obtener el **`showtimeId` REAL** desde el backend antes de navegar a la página de butacas.

---

## 📊 ESTRUCTURA DE DATOS DEL BACKEND

### 1. SHOWTIMES (Funciones)

El backend ya tiene **30+ funciones** pre-cargadas en la base de datos. Aquí está la estructura:

```sql
-- Tabla: showtimes
CREATE TABLE showtimes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  movie_id BIGINT NOT NULL,
  theater_id BIGINT NOT NULL,
  date DATE NOT NULL,
  time TIME NOT NULL,
  format ENUM('_2D', '_3D', 'XD') NOT NULL,
  available_seats INT NOT NULL,
  FOREIGN KEY (movie_id) REFERENCES movies(id),
  FOREIGN KEY (theater_id) REFERENCES theaters(id)
);
```

**Datos reales en la BD (ejemplos):**
```sql
-- movie_id = 1 (Andrea Bocelli), theater_id = 1 (Sala 1 del Real Plaza Trujillo)
INSERT INTO showtimes VALUES (1, 1, 1, '2025-11-20', '14:00:00', '_2D', 50);
INSERT INTO showtimes VALUES (2, 1, 1, '2025-11-20', '18:30:00', '_2D', 50);
INSERT INTO showtimes VALUES (3, 1, 2, '2025-11-21', '16:00:00', '_2D', 80);

-- movie_id = 17 (Los 4 Fantásticos)
INSERT INTO showtimes VALUES (34, 17, 11, '2025-11-20', '14:00:00', '_2D', 60);
INSERT INTO showtimes VALUES (35, 17, 12, '2025-11-20', '17:00:00', '_3D', 100);
INSERT INTO showtimes VALUES (36, 17, 13, '2025-11-20', '20:30:00', '_3D', 120);
```

### 2. THEATERS (Salas)

Cada función está asociada a una sala específica con dimensiones predefinidas:

```sql
-- Ejemplo: Cineplus Real Plaza Trujillo (cinema_id = 1)
theater_id = 1: Sala 1, SMALL,  5 filas × 10 columnas = 50 butacas
theater_id = 2: Sala 2, MEDIUM, 8 filas × 10 columnas = 80 butacas
theater_id = 3: Sala 3, LARGE, 10 filas × 10 columnas = 100 butacas

-- Ejemplo: Cineplus Jockey Plaza (cinema_id = 7)
theater_id = 19: Sala 1, LARGE,  10 filas × 10 columnas = 100 butacas
theater_id = 20: Sala 2, XLARGE, 12 filas × 10 columnas = 120 butacas
theater_id = 21: Sala 3, XLARGE, 15 filas × 10 columnas = 150 butacas
```

---

## 🔌 ENDPOINTS DE LA API

### 📍 Endpoint 1: Obtener Funciones (SHOWTIMES)

**✅ IMPLEMENTADO: Este endpoint ya está disponible en el backend**

```
GET /api/showtimes
```

**Query Parameters:**
- `movieId` (Long, **required**): ID de la película
- `cinemaId` (Long, optional): ID del cine (para filtrar por ubicación)
- `date` (String, optional): Fecha en formato `YYYY-MM-DD`

**Ejemplos de Request:**
```bash
# Todas las funciones de una película
curl "http://localhost:8080/api/showtimes?movieId=17"

# Funciones de una película en un cine específico
curl "http://localhost:8080/api/showtimes?movieId=17&cinemaId=7"

# Funciones de una película en una fecha específica
curl "http://localhost:8080/api/showtimes?movieId=17&date=2025-11-20"

# Funciones con todos los filtros
curl "http://localhost:8080/api/showtimes?movieId=17&cinemaId=7&date=2025-11-20"
```

**Respuesta (JSON):**
```json
[
  {
    "id": 34,
    "movieId": 17,
    "theaterId": 11,
    "theaterName": "Sala 1",
    "cinemaId": 7,
    "cinemaName": "Cineplus Jockey Plaza",
    "date": "2025-11-20",
    "time": "14:00:00",
    "format": "_2D",
    "availableSeats": 60,
    "totalSeats": 60,
    "seatMatrixType": "SMALL"
  },
  {
    "id": 35,
    "movieId": 17,
    "theaterId": 12,
    "theaterName": "Sala 2",
    "cinemaId": 7,
    "cinemaName": "Cineplus Jockey Plaza",
    "date": "2025-11-20",
    "time": "17:00:00",
    "format": "_3D",
    "availableSeats": 100,
    "totalSeats": 100,
    "seatMatrixType": "MEDIUM"
  }
]
```

---

### 📍 Endpoint 2: Obtener Matriz de Butacas

**✅ Este endpoint YA EXISTE en el backend**

```
GET /api/seat-reservations/{showtimeId}/matrix
```

**Path Parameter:**
- `showtimeId` (Long): ID real de la función obtenido del endpoint anterior

**Ejemplo de Request:**
```bash
curl "http://localhost:8080/api/seat-reservations/34/matrix"
```

**Respuesta (JSON):**
```json
{
  "rowCount": 10,
  "colCount": 10,
  "seats": [
    [
      {
        "id": 1,
        "seatNumber": "A1",
        "status": "AVAILABLE",
        "rowPosition": 0,
        "colPosition": 0,
        "sessionId": null,
        "reservationTime": null,
        "purchaseNumber": null,
        "isCancelled": false
      },
      {
        "id": 2,
        "seatNumber": "A2",
        "status": "TEMPORARILY_RESERVED",
        "rowPosition": 0,
        "colPosition": 1,
        "sessionId": "550e8400-e29b-41d4-a716-446655440000",
        "reservationTime": "2025-11-20T14:30:00",
        "purchaseNumber": null,
        "isCancelled": false
      }
    ]
  ]
}
```

---

### 📍 Endpoint 3: Reservar Butacas

**✅ Este endpoint YA EXISTE en el backend**

```
POST /api/seat-reservations/{showtimeId}
```

**Path Parameter:**
- `showtimeId` (Long): ID real de la función

**Request Body (JSON):**
```json
{
  "seatIds": [5, 6, 7]
}
```

**Respuesta (JSON):**
```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "expiryTime": "2025-11-20T14:31:00",
  "reservedSeats": [
    {
      "id": 5,
      "seatNumber": "A5",
      "status": "TEMPORARILY_RESERVED",
      "rowPosition": 0,
      "colPosition": 4,
      "sessionId": "550e8400-e29b-41d4-a716-446655440000",
      "reservationTime": "2025-11-20T14:30:00"
    }
  ]
}
```

---

### 📍 Endpoint 4: Confirmar Compra

**✅ Este endpoint YA EXISTE en el backend**

```
POST /api/seat-reservations/confirm
```

**Request Body (JSON):**
```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "purchaseNumber": "ORD-20251120-1234"
}
```

**Respuesta:** `200 OK` (sin body)

---

### 📍 Endpoint 5: Liberar Reserva

**✅ Este endpoint YA EXISTE en el backend**

```
DELETE /api/seat-reservations/{sessionId}
```

**Path Parameter:**
- `sessionId` (String): UUID de la sesión

**Respuesta:** `200 OK` (sin body)

---

## 💻 IMPLEMENTACIÓN FRONTEND

### Paso 1: Crear Servicio de Showtimes

```typescript
// src/services/showtimesApi.ts
import apiClient from './apiClient';

export interface Showtime {
  id: number;
  movieId: number;
  theaterId: number;
  theaterName: string;
  cinemaId: number;
  cinemaName: string;
  date: string; // "YYYY-MM-DD"
  time: string; // "HH:mm:ss"
  format: '_2D' | '_3D' | 'XD';
  availableSeats: number;
  totalSeats: number;
}

export const getShowtimes = async (
  movieId: number,
  cinemaId?: number,
  date?: string
): Promise<Showtime[]> => {
  const params: any = { movieId };
  if (cinemaId) params.cinemaId = cinemaId;
  if (date) params.date = date;

  const response = await apiClient.get<Showtime[]>('/api/showtimes', { params });
  return response.data;
};
```

---

### Paso 2: Actualizar DetallePelicula.tsx

Reemplazar la generación del `showtimeId` temporal:

```typescript
// ANTES (❌ INCORRECTO):
const temporaryShowtimeId = `${selectedCinemaData.id}_${pelicula.id}_${selectedDay}_${selectedTime}_${selectedFormat}`.replace(/\//g, '-');

// DESPUÉS (✅ CORRECTO):
import { getShowtimes } from '../services/showtimesApi';

const handleComprarEntradas = async () => {
  if (!selectedCinemaData || !selectedDay || !selectedTime || !selectedFormat) {
    alert('Por favor selecciona todos los campos');
    return;
  }

  try {
    // 1. Obtener funciones disponibles desde el backend
    const showtimes = await getShowtimes(
      Number(pelicula.id),
      selectedCinemaData.id,
      selectedDay
    );

    // 2. Encontrar la función exacta seleccionada
    const selectedShowtime = showtimes.find(
      st => st.time.substring(0, 5) === selectedTime && 
            st.format === `_${selectedFormat}`
    );

    if (!selectedShowtime) {
      alert('Función no disponible. Por favor selecciona otro horario.');
      return;
    }

    // 3. Guardar en localStorage con el showtimeId REAL
    localStorage.setItem('movieSelection', JSON.stringify({
      pelicula: pelicula,
      selectedDay,
      selectedTime,
      selectedFormat,
      selectedCineId: selectedCinemaData.id,
      showtimeId: selectedShowtime.id, // ✅ ID REAL del backend
      availableSeats: selectedShowtime.availableSeats,
      theaterName: selectedShowtime.theaterName
    }));

    // 4. Navegar a la página de confirmación
    navigate('/confirmacion');
  } catch (error) {
    console.error('Error al obtener funciones:', error);
    alert('Error al cargar la información de la función. Intenta nuevamente.');
  }
};
```

---

### Paso 3: Verificar Butacas.tsx

**No necesitas cambiar nada**, este código ya está listo:

```typescript
// Este código YA lee correctamente el showtimeId desde localStorage
const savedShowtimeId = (() => {
  try {
    const ms = localStorage.getItem('movieSelection');
    if (ms) {
      const parsed = JSON.parse(ms);
      if (parsed?.showtimeId) return String(parsed.showtimeId);
    }
  } catch (e) {
    console.error('Error parsing movieSelection for showtimeId:', e);
  }
  return '1'; // Fallback solo para desarrollo
})();
```

---

### Paso 4: (Opcional) Mostrar Asientos Disponibles en la UI

```tsx
// En DetallePelicula.tsx, al listar horarios:
{availableTimes.map(time => {
  const showtime = showtimes.find(
    st => st.time.substring(0, 5) === time && 
          st.format === `_${selectedFormat}`
  );
  const available = showtime?.availableSeats ?? 0;
  
  return (
    <button
      key={time}
      className={`px-6 py-2 rounded-lg transition ${
        selectedTime === time 
          ? 'bg-red-600 text-white' 
          : 'bg-gray-700 text-white hover:bg-gray-600'
      }`}
      onClick={() => setSelectedTime(time)}
      disabled={available === 0}
    >
      {time}
      <span className="text-xs ml-2">
        ({available > 0 ? `${available} disponibles` : 'Agotado'})
      </span>
    </button>
  );
})}
```

---

## 🧪 TESTING Y VALIDACIÓN

### 1. Verificar que el Backend Esté Corriendo

```powershell
# En el directorio del backend
.\mvnw.cmd spring-boot:run
```

Deberías ver en los logs:
```
Started ECommerceCineplusBackendApplication in 5.945 seconds
```

### 2. Probar el Endpoint de Showtimes

```bash
# Test 1: Obtener funciones de la película 1
curl "http://localhost:8080/api/showtimes?movieId=1"

# Test 2: Filtrar por cine y fecha
curl "http://localhost:8080/api/showtimes?movieId=17&cinemaId=7&date=2025-11-20"
```

**✅ Este endpoint ya está implementado** y debería devolver un array JSON con las funciones disponibles.

### 3. Probar el Endpoint de Matriz de Butacas

```bash
# Usando un showtimeId real (por ejemplo, id=1)
curl "http://localhost:8080/api/seat-reservations/1/matrix"
```

**Respuesta esperada:** JSON con `rowCount`, `colCount` y matriz `seats[][]`

### 4. Probar Reserva desde el Frontend

1. Selecciona una película, cine, fecha, horario y formato
2. Haz clic en "COMPRAR ENTRADAS"
3. Abre las DevTools del navegador (F12)
4. Ve a la pestaña **Application → Local Storage**
5. Verifica que `movieSelection` tenga un `showtimeId` numérico:
   ```json
   {
     "showtimeId": 34,
     "selectedDay": "2025-11-20",
     "selectedTime": "14:00",
     "selectedFormat": "2D"
   }
   ```
6. Navega a `/butacas`
7. Verifica que la matriz de butacas se cargue correctamente

---

## 🔥 DATOS DE PRUEBA DISPONIBLES

El backend tiene **30+ funciones pre-cargadas**. Aquí algunas para testing:

### Película: Andrea Bocelli (movie_id = 1)
- **Showtime ID 1:** 2025-11-20 a las 14:00, Formato 2D, 50 butacas
- **Showtime ID 2:** 2025-11-20 a las 18:30, Formato 2D, 50 butacas
- **Showtime ID 3:** 2025-11-21 a las 16:00, Formato 2D, 80 butacas

### Película: Los 4 Fantásticos (movie_id = 17)
- **Showtime ID 34:** 2025-11-20 a las 14:00, Formato 2D, 60 butacas
- **Showtime ID 35:** 2025-11-20 a las 17:00, Formato 3D, 100 butacas
- **Showtime ID 36:** 2025-11-20 a las 20:30, Formato 3D, 120 butacas
- **Showtime ID 37:** 2025-11-21 a las 15:00, Formato 2D, 50 butacas
- **Showtime ID 38:** 2025-11-22 a las 19:00, Formato 3D, 80 butacas

### Película: Demon Slayer (movie_id = 13)
- **Showtime ID 46:** 2025-11-20 a las 18:00, Formato 2D, 80 butacas
- **Showtime ID 47:** 2025-11-21 a las 19:30, Formato 3D, 100 butacas
- **Showtime ID 48:** 2025-11-22 a las 21:00, Formato 3D, 60 butacas

---

## ⚠️ NOTAS IMPORTANTES

### Formato de Enums

El backend usa **guion bajo como prefijo** en los formatos para evitar conflictos con palabras reservadas de SQL:

- **Backend:** `_2D`, `_3D`, `XD`
- **Frontend:** `2D`, `3D`, `XD`

**Conversión necesaria:**
```typescript
// Al enviar al backend
const backendFormat = `_${selectedFormat}`; // "2D" → "_2D"

// Al recibir del backend
const frontendFormat = showtime.format.replace('_', ''); // "_2D" → "2D"
```

### Tiempo de Expiración de Reservas

- Reservas temporales expiran en **1 minuto**
- El scheduler del backend limpia reservas expiradas cada **30 segundos**
- El timer del frontend debe sincronizarse con `expiryTime` del backend

### IDs Auto-incrementales

Los `showtimeId` son **auto-generados por MySQL**. No intentes crear IDs manualmente.

---

## 📊 ESTADO DE IMPLEMENTACIÓN BACKEND

**✅ Endpoints Completamente Implementados:**
- `GET /api/showtimes` - Obtener funciones con filtros
- `GET /api/seat-reservations/{showtimeId}/matrix` - Matriz de butacas
- `POST /api/seat-reservations/{showtimeId}` - Reservar butacas
- `POST /api/seat-reservations/confirm` - Confirmar compra
- `DELETE /api/seat-reservations/{sessionId}` - Liberar reserva

**Backend Listo:** Todos los endpoints necesarios están funcionando correctamente.

---

## ✅ CHECKLIST DE INTEGRACIÓN

- [ ] Backend corriendo en `http://localhost:8080`
- [ ] Endpoint `/api/showtimes` implementado y probado
- [ ] Servicio `showtimesApi.ts` creado en frontend
- [ ] `DetallePelicula.tsx` actualizado para obtener `showtimeId` real
- [ ] `localStorage.movieSelection.showtimeId` es un número válido
- [ ] Matriz de butacas se carga correctamente en `/butacas`
- [ ] Reservas funcionan con `showtimeId` real
- [ ] Timer de 60 segundos funciona correctamente
- [ ] Confirmación de compra exitosa con `purchaseNumber`

---

**Documentación generada:** 20 de Noviembre 2025  
**Versión Backend:** Spring Boot 3.2.5, Sistema de Butacas v2.0  
**Repositorio:** `hdd-backend` (branch: `hotfix-ESTABLE-BUTACAS`)

---

# 🤖 PROMPT PARA COPILOT - INTEGRACIÓN FRONTEND

Copia y pega este prompt directamente en GitHub Copilot Chat:

```
Necesito integrar el sistema de reserva de butacas con el backend real. Actualmente el código genera un showtimeId temporal que no existe en el backend, causando errores 404.

ENDPOINT DISPONIBLE EN EL BACKEND:
GET http://localhost:8080/api/showtimes?movieId={id}&cinemaId={id}&date={fecha}

Parámetros:
- movieId (number, REQUERIDO): ID de la película
- cinemaId (number, opcional): ID del cine
- date (string, opcional): Fecha en formato "YYYY-MM-DD"

Respuesta JSON:
[
  {
    "id": 34,
    "movieId": 17,
    "theaterId": 11,
    "theaterName": "Sala 1",
    "cinemaId": 7,
    "cinemaName": "Cineplus Jockey Plaza",
    "date": "2025-11-20",
    "time": "14:00:00",
    "format": "_2D",
    "availableSeats": 60,
    "totalSeats": 60,
    "seatMatrixType": "SMALL"
  }
]

TAREAS:
1. Crear el archivo src/services/showtimesApi.ts con:
   - Interface Showtime con todos los campos del JSON
   - Función getShowtimes(movieId, cinemaId?, date?) que llame al endpoint usando apiClient

2. Modificar src/pages/DetallePelicula.tsx en la función handleComprarEntradas():
   - ELIMINAR la línea que genera temporaryShowtimeId
   - Llamar a getShowtimes() con los parámetros: movieId, selectedCinemaData.id, selectedDay
   - Buscar en el array resultante la función que coincida con selectedTime y selectedFormat
   - IMPORTANTE: El backend usa formato "_2D", "_3D", "XD" (con guion bajo), pero el frontend usa "2D", "3D", "XD"
   - Convertir el formato: const backendFormat = `_${selectedFormat}`
   - Buscar: showtimes.find(st => st.time.substring(0,5) === selectedTime && st.format === backendFormat)
   - Guardar en localStorage el showtimeId REAL: selectedShowtime.id (es un número)
   - Mostrar alert si no se encuentra la función
   - Manejar errores con try/catch

3. El archivo src/pages/Butacas.tsx NO necesita cambios, ya lee correctamente el showtimeId desde localStorage.

4. (Opcional) En DetallePelicula.tsx al mostrar los horarios, puedes agregar el número de butacas disponibles usando showtime.availableSeats

IMPORTANTE:
- El showtimeId debe ser un número, no un string compuesto
- Usar async/await para la llamada al API
- El tiempo del backend viene en formato "HH:mm:ss", usar .substring(0,5) para comparar con "HH:mm"
- Agregar manejo de errores con mensajes claros al usuario
```

---

**VALIDACIÓN POST-IMPLEMENTACIÓN:**

Después de que Copilot implemente los cambios, verifica:

1. ✅ Archivo `showtimesApi.ts` creado con interface y función correctas
2. ✅ `DetallePelicula.tsx` llama a `getShowtimes()` antes de navegar
3. ✅ `localStorage.movieSelection.showtimeId` es un número (ej: 34, no "7_17_2025-11-20_14:00_2D")
4. ✅ La página `/butacas` carga la matriz de asientos correctamente
5. ✅ Las reservas funcionan sin errores 404

---
