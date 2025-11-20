# 🔒 Seguridad y Autenticación - Sistema CinePlus

## ⚠️ IMPORTANTE: Autenticación JWT Obligatoria

**El backend tiene Spring Security activo con autenticación JWT.** TODAS las operaciones de compra, consulta de historial y gestión de métodos de pago **requieren un token JWT válido**.

---

## 🔐 Flujo Completo de Autenticación

### 1. Login (Obtener JWT)

Antes de realizar cualquier operación, el usuario **DEBE** hacer login:

```typescript
// Frontend: Login
const loginResponse = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ 
    username: 'user@example.com', 
    password: 'password123' 
  })
});

if (!loginResponse.ok) {
  throw new Error('Credenciales inválidas');
}

const { token, userId, username } = await loginResponse.json();

// Guardar el token para requests futuras
localStorage.setItem('jwt_token', token);
localStorage.setItem('user_id', userId);
localStorage.setItem('username', username);
```

**Respuesta Esperada:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjk4NzY1NDMyLCJleHAiOjE2OTg4NTE4MzJ9...",
  "userId": 1,
  "username": "user@example.com",
  "roles": ["ROLE_USER"]
}
```

---

### 2. Incluir JWT en TODAS las Requests

Una vez obtenido el token, **DEBE incluirse** en el header `Authorization` de cada request:

```typescript
const token = localStorage.getItem('jwt_token');

const response = await fetch('http://localhost:8080/api/payments/process', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}` // ⭐ OBLIGATORIO
  },
  body: JSON.stringify({
    sessionId: 'sess-123',
    userId: 1,
    paymentMethodId: 1,
    amount: 45.50,
    items: [...]
  })
});

// Sin este header, obtendrás: 401 Unauthorized
```

---

## 🛡️ Validaciones de Seguridad Implementadas

### En el Backend (Ya implementado) ✅

1. **JWT Token Válido**: Spring Security valida automáticamente cada request
   - Verifica la firma del token
   - Verifica que no haya expirado
   - Extrae el usuario autenticado

2. **Propiedad del Método de Pago**: 
   ```java
   // En PurchaseServiceImpl.processPurchase()
   if (!paymentMethod.getUser().getId().equals(user.getId())) {
       throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
           "Payment method does not belong to the user");
   }
   ```

3. **Sesión de Reserva Activa**:
   ```java
   SeatReservation reservation = seatReservationRepository
       .findBySessionId(sessionId)
       .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
   
   if (reservation.getExpiryTime().isBefore(LocalDateTime.now())) {
       throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation expired");
   }
   ```

4. **Validación de Montos**:
   ```java
   BigDecimal calculatedAmount = calculateTotalAmount(request.getItems());
   if (calculatedAmount.compareTo(request.getAmount()) != 0) {
       throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount mismatch");
   }
   ```

5. **Datos Enmascarados**: 
   - Las tarjetas se devuelven enmascaradas: `**** **** **** 1234`
   - Nunca se expone el número completo de tarjeta

---

## 📱 Implementación en Frontend

### Opción 1: Service Helper con Manejo Automático de JWT

```typescript
// services/authService.ts
export class AuthService {
  private static readonly TOKEN_KEY = 'jwt_token';
  private static readonly USER_ID_KEY = 'user_id';
  
  static getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }
  
  static getUserId(): number | null {
    const id = localStorage.getItem(this.USER_ID_KEY);
    return id ? parseInt(id) : null;
  }
  
  static setSession(token: string, userId: number): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.USER_ID_KEY, userId.toString());
  }
  
  static clearSession(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_ID_KEY);
  }
  
  static isAuthenticated(): boolean {
    return this.getToken() !== null;
  }
  
  static getAuthHeaders(): HeadersInit {
    const token = this.getToken();
    return {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    };
  }
  
  static async login(username: string, password: string): Promise<void> {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    
    if (!response.ok) {
      throw new Error('Login failed');
    }
    
    const data = await response.json();
    this.setSession(data.token, data.userId);
  }
  
  static logout(): void {
    this.clearSession();
    window.location.href = '/login';
  }
}
```

