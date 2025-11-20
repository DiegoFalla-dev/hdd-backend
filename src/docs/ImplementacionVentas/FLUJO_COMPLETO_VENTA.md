# 🛒 Flujo Completo de Venta - CinePlus Backend vs Frontend

## 📋 Validación del Flujo

Este documento valida que el flujo del frontend coincida con la lógica implementada en el backend.

---

## 🔄 FLUJO BACKEND IMPLEMENTADO (Correcto)

### **Paso 1: Selección de Función** ✅
```
Usuario selecciona película → Elige cine, fecha, horario, formato
↓
Frontend obtiene showtimeId real desde backend
Endpoint: GET /api/showtimes?movieId={id}&cinemaId={id}&date={fecha}
↓
Respuesta: { id: 34, movieTitle: "...", date: "...", time: "...", format: "..." }
```

### **Paso 2: Visualización de Butacas** ✅
```
Frontend navega a /butacas con showtimeId
↓
Endpoint: GET /api/seat-reservations/{showtimeId}/matrix
↓
Respuesta: { rowCount: 10, colCount: 10, seats: [[...]] }
↓
Usuario ve matriz de asientos y selecciona sus butacas
```

### **Paso 3: Reserva Temporal (15 minutos)** ✅
```
Usuario hace clic en "Continuar" después de seleccionar asientos
↓
Endpoint: POST /api/seat-reservations/{showtimeId}
Body: { seatIdentifiers: ["A1", "A2", "A3"], userId: 1 }
↓
Backend:
  - Genera sessionId único (UUID)
  - Marca asientos como TEMPORARILY_RESERVED
  - Crea SeatReservation con expiryTime = now + 15 minutos
  - Retorna sessionId
↓
Respuesta: { sessionId: "uuid-123", message: "Seats reserved for 15 minutes" }
↓
Frontend guarda sessionId en localStorage
```

### **Paso 4: Selección de Dulcería** ⚠️ (Nueva funcionalidad)
```
Frontend navega a página de dulcería
↓
Usuario selecciona productos de confitería
↓
Frontend guarda selección en localStorage:
{
  sessionId: "uuid-123",
  seats: ["A1", "A2", "A3"],
  concessionItems: [
    { productId: 1, name: "Popcorn Grande", quantity: 2, price: 8.00 },
    { productId: 2, name: "Coca-Cola", quantity: 2, price: 5.50 }
  ]
}
```

### **Paso 5: Página Carrito/Resumen** ⚠️ (Nueva funcionalidad)
```
Frontend navega a /carrito-total.tsx
↓
Muestra resumen completo:
  - Película, función, sala
  - Asientos seleccionados (con precios)
  - Productos de dulcería
  - Total a pagar
↓
Usuario selecciona método de pago
↓
Endpoint: GET /api/users/{userId}/payment-methods
Respuesta: [{ id: 1, type: "CREDIT_CARD", maskedNumber: "**** 1234", ... }]
```

### **Paso 6: Procesamiento del Pago** ✅
```
Usuario hace clic en "PAGAR"
↓
Frontend construye request completo:
{
  sessionId: "uuid-123",
  userId: 1,
  paymentMethodId: 1,
  amount: 45.50,  // Total calculado
  items: [
    {
      itemType: "TICKET",
      description: "Entrada - Sala 3, Asiento A1",
      quantity: 1,
      unitPrice: 15.00,
      seatIdentifiers: "A1"
    },
    {
      itemType: "TICKET",
      description: "Entrada - Sala 3, Asiento A2",
      quantity: 1,
      unitPrice: 15.00,
      seatIdentifiers: "A2"
    },
    {
      itemType: "CONCESSION",
      description: "Popcorn Grande",
      quantity: 2,
      unitPrice: 8.00,
      concessionProductId: 1
    },
    {
      itemType: "CONCESSION",
      description: "Coca-Cola",
      quantity: 2,
      unitPrice: 5.50,
      concessionProductId: 2
    }
  ]
}
↓
Endpoint: POST /api/payments/process
Headers: { Authorization: "Bearer {jwt_token}" }
↓
Backend:
  1. Valida JWT (Spring Security)
  2. Valida sessionId existe y no expiró
  3. Valida paymentMethod pertenece al usuario
  4. Calcula total: 15+15+16+11 = 57.00
  5. Valida amount enviado === total calculado
  6. Genera purchaseNumber único: "CIN-20251120153045-A7B3C9D1"
  7. Simula pago (500ms - reemplazar con gateway real)
  8. Genera transactionId: "TXN-abc123"
  9. Crea entidad Purchase en BD
  10. Crea PurchaseItems en BD (4 items)
  11. Confirma asientos: TEMPORARILY_RESERVED → OCCUPIED
  12. Asigna purchaseNumber a los asientos
  13. Confirma SeatReservation (isConfirmed=true)
  14. Retorna PaymentResponseDto
↓
Respuesta: 
{
  success: true,
  purchaseNumber: "CIN-20251120153045-A7B3C9D1",
  transactionId: "TXN-abc123",
  message: "Payment processed successfully"
}
```

