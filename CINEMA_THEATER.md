# Sistema de Cines y Salas - CinePlus

## 📋 Descripción General

El sistema de Cines y Salas gestiona la infraestructura física de las locaciones de CinePlus, incluyendo la administración de complejos cinematográficos, salas individuales y sus configuraciones de asientos.

---

## 🏗️ Estructura de Datos

### Entidad `Cinema` (Complejo Cinematográfico)

```java
@Entity
@Table(name = "cinemas")
public class Cinema {
    private Long id;
    private String name;              // Nombre del complejo
    private String city;              // Ciudad
    private String address;           // Dirección física
    private String location;          // Coordenadas o referencia
    private List<String> availableFormats; // Formatos disponibles (2D, 3D, XD)
    private String image;             // URL de imagen del complejo
    private Set<Theater> theaters;    // Salas del complejo
}
```

### Entidad `Theater` (Sala de Cine)

```java
@Entity
@Table(name = "theaters")
public class Theater {
    private Long id;
    private Cinema cinema;            // Complejo al que pertenece
    private String name;              // Nombre de la sala (ej: "Sala 1", "Sala XD")
    private SeatMatrixType seatMatrixType; // Tipo de matriz de asientos
    private int rowCount;             // Número de filas
    private int colCount;             // Número de columnas
    private int totalSeats;           // Total de asientos
}
```

### Enum `SeatMatrixType`

```java
public enum SeatMatrixType {
    SMALL,   // Sala pequeña
    MEDIUM,  // Sala mediana
    LARGE,   // Sala grande
    XLARGE   // Sala extra grande
}
```

---

## 🔗 Relaciones

```
Cinema (1) ─────── (N) Theater
   │
   └─ availableFormats: List<String>
   
Theater
   │
   ├─ Pertenece a: Cinema (ManyToOne)
   └─ Configuración: seatMatrixType, rowCount, colCount
```

---

## 🔌 API Endpoints

### **Cinemas**

#### Listar Todos los Cines
```http
GET /api/cinemas
```
**Respuesta:**
```json
[
  {
    "id": 1,
    "name": "CinePlus Jockey Plaza",
    "city": "Lima",
    "address": "Av. Javier Prado Este 4200",
    "location": "-12.0897,-77.0087",
    "availableFormats": ["2D", "3D", "XD"],
    "image": "https://example.com/cinemas/jockey.jpg"
  }
]
```

#### Obtener Cine por ID
```http
GET /api/cinemas/{id}
```

#### Crear Cine (ADMIN)
```http
POST /api/cinemas
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "CinePlus San Miguel",
  "city": "Lima",
  "address": "Av. La Marina 2000",
  "location": "-12.0700,-77.0800",
  "availableFormats": ["2D", "3D"],
  "image": "https://example.com/cinemas/sanmiguel.jpg"
}
```

#### Actualizar Cine (ADMIN)
```http
PUT /api/cinemas/{id}
Authorization: Bearer {token}
Content-Type: application/json
```

#### Eliminar Cine (ADMIN)
```http
DELETE /api/cinemas/{id}
Authorization: Bearer {token}
```

---

### **Theaters (Salas)**

#### Listar Todas las Salas
```http
GET /api/theaters
```

#### Listar Salas por Cinema
```http
GET /api/theaters?cinemaId={cinemaId}
```
**Respuesta:**
```json
[
  {
    "id": 1,
    "cinemaId": 1,
    "name": "Sala 1",
    "seatMatrixType": "MEDIUM",
    "rowCount": 10,
    "colCount": 12,
    "totalSeats": 120
  },
  {
    "id": 2,
    "cinemaId": 1,
    "name": "Sala XD",
    "seatMatrixType": "LARGE",
    "rowCount": 15,
    "colCount": 20,
    "totalSeats": 300
  }
]
```

#### Obtener Sala por ID
```http
GET /api/theaters/{id}
```

#### Crear Sala (ADMIN)
```http
POST /api/theaters
Authorization: Bearer {token}
Content-Type: application/json

{
  "cinemaId": 1,
  "name": "Sala 3D Premium",
  "seatMatrixType": "LARGE",
  "rowCount": 12,
  "colCount": 16,
  "totalSeats": 192
}
```

#### Actualizar Sala (ADMIN)
```http
PUT /api/theaters/{id}
Authorization: Bearer {token}
```

#### Eliminar Sala (ADMIN)
```http
DELETE /api/theaters/{id}
Authorization: Bearer {token}
```

---

## 📐 Configuración de Asientos

### Tipos de Matriz de Asientos

| Tipo | Filas Sugeridas | Columnas Sugeridas | Total Aprox. |
|------|-----------------|---------------------|--------------|
| **SMALL** | 6-8 | 8-10 | 48-80 |
| **MEDIUM** | 9-12 | 10-14 | 90-168 |
| **LARGE** | 13-16 | 15-20 | 195-320 |
| **XLARGE** | 17-20+ | 20-25+ | 340-500+ |

### Cálculo de Capacidad
```java
totalSeats = rowCount × colCount
```

---