---

### Opción 2: Fetch Wrapper con Manejo de Errores

```typescript
// services/apiClient.ts
export async function authenticatedFetch(
  url: string, 
  options: RequestInit = {}
): Promise<Response> {
  const token = AuthService.getToken();
  
  if (!token) {
    window.location.href = '/login';
    throw new Error('No authentication token');
  }
  
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
      ...options.headers
    }
  });
  
  // Manejar errores de autenticación
  if (response.status === 401) {
    AuthService.clearSession();
    alert('Sesión expirada. Por favor, inicia sesión nuevamente.');
    window.location.href = '/login';
    throw new Error('Unauthorized');
  }
  
  if (response.status === 403) {
    alert('No tienes permisos para realizar esta acción.');
    throw new Error('Forbidden');
  }
  
  return response;
}

// Uso:
const response = await authenticatedFetch(
  'http://localhost:8080/api/payments/process',
  {
    method: 'POST',
    body: JSON.stringify(purchaseData)
  }
);
```

---

### Opción 3: Axios Interceptor (Recomendado)

```typescript
// services/api.ts
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Request interceptor: Agrega JWT automáticamente
api.interceptors.request.use(
  (config) => {
    const token = AuthService.getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: Maneja errores de autenticación
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      AuthService.clearSession();
      alert('Sesión expirada. Redirigiendo a login...');
      window.location.href = '/login';
    } else if (error.response?.status === 403) {
      alert('No tienes permisos para esta acción.');
    }
    return Promise.reject(error);
  }
);

export default api;

// Uso simplificado:
import api from './services/api';

// Ya no necesitas agregar headers manualmente
const response = await api.post('/payments/process', purchaseData);
const purchases = await api.get(`/users/${userId}/purchases`);
```

---

## 🧪 Ejemplo Completo: Login → Compra

```typescript
// 1. Usuario hace login
async function handleLogin(username: string, password: string) {
  try {
    await AuthService.login(username, password);
    console.log('✅ Login exitoso');
    window.location.href = '/home';
  } catch (error) {
    alert('❌ Credenciales inválidas');
  }
}

// 2. Usuario reserva asientos (requiere JWT)
async function reserveSeats(showtimeId: number, seatIds: string[]) {
  const userId = AuthService.getUserId();
  
  const response = await authenticatedFetch(
    `http://localhost:8080/api/seat-reservations/${showtimeId}`,
    {
      method: 'POST',
      body: JSON.stringify({
        seatIdentifiers: seatIds,
        userId: userId
      })
    }
  );
  
  const { sessionId } = await response.json();
  return sessionId;
}

// 3. Usuario procesa el pago (requiere JWT)
async function processPurchase(sessionId: string, paymentMethodId: number, items: any[]) {
  const userId = AuthService.getUserId();
  const amount = items.reduce((sum, item) => sum + (item.quantity * item.unitPrice), 0);
  
  const response = await authenticatedFetch(
    'http://localhost:8080/api/payments/process',
    {
      method: 'POST',
      body: JSON.stringify({
        sessionId,
        userId,
        paymentMethodId,
        amount,
        items
      })
    }
  );
  
  const result = await response.json();
  console.log('✅ Compra exitosa:', result.purchaseNumber);
  return result;
}

