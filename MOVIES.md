# Sistema de Películas - CinePlus

## 📋 Descripción General

El sistema de películas gestiona el catálogo completo de films disponibles en CinePlus, incluyendo información detallada, clasificación, estado de exhibición y contenido multimedia asociado.

---

## 🏗️ Estructura de Datos

### Entidad `Movie`

```java
@Entity
@Table(name = "movies")
public class Movie {
    private Long id;
    private String title;             // Título de la película
    private String synopsis;          // Sinopsis/descripción
    private String genre;             // Género (Acción, Comedia, Drama, etc.)
    private String classification;    // Clasificación (G, PG, PG-13, R, NC-17)
    private String duration;          // Duración (ej: "1h 45m", "2h 15m")
    private String cardImageUrl;      // Imagen para tarjeta/card
    private String bannerUrl;         // Imagen de banner
    private String trailerUrl;        // URL del trailer (YouTube, Vimeo, etc.)
    private List<String> cast;        // Elenco principal
    private List<String> showtimes;   // Horarios disponibles (legacy)
    private MovieStatus status;       // Estado de la película
}
```

### Enum `MovieStatus`

```java
public enum MovieStatus {
    CARTELERA,  // En cartelera (Now Playing)
    PROXIMO,    // Próximamente (Coming Soon)
    PREVENTA    // En preventa (Presale)
}
```

---

## 🎬 Estados de Película

### CARTELERA (Now Playing)
Películas actualmente en exhibición con funciones disponibles para compra inmediata.

### PROXIMO (Coming Soon)
Películas que se estrenarán próximamente. Los usuarios pueden ver información pero no comprar tickets.

### PREVENTA (Presale)
Películas en preventa donde los usuarios pueden comprar tickets antes del estreno oficial.

---

## 🎨 Clasificaciones

| Código | Nombre | Descripción |
|--------|--------|-------------|
| **G** | General Audiences | Apto para todas las edades |
| **PG** | Parental Guidance | Se sugiere orientación de los padres |
| **PG-13** | Parents Strongly Cautioned | Inadecuado para menores de 13 años |
| **R** | Restricted | Menores de 17 requieren acompañante adulto |
| **NC-17** | Adults Only | Solo para adultos (18+) |

---

## 🔌 API Endpoints

### Listar Todas las Películas
```http
GET /api/movies
```
**Parámetros opcionales:**
- `status`: Filtrar por estado (CARTELERA, PROXIMO, PREVENTA)
- `genre`: Filtrar por género
- `q`: Búsqueda por texto (título, sinopsis)
- `page`: Número de página (default: 0)
- `size`: Tamaño de página (default: 20)