### **Paso 7: Dashboard de Confirmación** ⚠️ (Nueva funcionalidad)
```
Frontend recibe purchaseNumber
↓
Navega a /dashboard o /mis-compras
↓
Endpoint: GET /api/purchases/{purchaseNumber}
Headers: { Authorization: "Bearer {jwt_token}" }
↓
Backend retorna PurchaseDto completo:
{
  purchaseNumber: "CIN-20251120153045-A7B3C9D1",
  userName: "Juan Pérez",
  movieTitle: "Los 4 Fantásticos",
  cinemaName: "Cineplus Jockey Plaza",
  theaterName: "Sala 3",
  showDate: "2025-11-25",
  showTime: "18:30:00",
  format: "IMAX 3D",
  status: "COMPLETED",
  maskedCardNumber: "**** **** **** 1234",
  paymentMethodType: "CREDIT_CARD",
  items: [
    { itemType: "TICKET", description: "...", quantity: 1, unitPrice: 15.00, subtotal: 15.00, seatIdentifiers: "A1" },
    { itemType: "TICKET", description: "...", quantity: 1, unitPrice: 15.00, subtotal: 15.00, seatIdentifiers: "A2" },
    { itemType: "CONCESSION", description: "Popcorn Grande", quantity: 2, unitPrice: 8.00, subtotal: 16.00 },
    { itemType: "CONCESSION", description: "Coca-Cola", quantity: 2, unitPrice: 5.50, subtotal: 11.00 }
  ],
  totalAmount: 57.00,
  purchaseDate: "2025-11-20T15:30:45"
}
↓
Frontend muestra:
  - ✅ Compra exitosa
  - Número de orden: CIN-20251120153045-A7B3C9D1
  - Detalles de la película y función
  - QR code con purchaseNumber (para escanear en cine)
  - Botón "Descargar ticket PDF"
  - Resumen de items comprados
```

---

## ⚠️ FLUJO ACTUAL DEL FRONTEND (A Validar)

### **Páginas Existentes:**
1. ✅ `DetallePelicula.tsx` - Selección de función
2. ✅ `Butacas.tsx` - Selección de asientos
3. ❓ `Dulceria.tsx` - Selección de productos (¿existe?)
4. ❓ `carrito-total.tsx` - Resumen y pago (¿existe?)
5. ❌ `Dashboard.tsx` - Confirmación de compra (NO existe)

### **Datos que el Frontend Debe Almacenar:**

#### **Durante Selección (localStorage):**
```typescript
interface MovieSelection {
  showtimeId: number;           // ⭐ ID REAL del backend
  movieId: number;
  movieTitle: string;
  cinemaId: number;
  cinemaName: string;
  theaterName: string;
  date: string;                 // "2025-11-25"
  time: string;                 // "18:30"
  format: string;               // "IMAX_3D"
  price: number;                // Precio por entrada
}

interface SeatSelection {
  sessionId: string;            // ⭐ UUID del backend
  showtimeId: number;
  selectedSeats: Array<{
    id: number;                 // ID del asiento en BD
    identifier: string;         // "A1", "A2", etc.
    rowPosition: number;
    colPosition: number;
    price: number;
  }>;
  expiryTime: string;           // Timestamp de expiración
}

interface ConcessionSelection {
  items: Array<{
    productId: number;          // ID del producto en BD
    name: string;
    quantity: number;
    unitPrice: number;
    subtotal: number;
  }>;
}

interface PaymentMethod {
  id: number;                   // ID del método de pago en BD
  type: string;                 // "CREDIT_CARD", "DEBIT_CARD"
  maskedNumber: string;         // "**** **** **** 1234"
}
```

---

## 🔍 VALIDACIÓN DE FLUJO

### ✅ **Lo que YA está implementado en el Backend:**

1. ✅ Sistema de autenticación JWT
2. ✅ Endpoints de showtimes (funciones)
3. ✅ Sistema de reserva de butacas con sessionId
4. ✅ Temporizador de 15 minutos para reservas
5. ✅ Sistema completo de pagos (Purchase + PurchaseItem)
6. ✅ Validación de métodos de pago
7. ✅ Generación de purchaseNumber único
8. ✅ Confirmación de asientos con purchaseNumber
9. ✅ Endpoint para obtener detalle de compra

### ⚠️ **Lo que el Frontend DEBE implementar:**

1. ⚠️ Obtener showtimeId REAL desde `/api/showtimes`
2. ⚠️ Guardar sessionId después de reservar butacas
3. ⚠️ Página de dulcería con productos de BD
4. ⚠️ Página `carrito-total.tsx` que:
   - Muestre resumen completo
   - Permita seleccionar método de pago
   - Construya request para `/api/payments/process`
   - Incluya JWT en header Authorization
5. ⚠️ Dashboard de confirmación que:
   - Reciba purchaseNumber
   - Consulte `/api/purchases/{purchaseNumber}`
   - Muestre ticket digital
   - Genere QR code
   - Permita descargar PDF

### ❌ **Lo que el Frontend NO debe hacer:**

1. ❌ Generar purchaseNumber (lo genera el backend)
2. ❌ Calcular total del lado del cliente sin validar
3. ❌ Confirmar asientos directamente (lo hace el backend en el pago)
4. ❌ Usar showtimeId temporal/fake
5. ❌ Hacer requests sin JWT token

