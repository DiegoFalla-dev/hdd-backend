# Sistema de Confitería (Concesiones) - CinePlus

## 📋 Descripción General

El sistema de confitería gestiona los productos de dulcería disponibles en los cines, incluyendo combos, canchita (palomitas), bebidas y snacks. Los productos pueden estar disponibles en múltiples cines.

---

## 🏗️ Estructura de Datos

### Entidad `ConcessionProduct`

```java
@Entity
@Table(name = "concession_products")
public class ConcessionProduct {
    private Long id;
    private String name;                // Nombre del producto
    private String description;         // Descripción
    private BigDecimal price;           // Precio
    private String imageUrl;            // URL de imagen del producto
    private ProductCategory category;   // Categoría
    private Set<Cinema> cinemas;        // Cines donde está disponible
}
```

### Enum `ProductCategory` (Categoría de Producto)

```java
public enum ProductCategory {
    COMBOS,     // Combos (canchita + bebida + extras)
    CANCHITA,   // Palomitas de maíz
    BEBIDAS,    // Bebidas (gaseosas, agua, jugos)
    SNACKS      // Snacks (chocolates, nachos, dulces)
}
```

---

## 🔗 Relaciones

```
ConcessionProduct (N) ─────── (N) Cinema
              │
              └─ Relación Many-to-Many
                 Tabla: cinema_product
                 
OrderConcession (Línea de Orden)
   │
   ├─ order: Order
   ├─ product: Product
   ├─ quantity: Integer
   └─ totalPrice: BigDecimal
```

---

## 🔌 API Endpoints

### Listar Productos por Cine
```http
GET /api/concessions?cinema={cinemaId}
```
**Respuesta:**
```json
[
  {
    "id": 1,
    "name": "Combo Grande",
    "description": "Canchita grande + 2 bebidas grandes",
    "price": 18.00,
    "imageUrl": "https://cdn.example.com/combo-grande.jpg",
    "category": "COMBOS"
  },
  {
    "id": 2,
    "name": "Canchita Mediana",
    "description": "Canchita mediana con mantequilla",
    "price": 8.50,
    "imageUrl": "https://cdn.example.com/canchita-mediana.jpg",
    "category": "CANCHITA"
  },
  {
    "id": 3,
    "name": "Coca-Cola Grande",
    "description": "Gaseosa Coca-Cola 1L",
    "price": 6.00,
    "imageUrl": "https://cdn.example.com/coca-cola.jpg",
    "category": "BEBIDAS"
  }
]
```

### Listar Productos por Cine y Categoría
```http
GET /api/concessions?cinema={cinemaId}&category={category}
```

**Ejemplos:**
```http
GET /api/concessions?cinema=1&category=COMBOS
GET /api/concessions?cinema=1&category=BEBIDAS
GET /api/concessions?cinema=2&category=SNACKS
```

### Obtener Producto por ID
```http
GET /api/concessions/{id}
```
**Respuesta:**
```json
{
  "id": 1,
  "name": "Combo Grande",
  "description": "Canchita grande + 2 bebidas grandes + nachos",
  "price": 18.00,
  "imageUrl": "https://cdn.example.com/combo-grande.jpg",
  "category": "COMBOS",
  "availableInCinemas": [1, 2, 3]
}
```

### Crear Producto (ADMIN)
```http
POST /api/concessions
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Combo Familiar",
  "description": "2 canchitas grandes + 4 bebidas + nachos grandes",
  "price": 35.00,
  "imageUrl": "https://cdn.example.com/combo-familiar.jpg",
  "category": "COMBOS",
  "cinemaIds": [1, 2, 3, 4]
}
```

### Actualizar Producto (ADMIN)
```http
PUT /api/concessions/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Combo Familiar",
  "description": "2 canchitas grandes + 4 bebidas grandes + 2 nachos",
  "price": 38.00,
  "imageUrl": "https://cdn.example.com/combo-familiar-v2.jpg",
  "category": "COMBOS",
  "cinemaIds": [1, 2, 3, 4, 5]
}
```

### Eliminar Producto (ADMIN)
```http
DELETE /api/concessions/{id}
Authorization: Bearer {token}
```

---

## 📦 Categorías de Productos

### COMBOS
Combinaciones de productos con precio promocional.

**Ejemplos:**
- Combo Pequeño: Canchita pequeña + bebida pequeña
- Combo Mediano: Canchita mediana + bebida mediana
- Combo Grande: Canchita grande + 2 bebidas grandes
- Combo Familiar: 2 canchitas grandes + 4 bebidas + nachos
- Combo Premium: Canchita XL + bebidas + nachos + chocolates

### CANCHITA (Palomitas)
Canchita en diferentes tamaños y sabores.

**Tamaños:**
- Pequeña (Small)
- Mediana (Medium)
- Grande (Large)
- Extra Grande (XL)

**Sabores:**
- Mantequilla (Butter)
- Caramelo (Caramel)
- Queso (Cheese)
- Natural (Plain)

### BEBIDAS
Bebidas frías y calientes.