**Respuesta:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Avengers: Endgame",
      "synopsis": "Después de los eventos devastadores...",
      "genre": "Acción/Aventura",
      "classification": "PG-13",
      "duration": "3h 1m",
      "cardImageUrl": "https://example.com/movies/avengers-card.jpg",
      "bannerUrl": "https://example.com/movies/avengers-banner.jpg",
      "trailerUrl": "https://youtube.com/watch?v=...",
      "cast": ["Robert Downey Jr.", "Chris Evans", "Scarlett Johansson"],
      "status": "CARTELERA"
    }
  ],
  "totalElements": 50,
  "totalPages": 3,
  "size": 20,
  "number": 0
}
```

### Películas en Cartelera
```http
GET /api/movies/now-playing
```
Retorna todas las películas con estado `CARTELERA`.

### Películas Próximas
```http
GET /api/movies/upcoming
```
Retorna todas las películas con estado `PROXIMO`.

### Películas en Preventa
```http
GET /api/movies/presale
```
Retorna todas las películas con estado `PREVENTA`.

### Obtener Película por ID
```http
GET /api/movies/{id}
```
**Respuesta:**
```json
{
  "id": 1,
  "title": "Spider-Man: No Way Home",
  "synopsis": "Peter Parker es desenmascarado y ya no puede separar su vida normal...",
  "genre": "Acción/Superhéroes",
  "classification": "PG-13",
  "duration": "2h 28m",
  "cardImageUrl": "https://example.com/movies/spiderman-card.jpg",
  "bannerUrl": "https://example.com/movies/spiderman-banner.jpg",
  "trailerUrl": "https://youtube.com/watch?v=...",
  "cast": [
    "Tom Holland",
    "Zendaya",
    "Benedict Cumberbatch",
    "Jacob Batalon"
  ],
  "status": "CARTELERA"
}
```

### Crear Película (ADMIN)
```http
POST /api/movies
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "The Batman",
  "synopsis": "Cuando un asesino se dirige a la élite de Gotham...",
  "genre": "Acción/Thriller",
  "classification": "PG-13",
  "duration": "2h 56m",
  "cardImageUrl": "https://example.com/movies/batman-card.jpg",
  "bannerUrl": "https://example.com/movies/batman-banner.jpg",
  "trailerUrl": "https://youtube.com/watch?v=...",
  "cast": ["Robert Pattinson", "Zoë Kravitz", "Paul Dano"],
  "status": "PREVENTA"
}
```

### Actualizar Película (ADMIN)
```http
PUT /api/movies/{id}
Authorization: Bearer {token}
Content-Type: application/json
```

### Eliminar Película (ADMIN)
```http
DELETE /api/movies/{id}
Authorization: Bearer {token}
```

---

## 🔍 Búsqueda y Filtrado

### Búsqueda por Texto
```http
GET /api/movies?q=avengers
```
Busca en título y sinopsis.

### Filtro por Estado
```http
GET /api/movies?status=CARTELERA
```

### Filtro por Género
```http
GET /api/movies?genre=Acción
```

### Combinación de Filtros
```http
GET /api/movies?status=CARTELERA&genre=Comedia&page=0&size=10
```

### Paginación
```http
GET /api/movies?page=1&size=15
```

---

## 💡 Ejemplos de Uso

### Ejemplo 1: Película de Acción en Cartelera
```json
{
  "title": "Top Gun: Maverick",
  "synopsis": "Después de más de 30 años de servicio...",
  "genre": "Acción/Drama",
  "classification": "PG-13",
  "duration": "2h 11m",
  "cardImageUrl": "https://cdn.example.com/topgun-card.jpg",
  "bannerUrl": "https://cdn.example.com/topgun-banner.jpg",
  "trailerUrl": "https://youtube.com/watch?v=giXco2jaZ_4",
  "cast": [
    "Tom Cruise",
    "Miles Teller",
    "Jennifer Connelly",
    "Jon Hamm"
  ],
  "status": "CARTELERA"
}
```

### Ejemplo 2: Película Infantil Próxima
```json
{
  "title": "Toy Story 5",
  "synopsis": "Una nueva aventura espera a Woody y Buzz...",
  "genre": "Animación/Familia",
  "classification": "G",
  "duration": "1h 40m",
  "cardImageUrl": "https://cdn.example.com/toystory5-card.jpg",
  "bannerUrl": "https://cdn.example.com/toystory5-banner.jpg",
  "trailerUrl": "https://youtube.com/watch?v=...",
  "cast": [
    "Tom Hanks (voz)",
    "Tim Allen (voz)",
    "Annie Potts (voz)"
  ],
  "status": "PROXIMO"
}
```

### Ejemplo 3: Película de Terror en Preventa
```json
{
  "title": "The Conjuring 4",
  "synopsis": "Ed y Lorraine Warren investigan su caso más aterrador...",
  "genre": "Terror/Suspenso",
  "classification": "R",
  "duration": "2h 5m",
  "cardImageUrl": "https://cdn.example.com/conjuring4-card.jpg",
  "bannerUrl": "https://cdn.example.com/conjuring4-banner.jpg",
  "trailerUrl": "https://youtube.com/watch?v=...",
  "cast": [
    "Patrick Wilson",
    "Vera Farmiga"
  ],
  "status": "PREVENTA"
}
```

---

## 🔄 Ciclo de Vida de una Película

```
┌──────────────────────────────────────────┐
│ 1. PROXIMO (Próximamente)               │
│    - Se anuncia la película             │
│    - Usuarios pueden ver info            │
│    - NO se pueden comprar tickets        │
└────────────────┬─────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────┐
│ 2. PREVENTA (Presale)                    │
│    - Venta anticipada de tickets        │
│    - Funciones programadas               │
│    - Descuentos especiales (opcional)    │
└────────────────┬─────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────┐
│ 3. CARTELERA (Now Playing)               │
│    - Exhibición normal                   │
│    - Múltiples funciones disponibles     │
│    - Compra inmediata de tickets         │
└────────────────┬─────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────┐
│ 4. Archivado/Eliminado                   │
│    - Película fuera de cartelera         │
│    - Histórico de compras mantenido      │
└──────────────────────────────────────────┘
```

---

## 🎭 Géneros Comunes

- **Acción**: Películas con mucha acción física
- **Animación**: Películas animadas (2D/3D/Stop-motion)
- **Aventura**: Historias de exploración y descubrimiento
- **Ciencia Ficción**: Futuros alternativos, tecnología avanzada
- **Comedia**: Películas humorísticas
- **Drama**: Historias serias y emocionales
- **Familia**: Aptas para toda la familia
- **Terror**: Películas de miedo y suspenso
- **Romance**: Historias de amor
- **Thriller**: Suspenso y tensión
- **Superhéroes**: Basadas en cómics de superhéroes
- **Musical**: Con canciones y baile

---

## 🔒 Seguridad y Permisos

| Acción | Rol Requerido | Endpoint |
|--------|---------------|----------|
| Listar películas | Público | `GET /api/movies` |
| Ver película específica | Público | `GET /api/movies/{id}` |
| Películas en cartelera | Público | `GET /api/movies/now-playing` |
| Películas próximas | Público | `GET /api/movies/upcoming` |
| Películas en preventa | Público | `GET /api/movies/presale` |
| Buscar películas | Público | `GET /api/movies?q={query}` |
| Crear película | `ADMIN` | `POST /api/movies` |
| Actualizar película | `ADMIN` | `PUT /api/movies/{id}` |
| Eliminar película | `ADMIN` | `DELETE /api/movies/{id}` |

---

## 🛡️ Validaciones

### Validaciones al Crear/Actualizar
- ✅ `title` no puede estar vacío
- ✅ `genre` no puede estar vacío
- ✅ `classification` debe ser válida (G, PG, PG-13, R, NC-17)
- ✅ `duration` debe seguir formato "Xh Ym" o similar
- ✅ `status` debe ser válido (CARTELERA, PROXIMO, PREVENTA)
- ✅ URLs deben ser válidas (opcional)
- ✅ `cast` puede estar vacío pero se recomienda al menos 1 actor

---

## 📊 Consultas Útiles

### Películas más populares (por número de funciones)
```sql
SELECT m.title, COUNT(s.id) as showtime_count
FROM movies m
LEFT JOIN showtimes s ON m.id = s.movie_id
WHERE m.status = 'CARTELERA'
GROUP BY m.id
ORDER BY showtime_count DESC
LIMIT 10;
```

### Películas por género
```sql
SELECT genre, COUNT(*) as count
FROM movies
WHERE status = 'CARTELERA'
GROUP BY genre
ORDER BY count DESC;
```

### Películas próximas a estrenar
```sql
SELECT title, status
FROM movies
WHERE status IN ('PROXIMO', 'PREVENTA')
ORDER BY title;
```

### Duración promedio por género
```sql
SELECT 
    genre,
    AVG(CAST(SUBSTRING_INDEX(duration, 'h', 1) AS UNSIGNED) * 60 + 
        CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(duration, 'm', 1), ' ', -1) AS UNSIGNED)) as avg_minutes
