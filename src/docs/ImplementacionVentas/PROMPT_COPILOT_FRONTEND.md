# 🤖 Prompt para GitHub Copilot - Sistema de Compra CinePlus Frontend

## 📋 CONTEXTO DEL PROYECTO

Estoy desarrollando el frontend de **CinePlus**, una aplicación de cine en React + TypeScript + Vite. El backend en Spring Boot ya está 100% implementado con:

- ✅ Sistema de autenticación JWT
- ✅ Sistema de reserva de butacas con sessionId (expira en 15 minutos)
- ✅ Sistema completo de pagos (Purchase + PurchaseItem)
- ✅ Endpoints REST documentados
- ✅ Base de datos MySQL con todas las tablas

---

## 🎯 OBJETIVO

Necesito implementar el **flujo completo de compra** desde que el usuario selecciona sus asientos hasta que ve su ticket digital en un dashboard. El flujo debe seguir EXACTAMENTE la arquitectura del backend.

---

## 📂 ARCHIVOS DE REFERENCIA

Lee y analiza estos documentos antes de generar código:

### 1. **SEGURIDAD_Y_AUTENTICACION.md** ⭐ (CRÍTICO)
- Explica cómo usar JWT en TODAS las requests
- Muestra ejemplos de AuthService helper
- Manejo de errores 401/403

### 2. **PAYMENT_SYSTEM_GUIDE.md** ⭐ (CRÍTICO)
- Endpoints completos de pagos
- DTOs (CreatePurchaseDto, PurchaseDto, etc.)
- Ejemplos de requests/responses
- Interfaces TypeScript ya definidas

### 3. **FLUJO_COMPLETO_VENTA.md** ⭐ (CRÍTICO)
- Flujo paso a paso del backend
- Validación de datos necesarios
- Endpoints en orden de uso

### 4. **FRONTEND_INTEGRATION_GUIDE.md**
- Integración con sistema de butacas
- Cómo obtener showtimeId real
- Ejemplo de servicio de showtimes

---

## 🔄 FLUJO ESPERADO (Resume de FLUJO_COMPLETO_VENTA.md)

```
1. Usuario hace LOGIN → Obtiene JWT token
2. Selecciona película → Obtiene showtimeId REAL desde /api/showtimes
3. Selecciona butacas → POST /api/seat-reservations/{showtimeId}
   → Backend retorna sessionId (reserva por 15 min)
4. Selecciona productos de dulcería
5. Ve resumen en carrito-total.tsx
6. Selecciona método de pago
7. Hace clic en PAGAR → POST /api/payments/process (con JWT)
   → Backend retorna purchaseNumber
8. Navega a dashboard → GET /api/purchases/{purchaseNumber}
   → Muestra ticket digital completo
```

---

## 🛠️ LO QUE NECESITO QUE IMPLEMENTES

### **TAREA 1: Servicio de Autenticación** ⭐

Crea `src/services/authService.ts` usando el patrón de **SEGURIDAD_Y_AUTENTICACION.md**:

```typescript
// Debe incluir:
- login(username, password) → Guarda JWT en localStorage
- getToken() → Retorna JWT actual
- getUserId() → Retorna ID del usuario autenticado
- isAuthenticated() → Verifica si hay sesión activa
- logout() → Limpia localStorage y redirige a /login
- getAuthHeaders() → Retorna headers con Authorization: Bearer {token}
```

---

### **TAREA 2: Wrapper de API Autenticado** ⭐

Crea `src/services/apiClient.ts`:

```typescript
// Wrapper que:
- Agrega automáticamente Authorization header a TODAS las requests
- Maneja error 401 → Redirige a /login
- Maneja error 403 → Muestra mensaje de permisos
- Usa fetch o axios (con interceptores)
```

**Ejemplo de uso esperado:**
```typescript
const response = await authenticatedFetch('/api/payments/process', {
  method: 'POST',
  body: JSON.stringify(purchaseData)
});
```

---

### **TAREA 3: Servicio de Pagos** ⭐

Crea `src/services/paymentsApi.ts` usando **PAYMENT_SYSTEM_GUIDE.md**:

```typescript
// Debe incluir estas funciones:

// 1. Obtener métodos de pago del usuario
getPaymentMethods(userId: number): Promise<PaymentMethodDto[]>

// 2. Procesar pago completo
processPurchase(data: CreatePurchaseDto): Promise<PaymentResponseDto>

// 3. Obtener detalle de compra
getPurchaseDetails(purchaseNumber: string): Promise<PurchaseDto>

// 4. Obtener historial de compras
getUserPurchases(userId: number): Promise<PurchaseDto[]>
```