## 💡 Ejemplos de Uso

### Ejemplo 1: Crear Complejo con Múltiples Formatos
```json
{
  "name": "CinePlus Mega Plaza",
  "city": "Lima",
  "address": "Av. Alfredo Mendiola 3698",
  "location": "-11.9895,-77.0574",
  "availableFormats": ["2D", "3D", "XD", "IMAX"],
  "image": "https://example.com/cinemas/megaplaza.jpg"
}
```

### Ejemplo 2: Crear Sala Estándar
```json
{
  "cinemaId": 1,
  "name": "Sala 2",
  "seatMatrixType": "MEDIUM",
  "rowCount": 10,
  "colCount": 12,
  "totalSeats": 120
}
```

### Ejemplo 3: Crear Sala Premium XD
```json
{
  "cinemaId": 1,
  "name": "Sala XD Premium",
  "seatMatrixType": "LARGE",
  "rowCount": 14,
  "colCount": 18,
  "totalSeats": 252
}
```

---

## 🔄 Flujo de Creación

```
┌──────────────────────────────────────────┐
│ 1. Admin crea Cinema (Complejo)         │
│    - Define ubicación y formatos        │
└────────────────┬─────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────┐
│ 2. Admin crea Theaters (Salas)          │
│    - Asocia al Cinema                    │
│    - Define matriz de asientos           │
└────────────────┬─────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────┐
│ 3. Sistema calcula totalSeats           │
│    totalSeats = rowCount × colCount      │
└────────────────┬─────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────┐
│ 4. Salas listas para Showtimes          │
│    (Funciones de películas)              │
└──────────────────────────────────────────┘
```

---

## 🔒 Seguridad y Permisos

| Acción | Rol Requerido | Endpoint |
|--------|---------------|----------|
| Listar cines | Público | `GET /api/cinemas` |
| Ver cine específico | Público | `GET /api/cinemas/{id}` |
| Listar salas | Público | `GET /api/theaters` |
| Ver sala específica | Público | `GET /api/theaters/{id}` |
| Crear cine | `ADMIN` | `POST /api/cinemas` |
| Actualizar cine | `ADMIN` | `PUT /api/cinemas/{id}` |
| Eliminar cine | `ADMIN` | `DELETE /api/cinemas/{id}` |
| Crear sala | `ADMIN` | `POST /api/theaters` |
| Actualizar sala | `ADMIN` | `PUT /api/theaters/{id}` |
| Eliminar sala | `ADMIN` | `DELETE /api/theaters/{id}` |

---

## 📊 Consultas Útiles

### Obtener todas las salas de un cine
```sql
SELECT * FROM theaters 
WHERE cinema_id = 1 
ORDER BY name;
```

### Calcular capacidad total de un cine
```sql
SELECT 
    c.name as cinema_name,
    COUNT(t.id) as total_theaters,
    SUM(t.total_seats) as total_capacity
FROM cinemas c
LEFT JOIN theaters t ON c.id = t.cinema_id
GROUP BY c.id;
```

### Salas por tipo de matriz
```sql
SELECT seat_matrix_type, COUNT(*) as count
FROM theaters
GROUP BY seat_matrix_type;
```

---

## 🛡️ Validaciones

### Validaciones al Crear Cinema
- ✅ `name` no puede estar vacío
- ✅ `city` opcional pero recomendado
- ✅ `availableFormats` debe contener al menos un formato válido

### Validaciones al Crear Theater
- ✅ `cinemaId` debe existir
- ✅ `name` no puede estar vacío
- ✅ `rowCount` y `colCount` deben ser > 0
- ✅ `totalSeats` = `rowCount × colCount`
- ✅ `seatMatrixType` debe ser válido (SMALL, MEDIUM, LARGE, XLARGE)

---

## 🚀 Mejoras Futuras

1. **Mapas de Asientos Personalizados**
   - Asientos con descuento (laterales)
   - Asientos premium (centro)
   - Espacios para discapacitados

2. **Servicios por Sala**
   - Butacas reclinables
   - Sistema de sonido específico
   - Dimensiones de pantalla

3. **Horarios de Operación**
   - Horarios específicos por cine
   - Días festivos
   - Mantenimiento programado

4. **Integración con Mapas**
   - Google Maps API
   - Cómo llegar
   - Estacionamiento disponible

5. **Estadísticas**
   - Salas más populares
   - Tasa de ocupación promedio
   - Ingresos por sala

---

## 📚 Referencias

- **Entidad Cinema**: `domain/entity/Cinema.java`
- **Entidad Theater**: `domain/entity/Theater.java`
- **DTO Cinema**: `domain/dto/CinemaDto.java`
- **DTO Theater**: `domain/dto/TheaterDto.java`
- **Service Cinema**: `persistence/service/impl/CinemaServiceImpl.java`
- **Service Theater**: `persistence/service/impl/TheaterServiceImpl.java`
- **Controller Cinema**: `web/controller/CinemaController.java`
- **Controller Theater**: `web/controller/TheaterController.java`

---

**Última actualización:** Diciembre 2025  
**Versión:** 1.0
