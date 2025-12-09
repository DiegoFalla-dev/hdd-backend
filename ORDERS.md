# Sistema de Órdenes - CinePlus

## 📋 Descripción General

El sistema de órdenes gestiona las compras de tickets y productos de confitería, incluyendo el proceso de pago, generación de facturas, códigos QR y PDFs de tickets individuales.

---

## 🏗️ Estructura de Datos

### Entidad `Order` (Orden de Compra)

```java
@Entity
@Table(name = "orders")
public class Order {
    private Long id;
    private User user;                     // Usuario que realizó la compra
    private LocalDateTime orderDate;        // Fecha y hora de la orden
    private BigDecimal totalAmount;         // Monto total
    private PaymentMethod paymentMethod;    // Método de pago usado
    private OrderStatus orderStatus;        // Estado de la orden
    private String invoiceNumber;           // Número de factura único
    private String invoicePdfUrl;           // URL del PDF de factura
    private String qrCodeUrl;               // URL del QR general (opcional)
    private List<OrderItem> orderItems;     // Tickets individuales
    private List<OrderConcession> orderConcessions; // Productos de confitería
    private Promotion promotion;            // Promoción aplicada (opcional)
}
```

### Entidad `OrderItem` (Ticket Individual)

```java
@Entity
@Table(name = "order_items")
public class OrderItem {
    private Long id;
    private Order order;                // Orden a la que pertenece
    private Showtime showtime;          // Función
    private Seat seat;                  // Asiento específico
    private BigDecimal price;           // Precio del ticket
    private TicketStatus ticketStatus;  // Estado del ticket
    private String qrCodeTicketUrl;     // URL del QR del ticket
    private String ticketPdfUrl;        // URL del PDF del ticket
}
```

### Entidad `OrderConcession` (Producto de Confitería)

```java
@Entity
@Table(name = "order_concessions")
public class OrderConcession {
    private Long id;
    private Order order;                // Orden a la que pertenece
    private Product product;            // Producto comprado
    private Integer quantity;           // Cantidad
    private BigDecimal unitPrice;       // Precio unitario
    private BigDecimal totalPrice;      // Precio total (quantity × unitPrice)
}
```

### Enum `OrderStatus`

```java
public enum OrderStatus {
    PENDING,     // Pendiente de pago
    COMPLETED,   // Completada y pagada
    CANCELLED,   // Cancelada
    REFUNDED     // Reembolsada
}
```

### Enum `TicketStatus`

```java
public enum TicketStatus {
    VALID,       // Válido, no usado
    USED,        // Ya usado (entrada validada)
    CANCELLED    // Cancelado
}
```

---

## 🔗 Relaciones

```
User (1) ──────── (N) Order
PaymentMethod (1) ─ (N) Order
Promotion (1) ───── (N) Order
Order (1) ─────────┐
                   ├─── (N) OrderItem
                   └─── (N) OrderConcession

OrderItem
   │
   ├─ showtime: Showtime
   ├─ seat: Seat
   └─ price: BigDecimal
   
OrderConcession
   │
   ├─ product: Product
   ├─ quantity: Integer
   └─ totalPrice: BigDecimal
```

---

## 🔌 API Endpoints

### Listar Todas las Órdenes (ADMIN)
```http
GET /api/orders
Authorization: Bearer {admin-token}
```

### Obtener Orden por ID
```http
GET /api/orders/{id}
Authorization: Bearer {token}
```
**Permisos**: ADMIN o el usuario propietario de la orden.

**Respuesta:**
```json
{
  "id": 1001,
  "userId": 25,
  "orderDate": "2024-12-08T14:30:00",
  "totalAmount": 125.50,
  "paymentMethodId": 5,
  "orderStatus": "COMPLETED",
  "invoiceNumber": "INV-A7F3B21C",
  "invoicePdfUrl": "/invoices/INV-A7F3B21C.pdf",
  "orderItems": [
    {
      "id": 5001,
      "showtimeId": 301,
      "seatId": 1205,
      "seatIdentifier": "E8",
      "price": 28.00,
      "ticketStatus": "VALID",
      "qrCodeTicketUrl": "/qrcodes/ticket_5001.png",
      "ticketPdfUrl": "/pdfs/ticket_5001.pdf"
    },
    {
      "id": 5002,
      "showtimeId": 301,
      "seatId": 1206,
      "seatIdentifier": "E9",
      "price": 28.00,
      "ticketStatus": "VALID",
      "qrCodeTicketUrl": "/qrcodes/ticket_5002.png",
      "ticketPdfUrl": "/pdfs/ticket_5002.pdf"
    }
  ],
  "orderConcessions": [
    {
      "id": 2001,
      "productId": 10,
      "productName": "Combo Grande",
      "quantity": 2,
      "unitPrice": 18.00,
      "totalPrice": 36.00
    },
    {
      "id": 2002,
      "productId": 15,
      "productName": "Nachos",
      "quantity": 1,
      "unitPrice": 15.50,
      "totalPrice": 15.50
    }
  ],
  "promotionCode": "VERANO2024",
  "discountAmount": 12.00
}
```