**Tipos TypeScript (copiar de PAYMENT_SYSTEM_GUIDE.md):**
```typescript
interface CreatePurchaseDto {
  sessionId: string;
  userId: number;
  paymentMethodId: number;
  amount: number;
  items: PurchaseItemRequestDto[];
}

interface PurchaseItemRequestDto {
  itemType: 'TICKET' | 'CONCESSION';
  description: string;
  quantity: number;
  unitPrice: number;
  concessionProductId?: number;
  seatIdentifiers?: string;
}

// ... (resto de interfaces en PAYMENT_SYSTEM_GUIDE.md)
```

---

### **TAREA 4: Actualizar Butacas.tsx** ⚠️

Modifica la página de butacas para:

1. **Guardar sessionId después de reservar:**
```typescript
// Después de POST /api/seat-reservations/{showtimeId}
const { sessionId } = await response.json();
localStorage.setItem('seatReservationSession', JSON.stringify({
  sessionId,
  showtimeId,
  selectedSeats: [...], // Array con id, identifier, price de cada asiento
  expiryTime: new Date(Date.now() + 15 * 60 * 1000).toISOString()
}));
```

2. **Mostrar temporizador de 15 minutos:**
- Cuenta regresiva visible para el usuario
- Al expirar → Redirigir a selección de butacas

3. **Al hacer clic en "Continuar":**
- Validar que haya sessionId guardado
- Navegar a `/dulceria` o `/carrito-total`

---

### **TAREA 5: Página de Dulcería** ⚠️ (Si no existe)

Crea `src/pages/Dulceria.tsx`:

```typescript
// Debe:
1. Obtener productos de confitería desde /api/concession-products
2. Permitir seleccionar cantidad de cada producto
3. Calcular subtotales en tiempo real
4. Guardar selección en localStorage:
   {
     concessionItems: [
       { productId: 1, name: "Popcorn", quantity: 2, unitPrice: 8.00, subtotal: 16.00 }
     ]
   }
5. Botón "Continuar al carrito" → Navega a /carrito-total
```

---

### **TAREA 6: Página carrito-total.tsx** ⭐ (PRINCIPAL)

Crea o completa `src/pages/carrito-total.tsx` con esta funcionalidad:

#### **6.1 Estado y Carga de Datos**
```typescript
// Cargar de localStorage:
const movieSelection = JSON.parse(localStorage.getItem('movieSelection') || '{}');
const seatReservation = JSON.parse(localStorage.getItem('seatReservationSession') || '{}');
const concessionSelection = JSON.parse(localStorage.getItem('concessionSelection') || '{}');

// Validar que existan todos los datos necesarios
if (!seatReservation.sessionId) {
  // Redirigir a /butacas con mensaje de error
}
```

#### **6.2 Resumen Visual**
Mostrar en la UI:

```
📽️ RESUMEN DE TU COMPRA

Película: Los 4 Fantásticos
Cine: Cineplus Jockey Plaza
Sala: Sala 3
Fecha: Sábado 25 de Noviembre 2025
Horario: 18:30
Formato: IMAX 3D

🎟️ ENTRADAS (3)
  - Asiento A1 ................ S/ 15.00
  - Asiento A2 ................ S/ 15.00
  - Asiento A3 ................ S/ 15.00
                            Subtotal: S/ 45.00

🍿 DULCERÍA (2 items)
  - Popcorn Grande (x2) ....... S/ 16.00
  - Coca-Cola (x2) ............ S/ 11.00
                            Subtotal: S/ 27.00

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
TOTAL A PAGAR:                  S/ 72.00
```

#### **6.3 Selección de Método de Pago**
```typescript
// Cargar métodos de pago del usuario
useEffect(() => {
  const userId = AuthService.getUserId();
  getPaymentMethods(userId).then(setPaymentMethods);
}, []);

// UI: Radio buttons o tarjetas para seleccionar
<select value={selectedPaymentMethodId} onChange={...}>
  {paymentMethods.map(pm => (
    <option value={pm.id}>
      {pm.type} - **** {pm.maskedCardNumber.slice(-4)}
    </option>
  ))}
</select>
```

