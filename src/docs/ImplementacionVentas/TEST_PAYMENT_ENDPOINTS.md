# 🧪 Pruebas de Endpoints del Sistema de Pagos

Este documento contiene ejemplos de pruebas para los endpoints del sistema de pagos de CinePlus.

## 📋 Pre-requisitos

1. Backend corriendo en `http://localhost:8080`
2. Base de datos MySQL con datos de prueba:
   - Usuario con ID 1
   - Método de pago con ID válido
   - Función (showtime) activa
   - Reserva de asientos con sessionId

---

## 🔧 Herramientas de Prueba

### Opción 1: PowerShell (Windows)
```powershell
# Procesar Pago
$body = @{
    sessionId = "test-session-123"
    userId = 1
    paymentMethodId = 1
    amount = 45.50
    items = @(
        @{
            itemType = "TICKET"
            description = "Entrada - Sala 3, Asiento A5"
            quantity = 2
            unitPrice = 15.00
            seatIdentifiers = "A5,A6"
        },
        @{
            itemType = "CONCESSION"
            description = "Combo Grande"
            quantity = 1
            unitPrice = 15.50
            concessionProductId = 3
        }
    )
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Uri "http://localhost:8080/api/payments/process" `
    -Method POST `
    -Body $body `
    -ContentType "application/json" | ConvertTo-Json
```

### Opción 2: curl (Windows/Linux/Mac)
```bash
# Procesar Pago
curl -X POST http://localhost:8080/api/payments/process \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "test-session-123",
    "userId": 1,
    "paymentMethodId": 1,
    "amount": 45.50,
    "items": [
      {
        "itemType": "TICKET",
        "description": "Entrada - Sala 3, Asiento A5",
        "quantity": 2,
        "unitPrice": 15.00,
        "seatIdentifiers": "A5,A6"
      },
      {
        "itemType": "CONCESSION",
        "description": "Combo Grande",
        "quantity": 1,
        "unitPrice": 15.50,
        "concessionProductId": 3
      }
    ]
  }'
