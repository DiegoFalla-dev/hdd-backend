# 💳 Guía del Sistema de Pagos - CinePlus Backend

**Fecha:** 20 de Noviembre 2025  
**Backend:** Spring Boot 3.2.5 + MySQL 8  
**API Base URL:** `http://localhost:8080`  
**Seguridad:** Encriptación AES-256 para datos sensibles

---

## 🎯 RESUMEN EJECUTIVO

El sistema de pagos de CinePlus permite a los usuarios:
1. **Guardar métodos de pago** de forma segura (tarjetas encriptadas)
2. **Gestionar sus tarjetas** guardadas
3. **Confirmar compras** asociadas a reservas de asientos

**⚠️ IMPORTANTE:** Actualmente **NO HAY una entidad de Compra/Orden** en la base de datos. El sistema funciona así:

1. Usuario reserva asientos → recibe `sessionId`
2. Usuario confirma compra → se genera `purchaseNumber` (debe ser único)
3. Los asientos se marcan como `OCCUPIED` con el `purchaseNumber`
4. **NO se almacena información detallada de la compra** (monto total, método de pago usado, fecha, etc.)

---

## 🏗️ ARQUITECTURA ACTUAL

### ✅ LO QUE EXISTE

```
┌─────────────────┐
│ PaymentMethod   │ (Entidad)
│ ─────────────── │
│ - id            │
│ - user_id       │
│ - cardNumber    │ ← ENCRIPTADO
│ - cardHolder    │ ← ENCRIPTADO
│ - cci           │ ← ENCRIPTADO
│ - expiry        │ ← ENCRIPTADO
│ - phone         │ ← ENCRIPTADO
│ - isDefault     │
└─────────────────┘

┌─────────────────┐
│ SeatReservation │ (Proceso)
│ ─────────────── │
│ - sessionId     │ ← UUID temporal
│ - showtimeId    │
│ - seatIds       │
│ - expiryTime    │ (1 minuto)
└─────────────────┘

┌─────────────────┐
│ Seat            │ (Entidad)
│ ─────────────── │
│ - id            │
│ - status        │ (AVAILABLE/RESERVED/OCCUPIED/CANCELLED)
│ - sessionId     │ ← Asociación temporal
│ - purchaseNumber│ ← Se asigna al confirmar
│ - isCancelled   │
└─────────────────┘
```

### ❌ LO QUE FALTA (CRÍTICO)

**NO existe una tabla `purchases` o `orders` que almacene:**
- ❌ Monto total de la compra
- ❌ Método de pago utilizado (ID de la tarjeta usada)
- ❌ Fecha y hora de la compra
- ❌ Estado de la transacción (PENDING/COMPLETED/FAILED)
- ❌ Detalle de productos (entradas + confitería)
- ❌ Información del usuario comprador
- ❌ Información fiscal (boleta/factura)

---

## 📊 FLUJO ACTUAL DE COMPRA

```
1. RESERVA DE ASIENTOS
   POST /api/seat-reservations/{showtimeId}
   Body: { "seatIdentifiers": ["A1", "A2"], "userId": 123 }
   Response: { "sessionId": "uuid-aqui", "message": "..." }
   
   ↓ (1 minuto de validez)

2. CONFIRMACIÓN DE COMPRA
   POST /api/seat-reservations/confirm
   Body: { "sessionId": "uuid-aqui", "purchaseNumber": "ORD-20251120-1234" }
   Response: { "message": "Purchase confirmed successfully" }
   
   ⚠️ PROBLEMA: purchaseNumber debe ser generado por el FRONTEND
   ⚠️ NO se registra: monto pagado, método de pago, fecha, etc.
```

---

## 🔌 ENDPOINTS DISPONIBLES

### 1️⃣ Gestión de Métodos de Pago

#### GET - Listar Métodos de Pago del Usuario

```http
GET /api/users/{userId}/payment-methods
```