#### **6.4 Botón de Pago**
```typescript
const handlePagar = async () => {
  const userId = AuthService.getUserId();
  
  // Construir items para el backend
  const items: PurchaseItemRequestDto[] = [
    // Items de TICKETS (uno por asiento)
    ...seatReservation.selectedSeats.map(seat => ({
      itemType: 'TICKET' as const,
      description: `Entrada - ${movieSelection.theaterName}, Asiento ${seat.identifier}`,
      quantity: 1,
      unitPrice: seat.price,
      seatIdentifiers: seat.identifier
    })),
    
    // Items de CONCESSION
    ...concessionSelection.items.map(item => ({
      itemType: 'CONCESSION' as const,
      description: item.name,
      quantity: item.quantity,
      unitPrice: item.unitPrice,
      concessionProductId: item.productId
    }))
  ];
  
  // Calcular total
  const amount = items.reduce((sum, item) => sum + (item.quantity * item.unitPrice), 0);
  
  // Request completo
  const purchaseData: CreatePurchaseDto = {
    sessionId: seatReservation.sessionId,
    userId: userId,
    paymentMethodId: selectedPaymentMethodId,
    amount: amount,
    items: items
  };
  
  try {
    const response = await processPurchase(purchaseData);
    
    if (response.success) {
      // Guardar purchaseNumber
      localStorage.setItem('lastPurchaseNumber', response.purchaseNumber);
      
      // Limpiar datos temporales
      localStorage.removeItem('seatReservationSession');
      localStorage.removeItem('concessionSelection');
      
      // Navegar a dashboard
      navigate(`/dashboard?purchase=${response.purchaseNumber}`);
    }
  } catch (error) {
    if (error.status === 401) {
      // JWT expirado → Redirigir a login
      AuthService.logout();
    } else {
      // Mostrar mensaje de error
      alert('Error al procesar el pago: ' + error.message);
    }
  }
};
```

---

### **TAREA 7: Dashboard de Confirmación** ⭐ (PRINCIPAL)

Crea `src/pages/Dashboard.tsx`:

#### **7.1 Cargar Detalle de Compra**
```typescript
const [purchaseDetails, setPurchaseDetails] = useState<PurchaseDto | null>(null);
const searchParams = new URLSearchParams(location.search);
const purchaseNumber = searchParams.get('purchase') || localStorage.getItem('lastPurchaseNumber');

useEffect(() => {
  if (purchaseNumber) {
    getPurchaseDetails(purchaseNumber).then(setPurchaseDetails);
  }
}, [purchaseNumber]);
```

#### **7.2 UI del Dashboard**
Mostrar:

```
✅ ¡COMPRA EXITOSA!

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🎫 TICKET DIGITAL
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Número de Orden: CIN-20251120153045-A7B3C9D1

[QR CODE AQUÍ - generado con librería react-qr-code]

📽️ DETALLES DE LA FUNCIÓN
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Película: {purchaseDetails.movieTitle}
Cine: {purchaseDetails.cinemaName}
Sala: {purchaseDetails.theaterName}
Fecha: {purchaseDetails.showDate}
Horario: {purchaseDetails.showTime}
Formato: {purchaseDetails.format}

🎟️ TUS ENTRADAS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
{purchaseDetails.items
  .filter(item => item.itemType === 'TICKET')
  .map(item => `${item.description} - S/ ${item.subtotal}`)}

🍿 PRODUCTOS ADICIONALES
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
{purchaseDetails.items
  .filter(item => item.itemType === 'CONCESSION')
  .map(item => `${item.description} (x${item.quantity}) - S/ ${item.subtotal}`)}

💳 MÉTODO DE PAGO
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
{purchaseDetails.paymentMethodType}
{purchaseDetails.maskedCardNumber}

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
TOTAL PAGADO: S/ {purchaseDetails.totalAmount}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Fecha de compra: {purchaseDetails.purchaseDate}
Estado: {purchaseDetails.status}

[Botón: Descargar Ticket PDF]
[Botón: Ver mis compras]
[Botón: Volver al inicio]
```

#### **7.3 Funcionalidades Adicionales**
```typescript
// 1. QR Code
import QRCode from 'react-qr-code';
<QRCode value={purchaseNumber} size={200} />

// 2. Descargar PDF (opcional)
import jsPDF from 'jspdf';
const downloadPDF = () => {
  const doc = new jsPDF();
  doc.text(`Número de orden: ${purchaseNumber}`, 10, 10);
  // ... agregar más contenido
  doc.save(`ticket-${purchaseNumber}.pdf`);
};

// 3. Ver historial
const navigate = useNavigate();
<button onClick={() => navigate('/mis-compras')}>Ver mis compras</button>
```

---

### **TAREA 8: Página Mis Compras** (Opcional)

Crea `src/pages/MisCompras.tsx`:

```typescript
// Listar historial completo del usuario
const userId = AuthService.getUserId();
const purchases = await getUserPurchases(userId);

// UI: Tarjetas de cada compra
purchases.map(purchase => (
  <div className="purchase-card" onClick={() => navigate(`/dashboard?purchase=${purchase.purchaseNumber}`)}>
    <h3>{purchase.movieTitle}</h3>
    <p>{purchase.cinemaName} - {purchase.showDate}</p>
    <p>Total: S/ {purchase.totalAmount}</p>
    <span className={`status ${purchase.status}`}>{purchase.status}</span>
  </div>
))
```