FROM movies
GROUP BY genre;
```

---

## 🚀 Mejoras Futuras

1. **Calificaciones y Reseñas**
   - Sistema de puntuación (1-5 estrellas)
   - Comentarios de usuarios
   - Críticas profesionales

2. **Información Extendida**
   - Director
   - Productores
   - Premios ganados
   - País de origen
   - Idiomas disponibles
   - Subtítulos disponibles

3. **Contenido Multimedia**
   - Múltiples trailers
   - Imágenes behind the scenes
   - Galería de fotos
   - Clips exclusivos

4. **Recomendaciones**
   - Películas similares
   - "Si te gustó X, te recomendamos Y"
   - Basado en historial de compras

5. **Etiquetas y Categorías**
   - Basada en libro
   - Secuela/Precuela
   - Remake
   - Película original

6. **Integración con APIs Externas**
   - TMDB (The Movie Database)
   - IMDb
   - Rotten Tomatoes
   - Actualización automática de información

7. **Estadísticas Avanzadas**
   - Películas más vistas
   - Ingresos por película
   - Ocupación promedio
   - Horarios más populares

---

## 📱 Campos de Imagen

### cardImageUrl
- **Uso**: Tarjeta de película en listados
- **Dimensiones sugeridas**: 300x450px (aspect ratio 2:3)
- **Formato**: JPG, PNG, WEBP

### bannerUrl
- **Uso**: Banner en página de detalle
- **Dimensiones sugeridas**: 1920x600px (aspect ratio 16:5)
- **Formato**: JPG, PNG, WEBP

### trailerUrl
- **Uso**: Video promocional
- **Formatos aceptados**: 
  - YouTube: `https://youtube.com/watch?v=...`
  - Vimeo: `https://vimeo.com/...`
  - URL directa: `.mp4`, `.webm`

---

## 📚 Referencias

- **Entidad**: `domain/entity/Movie.java`
- **DTO**: `domain/dto/MovieDto.java`
- **Enum Status**: `domain/entity/MovieStatus.java`
- **Service**: `persistence/service/impl/MovieServiceImpl.java`
- **Controller**: `web/controller/MovieController.java`
- **Repository**: `domain/repository/MovieRepository.java`

---

## ❓ Preguntas Frecuentes

### ¿Puedo tener una película en múltiples estados?
No, cada película solo puede tener un estado a la vez (CARTELERA, PROXIMO, o PREVENTA).

### ¿Cómo cambio una película de PREVENTA a CARTELERA?
Actualiza el campo `status` usando el endpoint `PUT /api/movies/{id}`.

### ¿El campo showtimes todavía se usa?
Es legacy. Las funciones ahora se gestionan con la entidad `Showtime` independiente.

### ¿Puedo eliminar una película que tiene tickets vendidos?
Técnicamente sí, pero se recomienda cambiar el estado en lugar de eliminar para mantener el historial.

### ¿Cómo subo las imágenes?
Las URLs apuntan a imágenes ya alojadas (CDN, S3, etc.). El sistema solo guarda las URLs.

---

**Última actualización:** Diciembre 2025  
**Versión:** 1.0