### Listar Órdenes por Usuario
```http
GET /api/orders/user/{userId}
Authorization: Bearer {token}
```
**Permisos**: ADMIN o el propio usuario.

### Crear Orden
```http
POST /api/orders
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 25,
  "paymentMethodId": 5,
  "promotionCode": "VERANO2024",
  "items": [
    {
      "showtimeId": 301,
      "seatId": 1205,
      "ticketType": "ADULT",
      "price": 28.00
    },
    {
      "showtimeId": 301,
      "seatId": 1206,
      "ticketType": "ADULT",
      "price": 28.00
    }
  ],
  "concessions": [
    {
      "productId": 10,
      "quantity": 2
    },
    {
      "productId": 15,
      "quantity": 1
    }
  ]
}
```

**Respuesta:**
```json
{
  "id": 1001,
  "userId": 25,
  "orderDate": "2024-12-08T14:30:00",
  "totalAmount": 125.50,
  "orderStatus": "COMPLETED",
  "invoiceNumber": "INV-A7F3B21C",
  "message": "Orden creada exitosamente"
}
```

### Actualizar Estado de Orden (ADMIN)
```http
PATCH /api/orders/{id}/status?newStatus=REFUNDED
Authorization: Bearer {admin-token}
```

---

## 📄 Generación de Documentos

### Descargar Factura (PDF)
```http
GET /api/orders/{orderId}/invoice-pdf
Authorization: Bearer {token}
```
**Permisos**: ADMIN o propietario de la orden.

**Respuesta**: PDF file (application/pdf)

### Descargar QR de Ticket Individual
```http
GET /api/orders/items/{itemId}/qr-code
Authorization: Bearer {token}
```
**Permisos**: ADMIN, EMPLOYEE, o propietario del ticket.

**Respuesta**: PNG image (image/png)

### Descargar PDF de Ticket Individual
```http
GET /api/orders/items/{itemId}/ticket-pdf
Authorization: Bearer {token}
```
**Permisos**: ADMIN o propietario del ticket.

**Respuesta**: PDF file (application/pdf)

---

## 🎫 Gestión de Tickets Individuales

### Listar Tickets de una Orden
```http
GET /api/orders/{orderId}/items
Authorization: Bearer {token}
```

### Obtener Ticket por ID
```http
GET /api/orders/items/{itemId}
Authorization: Bearer {token}
```

### Marcar Ticket como Usado (EMPLOYEE/ADMIN)
```http
PATCH /api/orders/items/{itemId}/use
Authorization: Bearer {employee-token}
```
Cambia el estado de `VALID` a `USED`. Se usa en la entrada al cine.

---

## 🔄 Flujo de Creación de Orden

```
┌─────────────────────────────────────────────────┐
│ 1. Usuario agrega tickets y productos          │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 2. Usuario selecciona método de pago           │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 3. Usuario aplica código promocional (opcional)│
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 4. POST /api/orders                             │
│    - Valida promoción                           │
│    - Calcula total con descuento                │
│    - Confirma asientos (OCCUPIED)               │
│    - Crea OrderItems y OrderConcessions         │
│    - Genera invoiceNumber único                 │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 5. Sistema genera documentos                    │
│    - QR codes por ticket                        │
│    - PDFs por ticket                            │
│    - PDF de factura general                     │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 6. Orden completada (orderStatus=COMPLETED)    │
│    Usuario recibe confirmación por email        │
└─────────────────────────────────────────────────┘
```

---

## 💰 Cálculo de Totales

### Sin Promoción
```
Subtotal Tickets = Σ(precio × cantidad)
Subtotal Confitería = Σ(precioUnitario × cantidad)
Total = Subtotal Tickets + Subtotal Confitería
```

### Con Promoción
```
Subtotal = Subtotal Tickets + Subtotal Confitería

Si promoción es PERCENTAGE:
    Descuento = Subtotal × valor
    Total = Subtotal - Descuento

Si promoción es FIXED_AMOUNT:
    Total = max(Subtotal - valor, 0)
```