**Path Parameters:**
- `userId` (Long): ID del usuario autenticado

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "isDefault": true,
    "maskedCardNumber": "**** **** **** 1234"
  },
  {
    "id": 2,
    "isDefault": false,
    "maskedCardNumber": "**** **** **** 5678"
  }
]
```

**Notas:**
- Solo devuelve tarjetas **enmascaradas** por seguridad
- NO expone datos sensibles (cardHolder, CCI, expiry, phone)

---

#### POST - Agregar Método de Pago

```http
POST /api/users/{userId}/payment-methods
```

**Path Parameters:**
- `userId` (Long): ID del usuario autenticado

**Request Body:**
```json
{
  "cardNumber": "4532015112830366",
  "cardHolder": "JUAN PEREZ LOPEZ",
  "cci": "00219100320145678901",
  "expiry": "12/26",
  "phone": "987654321",
  "isDefault": true
}
```

**Response (201 Created):**
```json
1
```
(Devuelve solo el ID del método de pago creado)

**Seguridad:**
- Todos los campos se **encriptan automáticamente** antes de guardarse
- Usa AES-256 con clave configurable
- Solo el backend puede desencriptar

**Validaciones Necesarias en Frontend:**
- `cardNumber`: 13-19 dígitos, validar con algoritmo Luhn
- `cardHolder`: Solo letras y espacios, mínimo 5 caracteres
- `cci`: Exactamente 20 dígitos (formato peruano)
- `expiry`: Formato MM/YY, fecha futura
- `phone`: 9 dígitos (celular peruano)

---

### 2️⃣ Flujo de Compra (Reserva + Confirmación)

#### POST - Reservar Asientos

```http
POST /api/seat-reservations/{showtimeId}
```

**Path Parameters:**
- `showtimeId` (Long): ID de la función (obtenido de `/api/showtimes`)

**Request Body:**
```json
{
  "seatIdentifiers": ["A1", "A2", "A3"],
  "userId": 123
}
```

**Response (201 Created):**
```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "message": "Seats reserved temporarily for 1 minute"
}
```

**Importante:**
- El `sessionId` expira en **60 segundos**
- Un scheduler limpia reservas expiradas cada **30 segundos**
- Si no se confirma a tiempo, los asientos vuelven a estar `AVAILABLE`

---

#### POST - Confirmar Compra

```http
POST /api/seat-reservations/confirm
```

**Request Body:**
```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "purchaseNumber": "ORD-20251120-123456"
}
```

**Response (200 OK):**
```json
{
  "message": "Purchase confirmed successfully"
}
```

**⚠️ PROBLEMA CRÍTICO:**
- `purchaseNumber` debe ser generado por el **frontend** (no es buena práctica)
- NO se almacena información de la compra completa
- NO se asocia con un método de pago
- NO se guarda el monto total

---

## 🚨 PROBLEMAS Y SOLUCIONES RECOMENDADAS

### Problema 1: No hay Entidad de Compra

**Situación Actual:**
```java
// Solo se guarda purchaseNumber en Seat
public class Seat {
    private String purchaseNumber; // "ORD-20251120-1234"
    // NO hay relación con una tabla purchases
}
```

**Solución Recomendada:**

Crear la entidad `Purchase`:

```java
@Entity
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String purchaseNumber; // Generado por backend
    
    @ManyToOne
    private User user;
    
    @ManyToOne
    private Showtime showtime;
    
    @ManyToOne
    private PaymentMethod paymentMethod; // Tarjeta usada
    
    private BigDecimal totalAmount; // Monto total
    
    private LocalDateTime purchaseDate;
    
    @Enumerated(EnumType.STRING)
    private PurchaseStatus status; // PENDING, COMPLETED, FAILED, REFUNDED
    
    // Productos comprados (entradas + confitería)
    @OneToMany(mappedBy = "purchase")
    private List<PurchaseItem> items;
}
```

---

### Problema 2: Frontend Genera purchaseNumber

**Situación Actual:**
```typescript
// ❌ INCORRECTO: Frontend genera el número de orden
const purchaseNumber = `ORD-${Date.now()}-${Math.random()}`;
```

**Solución Recomendada:**

El backend debe generar el `purchaseNumber`:

```java
// En PurchaseService
public String generatePurchaseNumber() {
    String timestamp = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    return String.format("CIN-%s-%s", timestamp, random);
    // Ejemplo: CIN-20251120153045-A7B3C9D1
}
```

---

### Problema 3: No se Valida el Pago Real

**Situación Actual:**
- Usuario "selecciona" método de pago en frontend
- Backend NO valida si tiene fondos
- Backend NO procesa transacción real
- Solo guarda `purchaseNumber` en los asientos

**Solución Recomendada:**

Integrar con pasarela de pago (Niubiz, MercadoPago, Culqi):

```java
@Service
public class PaymentProcessorService {
    