---

## 🔒 REQUISITOS CRÍTICOS DE SEGURIDAD

### ⚠️ TODAS las requests de pagos/compras DEBEN incluir JWT:

```typescript
// ❌ MAL:
fetch('/api/payments/process', { method: 'POST', body: ... })

// ✅ BIEN:
fetch('/api/payments/process', {
  method: 'POST',
  headers: AuthService.getAuthHeaders(), // Incluye Authorization: Bearer {token}
  body: ...
})
```

### ⚠️ Validar autenticación antes de acceder a páginas protegidas:

```typescript
// En cada página de carrito/dashboard
useEffect(() => {
  if (!AuthService.isAuthenticated()) {
    navigate('/login');
  }
}, []);
```

---

## 📦 DEPENDENCIAS NECESARIAS

```bash
npm install react-qr-code      # Para QR codes
npm install jspdf              # Para PDFs (opcional)
npm install axios              # Si prefieres axios sobre fetch
```

---

## 🎨 CONSIDERACIONES DE UX

1. **Feedback visual:**
   - Loading spinners durante requests
   - Mensajes de éxito/error claros
   - Deshabilitar botón de pago mientras procesa

2. **Validaciones:**
   - Verificar que sessionId no haya expirado antes de pagar
   - Mostrar temporizador de 15 minutos en todas las páginas del flujo
   - Validar que el método de pago esté seleccionado

3. **Navegación:**
   - Breadcrumb: Butacas → Dulcería → Carrito → Dashboard
   - Botón "Atrás" para regresar a paso anterior

4. **Responsividad:**
   - UI adaptable a móvil/tablet/desktop
   - QR code visible y escaneable en móviles

---

## 📝 ESTRUCTURA DE ARCHIVOS ESPERADA

```
src/
├── services/
│   ├── authService.ts          ⭐ Gestión de JWT
│   ├── apiClient.ts            ⭐ Wrapper autenticado
│   ├── paymentsApi.ts          ⭐ Endpoints de pagos
│   ├── showtimesApi.ts         (Ya existe - verificar)
│   └── concessionApi.ts        (Crear si no existe)
├── pages/
│   ├── Butacas.tsx             (Modificar)
│   ├── Dulceria.tsx            ⭐ Crear
│   ├── carrito-total.tsx       ⭐ Completar/Crear
│   ├── Dashboard.tsx           ⭐ Crear
│   └── MisCompras.tsx          (Opcional)
├── types/
│   └── payment.types.ts        ⭐ Interfaces de PAYMENT_SYSTEM_GUIDE.md
└── utils/
    ├── formatters.ts           (Formatos de fecha/moneda)
    └── validators.ts           (Validaciones de formulario)
```

---

## ✅ CHECKLIST DE VALIDACIÓN

Después de implementar, verificar que:

- [ ] Login funciona y guarda JWT en localStorage
- [ ] Todas las requests protegidas incluyen Authorization header
- [ ] Error 401 redirige a login automáticamente
- [ ] SessionId se guarda después de reservar butacas
- [ ] Temporizador de 15 minutos es visible
- [ ] Carrito muestra resumen correcto con precios
- [ ] Request a `/api/payments/process` incluye todos los campos requeridos
- [ ] Items se separan correctamente: TICKET vs CONCESSION
- [ ] Cada ticket tiene `seatIdentifiers` individual ("A1", no "A1,A2")
- [ ] Dashboard muestra purchaseNumber y todos los detalles
- [ ] QR code se genera correctamente con purchaseNumber
- [ ] Datos se limpian de localStorage después del pago

---

## 🚀 ORDEN DE IMPLEMENTACIÓN RECOMENDADO

1. **AuthService** → Base para todo
2. **ApiClient** → Wrapper autenticado
3. **PaymentsApi** → Servicios de pagos
4. **Actualizar Butacas.tsx** → Guardar sessionId
5. **Dulceria.tsx** → Selección de productos
6. **carrito-total.tsx** → Resumen y pago
7. **Dashboard.tsx** → Confirmación

---

## 💡 NOTAS FINALES PARA COPILOT

- **Lee primero los 3 documentos marcados con ⭐ CRÍTICO**
- **Usa las interfaces TypeScript de PAYMENT_SYSTEM_GUIDE.md tal cual están**
- **Sigue EXACTAMENTE el flujo de FLUJO_COMPLETO_VENTA.md**
- **NO inventes endpoints - usa solo los documentados**
- **Incluye comentarios explicativos en código complejo**
- **Usa nombres de variables descriptivos en español cuando tenga sentido**

---

**🎬 ¡Estoy listo para generar código! Dame el visto bueno para empezar.** 🚀