---

## 📊 ENDPOINTS NECESARIOS PARA EL FLUJO COMPLETO

### **1. Autenticación** ⭐ (DEBE implementarse primero)
```http
POST /api/auth/login
Body: { username: "user@example.com", password: "123456" }
Response: { token: "eyJhbGci...", userId: 1, username: "..." }
```

### **2. Obtener Funciones**
```http
GET /api/showtimes?movieId=17&cinemaId=7&date=2025-11-20
Response: [{ id: 34, movieTitle: "...", ... }]
```

### **3. Obtener Matriz de Butacas**
```http
GET /api/seat-reservations/{showtimeId}/matrix
Response: { rowCount: 10, colCount: 10, seats: [[...]] }
```

### **4. Reservar Butacas**
```http
POST /api/seat-reservations/{showtimeId}
Body: { seatIdentifiers: ["A1", "A2"], userId: 1 }
Response: { sessionId: "uuid-123", message: "..." }
```

### **5. Obtener Productos de Dulcería** ⚠️ (¿Existe endpoint?)
```http
GET /api/concession-products
Response: [{ id: 1, name: "Popcorn", price: 8.00, ... }]
```

### **6. Obtener Métodos de Pago del Usuario**
```http
GET /api/users/{userId}/payment-methods
Headers: { Authorization: "Bearer {token}" }
Response: [{ id: 1, type: "CREDIT_CARD", maskedNumber: "****1234" }]
```

### **7. Procesar Pago** ⭐ (Endpoint principal)
```http
POST /api/payments/process
Headers: { Authorization: "Bearer {token}" }
Body: { sessionId, userId, paymentMethodId, amount, items: [...] }
Response: { success: true, purchaseNumber: "CIN-...", transactionId: "TXN-..." }
```

### **8. Obtener Detalle de Compra**
```http
GET /api/purchases/{purchaseNumber}
Headers: { Authorization: "Bearer {token}" }
Response: { purchaseNumber, userName, movieTitle, items: [...], totalAmount, ... }
```

### **9. Obtener Historial de Compras**
```http
GET /api/users/{userId}/purchases
Headers: { Authorization: "Bearer {token}" }
Response: [{ purchaseNumber, movieTitle, totalAmount, purchaseDate, ... }]
```

---

## 🚨 PUNTOS CRÍTICOS DE VALIDACIÓN

### **1. SessionId**
- ✅ Backend lo genera al reservar
- ⚠️ Frontend DEBE guardarlo en localStorage
- ⚠️ Frontend DEBE enviarlo al procesar pago
- ⚠️ Backend valida que no haya expirado (15 min)

### **2. PurchaseNumber**
- ✅ Backend lo genera en el pago
- ❌ Frontend NO debe generarlo
- ⚠️ Frontend debe guardarlo después del pago exitoso
- ⚠️ Frontend debe usarlo para consultar detalle de compra

### **3. Items del Carrito**
- ⚠️ Frontend debe separar items por tipo:
  - `itemType: "TICKET"` para cada asiento individual
  - `itemType: "CONCESSION"` para productos de dulcería
- ⚠️ Cada TICKET debe incluir `seatIdentifiers` individual ("A1", no "A1,A2")
- ⚠️ CONCESSION debe incluir `concessionProductId`

### **4. Cálculo de Totales**
- ⚠️ Frontend calcula total para mostrar al usuario
- ✅ Backend RECALCULA total y valida coincidencia
- ⚠️ Si no coinciden → Error 400 Bad Request

### **5. Autenticación**
- ⚠️ TODAS las requests de pago DEBEN incluir JWT
- ⚠️ Sin JWT → 401 Unauthorized
- ⚠️ JWT inválido/expirado → Redirigir a login

---

## 🎯 RESUMEN: ¿El Flujo es Correcto?

### ✅ **Backend está 100% correcto:**
- Sistema de reservas con temporizador ✅
- Sistema de pagos completo ✅
- Validaciones de seguridad ✅
- Generación de purchaseNumber ✅
- Almacenamiento en BD ✅

### ⚠️ **Frontend necesita ajustes:**
1. Implementar login/autenticación JWT
2. Obtener showtimeId real (no temporal)
3. Guardar sessionId al reservar
4. Crear/completar página carrito-total.tsx
5. Implementar dashboard de confirmación
6. Incluir JWT en todas las requests protegidas

---

## 📄 PRÓXIMO PASO: Prompt para GitHub Copilot

Una vez validado el flujo, el prompt para Copilot debe incluir:

1. ✅ `SEGURIDAD_Y_AUTENTICACION.md` - Cómo usar JWT
2. ✅ `PAYMENT_SYSTEM_GUIDE.md` - Endpoints y DTOs de pagos
3. ✅ `FLUJO_COMPLETO_VENTA.md` - Este documento (flujo completo)
4. ⚠️ Estructura de localStorage esperada
5. ⚠️ Mockups/wireframes de carrito-total.tsx y dashboard

**¿Continuamos con el prompt para Copilot?** 🚀