    public PaymentResult processPayment(
        Long paymentMethodId, 
        BigDecimal amount,
        String description
    ) {
        // 1. Obtener método de pago y desencriptar
        PaymentMethod pm = paymentMethodRepository.findById(paymentMethodId)...;
        String cardNumber = Encryptor.decrypt(pm.getCardNumberEncrypted());
        
        // 2. Llamar a API de pasarela de pago
        PaymentGatewayResponse response = niubizClient.charge(
            cardNumber, amount, description
        );
        
        // 3. Retornar resultado
        return new PaymentResult(
            response.isSuccess(),
            response.getTransactionId(),
            response.getMessage()
        );
    }
}
```

---

## 💻 IMPLEMENTACIÓN FRONTEND RECOMENDADA

### Flujo Completo de Compra

```typescript
// 1. Usuario selecciona asientos
const selectedSeats = ['A1', 'A2', 'A3'];
const showtimeId = 34;
const userId = localStorage.getItem('userId');

// 2. Calcular monto total
const ticketPrice = 25.00; // Precio por entrada
const totalAmount = selectedSeats.length * ticketPrice;

// 3. Iniciar reserva
const reserveResponse = await fetch(
  `http://localhost:8080/api/seat-reservations/${showtimeId}`,
  {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      seatIdentifiers: selectedSeats,
      userId: userId
    })
  }
);

const { sessionId } = await reserveResponse.json();
console.log('SessionId:', sessionId);

// 4. Iniciar timer de 60 segundos
startCountdown(60);

// 5. Usuario selecciona método de pago
const paymentMethods = await fetch(
  `http://localhost:8080/api/users/${userId}/payment-methods`
).then(r => r.json());

// Mostrar lista de tarjetas enmascaradas
// Usuario selecciona una: selectedPaymentMethodId = 1

// 6. Procesar pago (AQUÍ FALTA LA INTEGRACIÓN)
// ⚠️ Actualmente NO existe endpoint para procesar el pago
// Debería existir: POST /api/payments/process

// 7. Confirmar compra (solo si pago exitoso)
const confirmResponse = await fetch(
  'http://localhost:8080/api/seat-reservations/confirm',
  {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      sessionId: sessionId,
      purchaseNumber: `ORD-${Date.now()}` // ⚠️ Generado por frontend (no ideal)
    })
  }
);