// 4. Flujo completo
async function completePurchaseFlow() {
  // Paso 1: Login
  await handleLogin('user@example.com', 'password123');
  
  // Paso 2: Reservar asientos
  const sessionId = await reserveSeats(1, ['A1', 'A2']);
  
  // Paso 3: Procesar pago
  const purchase = await processPurchase(sessionId, 1, [
    {
      itemType: 'TICKET',
      description: 'Entrada - Sala 1, Asiento A1',
      quantity: 2,
      unitPrice: 15.00,
      seatIdentifiers: 'A1,A2'
    }
  ]);
  
  alert(`¡Compra exitosa! Número: ${purchase.purchaseNumber}`);
}
```

---

## 🚨 Manejo de Errores Comunes

### 1. Error 401 Unauthorized

**Causa:** JWT no incluido o inválido/expirado

**Solución:**
```typescript
if (response.status === 401) {
  // Token inválido o expirado
  AuthService.clearSession();
  alert('Tu sesión ha expirado. Por favor, inicia sesión nuevamente.');
  window.location.href = '/login';
}
```

---

### 2. Error 403 Forbidden

**Causa:** Usuario autenticado pero sin permisos (ej: intentar usar método de pago de otro usuario)

**Solución:**
```typescript
if (response.status === 403) {
  alert('No tienes permisos para realizar esta acción.');
  // No cerrar sesión, el token es válido
}
```

---

### 3. Error 404 Not Found

**Causa:** Sesión de reserva no encontrada o expirada

**Solución:**
```typescript
if (response.status === 404) {
  alert('Tu reserva ha expirado. Por favor, reserva nuevamente tus asientos.');
  window.location.href = '/seat-selection';
}
```

---

## 📋 Checklist de Seguridad para Frontend

- [ ] Implementar login con almacenamiento de JWT
- [ ] Incluir `Authorization: Bearer {token}` en TODAS las requests protegidas
- [ ] Manejar error 401 → Redirigir a login y limpiar localStorage
- [ ] Manejar error 403 → Mostrar mensaje de permisos
- [ ] Validar que el userId del token coincida con el usado en requests
- [ ] NO almacenar información sensible (números de tarjeta completos)
- [ ] Implementar logout que limpie el token
- [ ] Agregar validación `isAuthenticated()` antes de acceder a rutas protegidas
- [ ] Usar HTTPS en producción (nunca HTTP)
- [ ] Implementar refresh token para sesiones largas (opcional)

---

## 🔑 Endpoints de Autenticación Disponibles

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

**Respuesta 200 OK:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": 1,
  "username": "user@example.com"
}
```

---

### Register (Opcional, si está implementado)
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "newuser@example.com",
  "password": "password123",
  "firstName": "Juan",
  "lastName": "Pérez",
  "nationalId": "12345678",
  "email": "newuser@example.com",
  "phone": "987654321"
}
```

---

## 💡 Mejores Prácticas

1. **Almacenar JWT de forma segura**
   - ✅ `localStorage` para SPA simples
   - ✅ `sessionStorage` si quieres que expire al cerrar el navegador
   - ❌ NUNCA en cookies sin `httpOnly` flag

2. **Validar autenticación en cada ruta**
   ```typescript
   // React Router example
   const ProtectedRoute = ({ children }) => {
     if (!AuthService.isAuthenticated()) {
       return <Navigate to="/login" />;
     }
     return children;
   };
   ```

3. **Mostrar estado de autenticación**
   ```typescript
   const [user, setUser] = useState(null);
   
   useEffect(() => {
     if (AuthService.isAuthenticated()) {
       setUser(AuthService.getUsername());
     }
   }, []);
   ```

4. **Logout limpio**
   ```typescript
   function handleLogout() {
     AuthService.clearSession();
     setUser(null);
     window.location.href = '/login';
   }
   ```

---

## 🎯 Resumen

✅ **Backend:** Spring Security con JWT ya está configurado y activo
✅ **Frontend:** Debe implementar login, almacenar JWT y enviarlo en cada request
✅ **Seguridad:** Todas las validaciones críticas están en el backend
✅ **Flujo:** Login → Obtener JWT → Incluir en headers → Realizar operaciones

**Sin JWT = 401 Unauthorized** 🔒

---

**🎬 Sistema CinePlus - Seguridad y Autenticación** ✅