**Tipos:**
- Gaseosas (Coca-Cola, Inka Kola, Sprite, Fanta)
- Agua mineral
- Jugos
- Café
- Bebidas energéticas

**Tamaños:**
- Pequeño (Small) - 500ml
- Mediano (Medium) - 750ml
- Grande (Large) - 1L

### SNACKS
Snacks y dulces variados.

**Productos:**
- Nachos con queso
- Hot dogs
- Chocolates (Snickers, M&Ms, Kit Kat)
- Gomitas
- Chicles
- Papas fritas

---

## 💰 Estrategia de Precios

### Precios Sugeridos (en Soles Peruanos)

| Producto | Precio |
|----------|--------|
| **COMBOS** |
| Combo Pequeño | S/ 12.00 |
| Combo Mediano | S/ 15.00 |
| Combo Grande | S/ 18.00 |
| Combo Familiar | S/ 35.00 |
| Combo Premium | S/ 42.00 |
| **CANCHITA** |
| Canchita Pequeña | S/ 6.00 |
| Canchita Mediana | S/ 8.50 |
| Canchita Grande | S/ 11.00 |
| Canchita XL | S/ 14.00 |
| **BEBIDAS** |
| Bebida Pequeña | S/ 4.00 |
| Bebida Mediana | S/ 5.50 |
| Bebida Grande | S/ 7.00 |
| Agua Mineral | S/ 3.00 |
| Café | S/ 5.00 |
| **SNACKS** |
| Nachos con Queso | S/ 9.00 |
| Hot Dog | S/ 7.50 |
| Chocolate | S/ 4.00 |
| Gomitas | S/ 3.50 |
| Papas Fritas | S/ 5.00 |

---

## 🛍️ Integración con Órdenes

### Agregar Productos a una Orden

Al crear una orden (`POST /api/orders`), se incluyen productos de confitería:

```json
{
  "userId": 25,
  "paymentMethodId": 5,
  "items": [
    {
      "showtimeId": 301,
      "seatId": 1205,
      "ticketType": "ADULT",
      "price": 28.00
    }
  ],
  "concessions": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 15,
      "quantity": 1
    }
  ]
}
```

### Cálculo de Subtotal de Confitería

```
Por cada producto:
  totalPrice = unitPrice × quantity

Subtotal Confitería = Σ(totalPrice)
```

**Ejemplo:**
```
Producto 1: Combo Grande × 2 = S/ 18.00 × 2 = S/ 36.00
Producto 2: Nachos × 1 = S/ 9.00 × 1 = S/ 9.00

Subtotal Confitería = S/ 36.00 + S/ 9.00 = S/ 45.00
```

---

## 💡 Ejemplos de Uso

### Ejemplo 1: Combo Básico
```json
{
  "name": "Combo Básico",
  "description": "Canchita mediana + bebida mediana",
  "price": 15.00,
  "imageUrl": "https://cdn.example.com/combo-basico.jpg",
  "category": "COMBOS",
  "cinemaIds": [1, 2, 3]
}
```

### Ejemplo 2: Canchita Premium
```json
{
  "name": "Canchita Caramelo XL",
  "description": "Canchita extra grande con caramelo",
  "price": 16.00,
  "imageUrl": "https://cdn.example.com/canchita-caramelo-xl.jpg",
  "category": "CANCHITA",
  "cinemaIds": [1, 2, 3, 4, 5]
}
```

### Ejemplo 3: Snack Especial
```json
{
  "name": "Nachos Supreme",
  "description": "Nachos grandes con queso cheddar, guacamole y jalapeños",
  "price": 12.50,
  "imageUrl": "https://cdn.example.com/nachos-supreme.jpg",
  "category": "SNACKS",
  "cinemaIds": [1, 3, 5]
}
```

### Ejemplo 4: Bebida Especial
```json
{
  "name": "Smoothie de Fresa",
  "description": "Smoothie natural de fresa con yogurt",
  "price": 8.50,
  "imageUrl": "https://cdn.example.com/smoothie-fresa.jpg",
  "category": "BEBIDAS",
  "cinemaIds": [2, 4]
}
```

---

## 🔄 Flujo de Compra con Confitería

```
┌─────────────────────────────────────────────────┐
│ 1. Usuario selecciona película y función       │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 2. Usuario selecciona asientos                 │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 3. Sistema pregunta: "¿Desea añadir confitería?"│
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 4. Usuario navega productos por categoría      │
│    GET /api/concessions?cinema=1&category=...   │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 5. Usuario agrega productos al carrito         │
│    - Combo Grande × 2                           │
│    - Nachos × 1                                 │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 6. Sistema calcula subtotal confitería         │
│    Subtotal = Σ(precio × cantidad)             │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 7. Usuario procede al pago                     │
│    Total = Subtotal Tickets + Subtotal Confitería│
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 8. Orden creada con OrderConcessions           │
└─────────────────────────────────────────────────┘
```

---

## 🔒 Seguridad y Permisos