```

---

## 🧪 Suite de Pruebas

### Prueba 1: Procesar Pago Exitoso ✅

**Endpoint:** `POST /api/payments/process`

**Request:**
```json
{
  "sessionId": "sess-abc123",
  "userId": 1,
  "paymentMethodId": 1,
  "amount": 30.00,
  "items": [
    {
      "itemType": "TICKET",
      "description": "Entrada - Sala 1, Asiento A1",
      "quantity": 2,
      "unitPrice": 15.00,
      "seatIdentifiers": "A1,A2"
    }
  ]
}
```

**Respuesta Esperada:** `201 Created`
```json
{
  "success": true,
  "purchaseNumber": "CIN-20251120153045-A7B3C9D1",
  "transactionId": "TXN-abc123def456",
  "message": "Payment processed successfully"
}
```

---

### Prueba 2: Validación de Monto Incorrecto ❌

**Request:**
```json
{
  "sessionId": "sess-abc123",
  "userId": 1,
  "paymentMethodId": 1,
  "amount": 25.00,  // ❌ Debería ser 30.00
  "items": [
    {
      "itemType": "TICKET",
      "description": "Entrada - Sala 1, Asiento A1",
      "quantity": 2,
      "unitPrice": 15.00,
      "seatIdentifiers": "A1,A2"
    }
  ]
}
```

**Respuesta Esperada:** `400 Bad Request`
```json
{
  "error": "Bad Request",
  "message": "Amount mismatch: calculated 30.00 but received 25.00"
}
```

---

### Prueba 3: Sesión de Reserva No Encontrada ❌

**Request:**
```json
{
  "sessionId": "sess-invalid-999",  // ❌ Sesión inexistente
  "userId": 1,
  "paymentMethodId": 1,
  "amount": 30.00,
  "items": [
    {
      "itemType": "TICKET",
      "description": "Entrada",
      "quantity": 2,
      "unitPrice": 15.00,
      "seatIdentifiers": "A1,A2"
    }
  ]
}
```

**Respuesta Esperada:** `404 Not Found`
```json
{
  "error": "Not Found",
  "message": "Reservation session not found or expired"
}
```

---

### Prueba 4: Método de Pago No Pertenece al Usuario ❌

**Request:**
```json
{
  "sessionId": "sess-abc123",
  "userId": 1,
  "paymentMethodId": 999,  // ❌ No pertenece al usuario 1
  "amount": 30.00,
  "items": [
    {
      "itemType": "TICKET",
      "description": "Entrada",
      "quantity": 2,
      "unitPrice": 15.00,
      "seatIdentifiers": "A1,A2"
    }
  ]
}
```

**Respuesta Esperada:** `403 Forbidden`
```json
{
  "error": "Forbidden",
  "message": "Payment method does not belong to the user"
}
```

---

### Prueba 5: Obtener Historial de Compras del Usuario ✅

**Endpoint:** `GET /api/users/{userId}/purchases`

**Request:**
```bash
curl -X GET http://localhost:8080/api/users/1/purchases
```

**Respuesta Esperada:** `200 OK`
```json
[
  {
    "purchaseNumber": "CIN-20251120153045-A7B3C9D1",
    "userId": 1,
    "userName": "Juan Pérez",
    "movieTitle": "Oppenheimer",
    "cinemaName": "CinePlus Miraflores",
    "theaterName": "Sala 3",
    "showDate": "2025-11-25",
    "showTime": "18:30:00",
    "format": "IMAX 3D",
    "status": "COMPLETED",
    "maskedCardNumber": "**** **** **** 1234",
    "paymentMethodType": "CREDIT_CARD",
    "items": [
      {
        "itemType": "TICKET",
        "description": "Entrada - Sala 3, Asiento A5",
        "quantity": 2,
        "unitPrice": 15.00,
        "subtotal": 30.00,
        "seatIdentifiers": "A5,A6"
      },
      {
        "itemType": "CONCESSION",
        "description": "Combo Grande",
        "quantity": 1,
        "unitPrice": 15.50,
        "subtotal": 15.50,
        "concessionProductId": 3
      }
    ],
    "totalAmount": 45.50,
    "purchaseDate": "2025-11-20T15:30:45"
  }
]
```

---

### Prueba 6: Obtener Detalle de Compra por Número ✅

**Endpoint:** `GET /api/purchases/{purchaseNumber}`

**Request:**
```bash
curl -X GET http://localhost:8080/api/purchases/CIN-20251120153045-A7B3C9D1
```

**Respuesta Esperada:** `200 OK`
```json
{
  "purchaseNumber": "CIN-20251120153045-A7B3C9D1",
  "userId": 1,
  "userName": "Juan Pérez",
  "movieTitle": "Oppenheimer",
  "cinemaName": "CinePlus Miraflores",
  "theaterName": "Sala 3",
  "showDate": "2025-11-25",
  "showTime": "18:30:00",
  "format": "IMAX 3D",
  "status": "COMPLETED",
  "maskedCardNumber": "**** **** **** 1234",
  "paymentMethodType": "CREDIT_CARD",
  "items": [
    {
      "itemType": "TICKET",
      "description": "Entrada - Sala 3, Asiento A5",
      "quantity": 2,
      "unitPrice": 15.00,
      "subtotal": 30.00,
      "seatIdentifiers": "A5,A6"
    }
  ],
  "totalAmount": 30.00,
  "purchaseDate": "2025-11-20T15:30:45"
}
```

---

### Prueba 7: Compra con Solo Tickets ✅

**Request:**
```json
{
  "sessionId": "sess-abc123",
  "userId": 1,
  "paymentMethodId": 1,
  "amount": 45.00,
  "items": [
    {
      "itemType": "TICKET",
      "description": "Entrada General - Sala 2",
      "quantity": 3,
      "unitPrice": 15.00,
      "seatIdentifiers": "B1,B2,B3"
    }
  ]
}
```

**Respuesta Esperada:** `201 Created`

---

### Prueba 8: Compra con Múltiples Items ✅

**Request:**
```json
{
  "sessionId": "sess-xyz789",
  "userId": 1,
  "paymentMethodId": 1,
  "amount": 92.50,
  "items": [
    {
      "itemType": "TICKET",
      "description": "Entrada VIP - Sala Premium",
      "quantity": 2,
      "unitPrice": 25.00,
      "seatIdentifiers": "VIP1,VIP2"
    },
    {
      "itemType": "CONCESSION",
      "description": "Popcorn Grande",
      "quantity": 2,
      "unitPrice": 8.00,
      "concessionProductId": 1
    },
    {
      "itemType": "CONCESSION",
      "description": "Coca-Cola Mediana",
      "quantity": 2,
      "unitPrice": 5.50,
      "concessionProductId": 2
    },
    {
      "itemType": "CONCESSION",
      "description": "Nachos",
      "quantity": 1,
      "unitPrice": 12.50,
      "concessionProductId": 4
    }
  ]
}
```

**Cálculo:**
- 2 entradas VIP × $25.00 = $50.00
- 2 popcorn × $8.00 = $16.00
- 2 cocacola × $5.50 = $11.00
- 1 nachos × $12.50 = $12.50
- **Total: $89.50** ❌ (el amount debería ser 89.50, no 92.50)

---

## 📊 Verificación en Base de Datos

Después de procesar un pago exitoso, verifica:

### 1. Tabla `purchases`
```sql
SELECT * FROM purchases 
WHERE purchase_number = 'CIN-20251120153045-A7B3C9D1';
```

**Debe mostrar:**
- `id`, `purchase_number`, `session_id`, `user_id`, `showtime_id`
- `payment_method_id`, `total_amount`, `purchase_date`
- `status = 'COMPLETED'`, `transaction_id`

### 2. Tabla `purchase_items`
```sql
SELECT * FROM purchase_items 
WHERE purchase_id = (
  SELECT id FROM purchases 
  WHERE purchase_number = 'CIN-20251120153045-A7B3C9D1'
);
```

**Debe mostrar:**
- Todos los items de la compra con sus cantidades y precios
- `subtotal` correctamente calculado

### 3. Tabla `seats` (confirmación)
```sql
SELECT * FROM seats 
WHERE purchase_number = 'CIN-20251120153045-A7B3C9D1';
```

**Debe mostrar:**
- Los asientos reservados ahora con `status = 'OCCUPIED'`
- `purchase_number` asignado correctamente

---

## 🛠️ Comandos PowerShell Útiles

### Obtener todos los endpoints disponibles
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users/1/purchases" | ConvertTo-Json -Depth 10
```