// 8. Mostrar confirmación
alert('¡Compra exitosa!');
navigate('/mis-compras');
```

---

## 🛠️ ENDPOINTS QUE FALTAN (RECOMENDADOS)

### 1. Procesar Pago

```http
POST /api/payments/process
```

**Request Body:**
```json
{
  "sessionId": "uuid-aqui",
  "paymentMethodId": 1,
  "amount": 75.00,
  "userId": 123,
  "items": [
    {
      "type": "TICKET",
      "quantity": 3,
      "unitPrice": 25.00,
      "description": "Entrada General - Los 4 Fantásticos"
    }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "purchaseNumber": "CIN-20251120153045-A7B3C9D1",
  "transactionId": "TXN-98765432",
  "message": "Payment processed successfully"
}
```

---

### 2. Obtener Historial de Compras

```http
GET /api/users/{userId}/purchases
```

**Response:**
```json
[
  {
    "id": 1,
    "purchaseNumber": "CIN-20251120153045-A7B3C9D1",
    "movieTitle": "Los 4 Fantásticos",
    "cinemaName": "Cineplus Jockey Plaza",
    "date": "2025-11-20",
    "time": "14:00",
    "seats": ["A1", "A2", "A3"],
    "totalAmount": 75.00,
    "purchaseDate": "2025-11-20T15:30:45",
    "status": "COMPLETED"
  }
]
```

---

### 3. Obtener Detalle de Compra

```http
GET /api/purchases/{purchaseNumber}
```

**Response:**
```json
{
  "purchaseNumber": "CIN-20251120153045-A7B3C9D1",
  "user": {
    "id": 123,
    "name": "Juan Pérez",
    "email": "juan@example.com"
  },
  "showtime": {
    "id": 34,
    "movieTitle": "Los 4 Fantásticos",
    "date": "2025-11-20",
    "time": "14:00",
    "format": "2D",
    "cinemaName": "Cineplus Jockey Plaza",
    "theaterName": "Sala 1"
  },
  "items": [
    {
      "type": "TICKET",
      "description": "Entrada General",
      "quantity": 3,
      "unitPrice": 25.00,
      "subtotal": 75.00
    }
  ],
  "paymentMethod": {
    "maskedCardNumber": "**** **** **** 1234"
  },
  "totalAmount": 75.00,
  "purchaseDate": "2025-11-20T15:30:45",
  "status": "COMPLETED",
  "seats": ["A1", "A2", "A3"]
}
```

---

## 🔐 SEGURIDAD Y ENCRIPTACIÓN

### Datos Encriptados en BD

```sql
-- Ejemplo de cómo se ven en la base de datos
SELECT * FROM payment_methods WHERE user_id = 123;

+----+---------+----------------------------+----------------------------+
| id | user_id | card_number_encrypted      | card_holder_encrypted      |
+----+---------+----------------------------+----------------------------+
| 1  | 123     | aGVsbG8gd29ybGQgdGhpcyBp... | dGVzdCBkYXRhIGhlcmUgZm9y... |
+----+---------+----------------------------+----------------------------+
```

### Clase Encryptor

```java
package com.cineplus.cineplus.persistence.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Encryptor {
    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "MySecretKey12345".getBytes(); // ⚠️ Usar variable de entorno
    
    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KEY, ALGORITHM));
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }
    
    public static String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY, ALGORITHM));
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
```

**⚠️ IMPORTANTE:** La clave de encriptación debe estar en variables de entorno, NO hardcodeada.

---

## 📝 PROMPT PARA COPILOT (FRONTEND)

```
Necesito implementar el sistema de pagos completo en el frontend de CinePlus. 

CONTEXTO:
El backend tiene endpoints para gestionar métodos de pago (tarjetas encriptadas) y confirmar compras de entradas.

ENDPOINTS DISPONIBLES:

1. GET /api/users/{userId}/payment-methods
   - Retorna lista de tarjetas guardadas (enmascaradas)
   - Response: [{ "id": 1, "isDefault": true, "maskedCardNumber": "**** 1234" }]

2. POST /api/users/{userId}/payment-methods
   - Guarda nueva tarjeta (se encripta automáticamente)
   - Body: { "cardNumber": "4532...", "cardHolder": "JUAN PEREZ", "cci": "0021...", "expiry": "12/26", "phone": "987654321", "isDefault": true }
   - Response: ID de la tarjeta creada

3. POST /api/seat-reservations/confirm
   - Confirma la compra después de reservar asientos
   - Body: { "sessionId": "uuid", "purchaseNumber": "ORD-..." }
   - Response: { "message": "Purchase confirmed successfully" }

FLUJO ACTUAL DE COMPRA:
1. Usuario selecciona asientos → Llama POST /api/seat-reservations/{showtimeId}
2. Backend retorna sessionId válido por 60 segundos
3. Usuario selecciona método de pago (o agrega uno nuevo)
4. Usuario confirma → Llama POST /api/seat-reservations/confirm
5. Asientos quedan como OCCUPIED

TAREAS A IMPLEMENTAR:

1. Crear src/services/paymentApi.ts:
   - Interface PaymentMethod: id, isDefault, maskedCardNumber
   - Interface PaymentMethodCreate: cardNumber, cardHolder, cci, expiry, phone, isDefault
   - getPaymentMethods(userId): Obtener tarjetas guardadas
   - addPaymentMethod(userId, data): Agregar nueva tarjeta

2. Crear componente PaymentMethodSelector:
   - Mostrar lista de tarjetas guardadas con radio buttons
   - Opción "Agregar nueva tarjeta" que abre un formulario
   - Marcar visualmente cuál es la predeterminada