**Ejemplo:**
```
Subtotal Tickets: 2 × $28.00 = $56.00
Subtotal Confitería: 
  - Combo Grande × 2 = $36.00
  - Nachos × 1 = $15.50
  Total Confitería = $51.50

Subtotal = $56.00 + $51.50 = $107.50

Promoción "VERANO2024" (10% descuento):
Descuento = $107.50 × 0.10 = $10.75

Total Final = $107.50 - $10.75 = $96.75
```

---

## 📊 Estados de Orden

### PENDING (Pendiente)
- Orden creada pero pago no confirmado
- Asientos reservados temporalmente
- Se puede cancelar automáticamente después de timeout

### COMPLETED (Completada)
- Pago confirmado
- Asientos confirmados como OCCUPIED
- Tickets y factura generados
- Usuario recibe confirmación

### CANCELLED (Cancelada)
- Orden cancelada antes del pago
- Asientos liberados
- No se genera factura

### REFUNDED (Reembolsada)
- Orden completada pero reembolsada
- Tickets invalidados (status → CANCELLED)
- Asientos liberados si no se usó el ticket
- Factura de crédito generada

---

## 🔒 Seguridad y Permisos

| Acción | Rol Requerido | Endpoint |
|--------|---------------|----------|
| Listar todas las órdenes | `ADMIN` | `GET /api/orders` |
| Ver orden específica | `ADMIN` o Propietario | `GET /api/orders/{id}` |
| Listar órdenes de usuario | `ADMIN` o Propietario | `GET /api/orders/user/{userId}` |
| Crear orden | Autenticado | `POST /api/orders` |
| Actualizar estado | `ADMIN` | `PATCH /api/orders/{id}/status` |
| Descargar factura | `ADMIN` o Propietario | `GET /api/orders/{id}/invoice-pdf` |
| Ver tickets de orden | `ADMIN` o Propietario | `GET /api/orders/{id}/items` |
| Ver ticket individual | `ADMIN` o Propietario | `GET /api/orders/items/{id}` |
| Descargar QR ticket | `ADMIN`, `EMPLOYEE`, o Propietario | `GET /api/orders/items/{id}/qr-code` |
| Descargar PDF ticket | `ADMIN` o Propietario | `GET /api/orders/items/{id}/ticket-pdf` |
| Marcar ticket usado | `ADMIN` o `EMPLOYEE` | `PATCH /api/orders/items/{id}/use` |

---

## 🛡️ Validaciones

### Validaciones al Crear Orden
- ✅ `userId` debe existir
- ✅ `paymentMethodId` debe existir y pertenecer al usuario
- ✅ Todos los `showtimeId` deben existir
- ✅ Todos los `seatId` deben estar disponibles
- ✅ `promotionCode` debe ser válido (si se proporciona)
- ✅ Promoción debe cumplir requisitos (monto mínimo, usos, fechas)
- ✅ Total debe ser > 0

### Validaciones de Tickets
- ✅ No se puede usar un ticket ya `USED`
- ✅ No se puede usar un ticket `CANCELLED`
- ✅ Solo EMPLOYEE o ADMIN puede marcar tickets como usados

---

## 📧 Notificaciones

### Email de Confirmación
Después de crear una orden exitosa, el usuario recibe:
- Número de factura
- Resumen de compra
- Links a PDFs y QR codes
- Información de la función (película, hora, sala, asientos)

### Email de Recordatorio
1 hora antes de la función:
- Recordatorio de asistencia
- QR codes adjuntos
- Instrucciones de llegada

---

## 🚀 Mejoras Futuras

1. **Sistema de Reembolsos Automático**
   - Solicitud de reembolso por parte del usuario
   - Aprobación por ADMIN
   - Procesamiento automático de pago inverso

2. **Compra de Grupos**
   - Descuentos para grupos grandes
   - Asignación automática de asientos juntos

3. **Membresías y Puntos**
   - Acumular puntos por compra
   - Canjear puntos por descuentos

4. **Historial de Compras Detallado**
   - Estadísticas personales
   - Películas más vistas
   - Gastos totales

5. **Validación de Tickets por QR**
   - App móvil para EMPLOYEE
   - Escaneo de QR en entrada
   - Actualización en tiempo real

---

## 📚 Referencias

- **Entidad Order**: `domain/entity/Order.java`
- **Entidad OrderItem**: `domain/entity/OrderItem.java`
- **Entidad OrderConcession**: `domain/entity/OrderConcession.java`
- **DTOs**: `domain/dto/OrderDTO.java`, `domain/dto/OrderItemDTO.java`
- **Service**: `persistence/service/impl/OrderServiceImpl.java`
- **Controller**: `web/controller/OrderController.java`
- **Enums**: `domain/entity/OrderStatus.java`, `domain/entity/TicketStatus.java`

---

**Última actualización:** Diciembre 2025  
**Versión:** 1.0