| Acción | Rol Requerido | Endpoint |
|--------|---------------|----------|
| Listar productos | Público | `GET /api/concessions` |
| Ver producto específico | Público | `GET /api/concessions/{id}` |
| Filtrar por categoría | Público | `GET /api/concessions?category=...` |
| Crear producto | `ADMIN` | `POST /api/concessions` |
| Actualizar producto | `ADMIN` | `PUT /api/concessions/{id}` |
| Eliminar producto | `ADMIN` | `DELETE /api/concessions/{id}` |

---

## 🛡️ Validaciones

### Validaciones al Crear/Actualizar
- ✅ `name` no puede estar vacío
- ✅ `description` opcional pero recomendado
- ✅ `price` debe ser > 0
- ✅ `category` debe ser válida (COMBOS, CANCHITA, BEBIDAS, SNACKS)
- ✅ Al menos un `cinemaId` debe proporcionarse
- ✅ Todos los `cinemaIds` deben existir

### Validaciones en Órdenes
- ✅ `productId` debe existir
- ✅ Producto debe estar disponible en el cinema de la función
- ✅ `quantity` debe ser > 0
- ✅ Precio unitario se obtiene del producto (no confiable desde frontend)

---

## 📊 Consultas Útiles

### Productos más vendidos
```sql
SELECT 
    cp.name,
    cp.category,
    SUM(oc.quantity) as total_sold,
    SUM(oc.total_price) as total_revenue
FROM concession_products cp
JOIN order_concessions oc ON cp.id = oc.product_id
JOIN orders o ON oc.order_id = o.id
WHERE o.order_status = 'COMPLETED'
GROUP BY cp.id
ORDER BY total_sold DESC
LIMIT 10;
```

### Productos por categoría
```sql
SELECT category, COUNT(*) as product_count
FROM concession_products
GROUP BY category;
```

### Ingresos por categoría
```sql
SELECT 
    cp.category,
    SUM(oc.total_price) as revenue
FROM concession_products cp
JOIN order_concessions oc ON cp.id = oc.product_id
JOIN orders o ON oc.order_id = o.id
WHERE o.order_status = 'COMPLETED'
GROUP BY cp.category;
```

### Productos disponibles por cine
```sql
SELECT 
    c.name as cinema_name,
    COUNT(cpr.product_id) as product_count
FROM cinemas c
LEFT JOIN cinema_product cpr ON c.id = cpr.cinema_id
GROUP BY c.id;
```

---

## 🚀 Mejoras Futuras

1. **Personalización de Productos**
   - Selección de sabores
   - Tamaños personalizados
   - Extras opcionales (mantequilla extra, sal, etc.)

2. **Combos Dinámicos**
   - Permitir al usuario armar su propio combo
   - Descuentos automáticos por volumen

3. **Inventario**
   - Control de stock por cine
   - Productos agotados no disponibles para compra
   - Alertas de reabastecimiento

4. **Promociones Específicas**
   - Descuentos en productos específicos
   - "Compra 2, lleva 3"
   - Happy hour (descuentos en horarios específicos)

5. **Recomendaciones**
   - "Otros usuarios también compraron..."
   - Productos populares de la película
   - Sugerencias basadas en historial

6. **Pedidos Anticipados**
   - Pre-ordenar confitería al comprar tickets
   - Recoger en cine con código QR
   - Reducir tiempo de espera en fila

7. **Información Nutricional**
   - Calorías
   - Ingredientes
   - Alérgenos
   - Opciones vegetarianas/veganas

8. **Ratings y Reseñas**
   - Calificación de productos
   - Comentarios de usuarios
   - Productos mejor valorados

---

## 🎨 Diseño de Imágenes

### imageUrl
- **Dimensiones sugeridas**: 400x400px (cuadrado)
- **Formato**: JPG, PNG, WEBP
- **Peso máximo**: 200KB
- **Fondo**: Blanco o transparente
- **Contenido**: Foto del producto con buena iluminación

---

## 📚 Referencias

- **Entidad**: `domain/entity/ConcessionProduct.java`
- **Entidad OrderConcession**: `domain/entity/OrderConcession.java`
- **DTO**: `domain/dto/ConcessionProductDto.java`
- **Service**: `persistence/service/impl/ConcessionProductServiceImpl.java`
- **Controller**: `web/controller/ConcessionProductController.java`
- **Enum**: `domain/entity/ConcessionProduct.ProductCategory`

---

## ❓ Preguntas Frecuentes

### ¿Los precios incluyen IGV?
Sí, todos los precios mostrados incluyen impuestos.

### ¿Puedo comprar solo confitería sin tickets?
No, actualmente las confiterías solo se pueden agregar a una orden que incluye tickets.

### ¿Todos los productos están en todos los cines?
No, cada producto tiene una lista de cines donde está disponible.

### ¿Puedo devolver un producto de confitería?
Solo si no ha sido preparado/entregado. Contactar personal del cine.

### ¿Los combos se pueden personalizar?
Actualmente no, pero es una mejora futura planeada.

---

**Última actualización:** Diciembre 2025  
**Versión:** 1.0