3. Crear componente AddPaymentMethodForm:
   - Campos: Número de tarjeta, Titular, CCI, Fecha vencimiento, Teléfono
   - Validaciones:
     * cardNumber: 13-19 dígitos, validar con algoritmo Luhn
     * cardHolder: Solo letras y espacios, mín 5 caracteres
     * cci: Exactamente 20 dígitos
     * expiry: Formato MM/YY, validar fecha futura
     * phone: 9 dígitos
   - Checkbox "Usar como predeterminada"
   - Al guardar, cerrar formulario y actualizar lista

4. Integrar en página de Confirmación/Pago:
   - Mostrar resumen: película, cine, fecha, hora, asientos, monto total
   - Mostrar PaymentMethodSelector
   - Botón "Confirmar Pago" que:
     * Valida que haya método de pago seleccionado
     * Genera purchaseNumber: `ORD-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
     * Llama POST /api/seat-reservations/confirm con sessionId y purchaseNumber
     * Redirige a página de éxito o muestra error

5. Crear página MisPagos (opcional):
   - Listar tarjetas guardadas
   - Opción para eliminar tarjetas (si backend lo soporta)
   - Opción para marcar como predeterminada

IMPORTANTE:
- El sessionId ya viene desde la reserva anterior (leer de localStorage o state)
- El timer de 60 segundos debe estar visible mientras el usuario selecciona método de pago
- Si el timer expira, mostrar mensaje y redirigir a volver a seleccionar asientos
- Los datos de tarjeta se encriptan automáticamente en el backend, NO encriptar en frontend
- Mostrar máscara visual mientras el usuario escribe el número de tarjeta: **** **** **** 1234
```

---

## ✅ CHECKLIST DE IMPLEMENTACIÓN

### Backend (FALTA IMPLEMENTAR)
- [ ] Crear entidad `Purchase` con relación a `User`, `Showtime`, `PaymentMethod`
- [ ] Crear entidad `PurchaseItem` para detallar productos comprados
- [ ] Endpoint `POST /api/payments/process` para procesar pago
- [ ] Endpoint `GET /api/users/{userId}/purchases` para historial
- [ ] Endpoint `GET /api/purchases/{purchaseNumber}` para detalle
- [ ] Endpoint `DELETE /api/users/{userId}/payment-methods/{id}` para eliminar tarjeta
- [ ] Endpoint `PATCH /api/users/{userId}/payment-methods/{id}/default` para marcar predeterminada
- [ ] Integración con pasarela de pago real (Niubiz/MercadoPago/Culqi)
- [ ] Migrar generación de `purchaseNumber` al backend
- [ ] Agregar validaciones de seguridad (2FA, CVV, OTP)

### Frontend (NECESITA IMPLEMENTARSE)
- [ ] Servicio `paymentApi.ts` con llamadas a endpoints
- [ ] Componente `PaymentMethodSelector`
- [ ] Componente `AddPaymentMethodForm` con validaciones
- [ ] Página de confirmación/pago integrada
- [ ] Timer de 60 segundos visible
- [ ] Página de historial de compras
- [ ] Manejo de errores y mensajes claros
- [ ] Loading states durante llamadas API
- [ ] Confirmación visual de compra exitosa

### Base de Datos (CRÍTICO)
- [ ] Crear tabla `purchases`
- [ ] Crear tabla `purchase_items`
- [ ] Agregar índices para búsquedas rápidas
- [ ] Crear trigger para actualizar `available_seats` en `showtimes`

---

## 🎬 CONCLUSIÓN

El sistema de pagos actual es **funcional pero incompleto**:

✅ **Funciona:**
- Guardar métodos de pago encriptados
- Reservar asientos temporalmente
- Confirmar compra básica

❌ **Falta:**
- Entidad de compra completa en BD
- Procesamiento real de pagos
- Historial de compras
- Validación de fondos
- Generación backend de purchaseNumber
- Integración con pasarela de pago

**Recomendación:** Implementar los endpoints faltantes antes de producción para tener un sistema completo y seguro.

---

**Documentación generada:** 20 de Noviembre 2025  
**Autor:** Sistema de Documentación CinePlus  
**Versión:** 1.0.0  
**Repositorio:** `hdd-backend` (branch: `hotfix-ESTABLE-BUTACAS`)