### Formatear respuesta JSON
```powershell
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/purchases/CIN-20251120153045-A7B3C9D1"
$response | ConvertTo-Json -Depth 10 | Out-File -FilePath "purchase_response.json"
```

### Probar con autenticación JWT (si está habilitada)
```powershell
$headers = @{
    "Authorization" = "Bearer YOUR_JWT_TOKEN_HERE"
    "Content-Type" = "application/json"
}

Invoke-RestMethod -Uri "http://localhost:8080/api/users/1/purchases" `
    -Method GET `
    -Headers $headers
```

---

## ✅ Checklist de Pruebas

- [ ] Procesar pago exitoso con solo tickets
- [ ] Procesar pago exitoso con tickets y confitería
- [ ] Validar error de monto incorrecto
- [ ] Validar error de sesión inexistente
- [ ] Validar error de sesión expirada
- [ ] Validar error de método de pago no pertenece al usuario
- [ ] Validar error de usuario inexistente
- [ ] Validar error de método de pago inexistente
- [ ] Obtener historial de compras vacío
- [ ] Obtener historial de compras con datos
- [ ] Obtener detalle de compra existente
- [ ] Obtener detalle de compra inexistente (404)
- [ ] Verificar que los asientos se marquen como OCCUPIED
- [ ] Verificar que el purchaseNumber sea único
- [ ] Verificar que los items se guarden correctamente
- [ ] Verificar que los subtotales se calculen automáticamente

---

## 🐛 Troubleshooting

### Error: "Port 8080 already in use"
```powershell
# Encontrar proceso en puerto 8080
Get-NetTCPConnection -LocalPort 8080 | Select-Object -ExpandProperty OwningProcess

# Matar proceso
Stop-Process -Id PROCESS_ID -Force
```

### Error: "Connection refused"
- Verifica que el backend esté corriendo
- Revisa los logs de Spring Boot
- Confirma que el puerto sea 8080

### Error: "404 Not Found"
- Verifica que la URL sea correcta
- Confirma que el endpoint esté registrado en el controlador
- Revisa los logs de mapeo de Spring

### Error: "500 Internal Server Error"
- Revisa los logs del backend para el stack trace completo
- Verifica que la base de datos esté accesible
- Confirma que todos los datos de referencia existan

---

**🎬 Sistema CinePlus - Pruebas de Endpoints de Pagos** ✅
