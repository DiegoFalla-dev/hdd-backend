# Sistema de Usuarios y Autenticación - CinePlus

## 📋 Descripción General

El sistema de usuarios gestiona la autenticación, autorización, perfiles de usuario y métodos de pago. Utiliza JWT (JSON Web Tokens) para la autenticación segura y encriptación para datos sensibles.

---

## 🏗️ Estructura de Datos

### Entidad `User`

```java
@Entity
@Table(name = "users")
public class User {
    private Long id;
    private String username;            // Usuario único
    private String nationalId;          // DNI/Documento (no encriptado)
    private String firstName;           // Nombre
    private String lastName;            // Apellido
    private String email;               // Email único
    private String birthDate;           // Fecha de nacimiento (ISO string)
    private String gender;              // Género
    private String avatar;              // URL de avatar
    private String favoriteCinema;      // Cine favorito
    private String phoneEncrypted;      // Teléfono (encriptado)
    private String passwordHash;        // Contraseña (hasheada)
    private Set<Role> roles;            // Roles del usuario
    private Set<PaymentMethod> paymentMethods; // Métodos de pago
}
```

### Entidad `Role`

```java
@Entity
@Table(name = "roles")
public class Role {
    private Long id;
    private String name;                // ROLE_USER, ROLE_ADMIN, ROLE_EMPLOYEE
}
```

### Entidad `PaymentMethod`

```java
@Entity
@Table(name = "payment_methods")
public class PaymentMethod {
    private Long id;
    private User user;                  // Usuario propietario
    private String name;                // Nombre descriptivo
    private String cardNumberEncrypted; // Número de tarjeta (encriptado)
    private String cardHolderEncrypted; // Titular (encriptado)
    private String cciEncrypted;        // CCI (encriptado)
    private String expiryEncrypted;     // Vencimiento (encriptado)
    private String phoneEncrypted;      // Teléfono (encriptado)
    private String type;                // CARD o YAPE
    private String verificationCodeEncrypted; // Código de verificación (encriptado)
    private Boolean isDefault;          // Si es método predeterminado
}
```

---

## 🔐 Roles y Permisos

### ROLE_USER (Usuario Regular)
- Comprar tickets
- Ver películas y funciones
- Gestionar perfil personal
- Ver historial de compras propio
- Gestionar métodos de pago propios

### ROLE_EMPLOYEE (Empleado)
- Todo lo de ROLE_USER
- Validar tickets (marcar como usados)
- Ver códigos QR de tickets

### ROLE_ADMIN (Administrador)
- Todo lo de ROLE_EMPLOYEE
- Crear/editar/eliminar películas
- Crear/editar/eliminar cines y salas
- Crear/editar/eliminar funciones
- Crear/editar/eliminar promociones
- Ver todas las órdenes
- Gestionar usuarios
- Cambiar estados de órdenes

---

## 🔌 API Endpoints

### **Autenticación**

#### Registrar Usuario
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "nationalId": "12345678",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "SecureP@ss123",
  "birthDate": "1990-05-15",
  "gender": "M",
  "phone": "+51987654321"
}
```
**Respuesta:**
```json
{
  "message": "User registered successfully!"
}
```

#### Iniciar Sesión
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "SecureP@ss123"
}
```
**Respuesta:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "userId": 25,
  "username": "johndoe",
  "email": "john.doe@example.com",
  "roles": ["ROLE_USER"]
}
```

#### Refrescar Token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Cerrar Sesión
```http
POST /api/auth/logout
Authorization: Bearer {token}
```

---

### **Usuarios**

#### Obtener Nombre de Usuario
```http
GET /api/users/{id}/name
```
**Respuesta:**
```json
{
  "firstName": "John",
  "lastName": "Doe"
}
```

#### Obtener Compras de Usuario
```http
GET /api/users/{id}/purchases
Authorization: Bearer {token}
```
**Permisos**: El propio usuario o ADMIN.

**Respuesta:**
```json
[
  {
    "id": 1001,
    "orderDate": "2024-12-08T14:30:00",
    "totalAmount": 125.50,
    "orderStatus": "COMPLETED",
    "itemCount": 2
  },
  {
    "id": 1002,
    "orderDate": "2024-12-05T19:15:00",
    "totalAmount": 78.00,
    "orderStatus": "COMPLETED",
    "itemCount": 3
  }
]
```

---

### **Métodos de Pago**

#### Listar Métodos de Pago
```http
GET /api/users/{userId}/payment-methods
Authorization: Bearer {token}
```
**Respuesta:**
```json
[
  {
    "id": 5,
    "name": "Tarjeta •••• 4532",
    "type": "CARD",
    "isDefault": true
  },
  {
    "id": 6,
    "name": "Yape •••• 7890",
    "type": "YAPE",
    "isDefault": false
  }
]
```

#### Agregar Método de Pago
```http
POST /api/users/{userId}/payment-methods
Authorization: Bearer {token}
Content-Type: application/json

{
  "type": "CARD",
  "cardNumber": "4532123456789012",
  "cardHolder": "JOHN DOE",
  "cci": "00212345678901234567",
  "expiry": "12/25",
  "isDefault": true
}
```

#### Agregar Yape
```http
POST /api/users/{userId}/payment-methods
Authorization: Bearer {token}
Content-Type: application/json

{
  "type": "YAPE",
  "phone": "+51987654321",
  "verificationCode": "123456",
  "isDefault": false
}
```

#### Actualizar Método de Pago
```http
PUT /api/users/{userId}/payment-methods/{paymentMethodId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "cci": "00212345678901234567",
  "isDefault": true
}
```

#### Establecer como Predeterminado
```http
PATCH /api/users/{userId}/payment-methods/{paymentMethodId}/default
Authorization: Bearer {token}
```

#### Eliminar Método de Pago
```http
DELETE /api/users/{userId}/payment-methods/{paymentMethodId}
Authorization: Bearer {token}
```

---

## 🔒 Seguridad

### Encriptación de Datos Sensibles

Los siguientes campos se encriptan antes de guardar en la base de datos:
- Número de tarjeta
- Titular de tarjeta
- CCI
- Fecha de vencimiento
- Teléfono
- Código de verificación (Yape)

### Hash de Contraseñas

Las contraseñas se hashean usando **BCrypt** antes de almacenar:
```java
passwordHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
```

### JSON Web Tokens (JWT)

#### Access Token
- **Duración**: 24 horas (configurable)
- **Uso**: Autenticación en cada request
- **Contenido**: userId, username, email, roles

#### Refresh Token
- **Duración**: 7 días (configurable)
- **Uso**: Obtener nuevo access token sin volver a loguearse
- **Contenido**: userId

### Configuración JWT (application.properties)
```properties
jwt.secret=UnaClaveSecretaMuyLargaYSeguraParaJWTQueDebeSerDeAlMenos256Bits
jwt.expiration=86400000
```

---

## 🔄 Flujo de Autenticación

```
┌─────────────────────────────────────────────────┐
│ 1. Usuario se registra                          │
│    POST /api/auth/register                      │
│    - Valida datos                               │
│    - Hashea contraseña                          │
│    - Asigna ROLE_USER por defecto               │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 2. Usuario inicia sesión                        │
│    POST /api/auth/login                         │
│    - Valida credenciales                        │
│    - Genera accessToken y refreshToken          │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 3. Usuario usa accessToken en requests          │
│    Authorization: Bearer {accessToken}          │
└────────────────┬────────────────────────────────┘
                 │
          ┌──────┴──────┐
          │             │
          ▼             ▼
    Token válido   Token expirado
          │             │
          │             ▼
          │    POST /api/auth/refresh
          │    { refreshToken }
          │             │
          │             ▼
          │    Nuevo accessToken
          │             │
          └─────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│ 4. Usuario cierra sesión                        │
│    POST /api/auth/logout                        │
│    - Invalida sesión en servidor                │
└─────────────────────────────────────────────────┘
```

---

## 💳 Tipos de Métodos de Pago

### CARD (Tarjeta de Crédito/Débito)
**Campos requeridos:**
- `cardNumber`: Número de tarjeta (16 dígitos)
- `cardHolder`: Nombre del titular
- `cci`: Código de cuenta interbancario
- `expiry`: Fecha de vencimiento (MM/YY)

**Generación de nombre:**
```
"Tarjeta •••• {últimos4dígitos}"
Ejemplo: "Tarjeta •••• 4532"
```

### YAPE (Pago Móvil)
**Campos requeridos:**
- `phone`: Número de celular
- `verificationCode`: Código de verificación de 6 dígitos

**Generación de nombre:**
```
"Yape •••• {últimos4dígitosCelular}"
Ejemplo: "Yape •••• 7890"
```

---

## 🛡️ Validaciones

### Validaciones de Registro
- ✅ `username` único
- ✅ `email` único y formato válido
- ✅ `nationalId` único
- ✅ `password` mínimo 8 caracteres, mayúscula, minúscula, número
- ✅ `birthDate` formato ISO (YYYY-MM-DD)
- ✅ Todos los campos requeridos presentes

### Validaciones de Login
- ✅ `email` existe
- ✅ `password` coincide con hash almacenado
- ✅ Usuario activo (si hay sistema de activación)

### Validaciones de Métodos de Pago
- ✅ Usuario propietario
- ✅ Si es CARD, validar formato de número de tarjeta
- ✅ Si es YAPE, validar formato de teléfono
- ✅ Solo un método puede ser `isDefault=true` a la vez

---

## 🔐 Permisos Basados en Roles

### Autorización con `@PreAuthorize`

```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> adminOnlyEndpoint() { ... }

@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
public ResponseEntity<?> staffEndpoint() { ... }

@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> authenticatedEndpoint() { ... }

@PreAuthorize("hasRole('ADMIN') or @userSecurity.hasUserId(authentication, #userId)")
public ResponseEntity<?> ownDataOrAdmin(Long userId) { ... }
```

---

## 📊 Consultas Útiles

### Usuarios más activos
```sql
SELECT 
    u.id,
    u.username,
    COUNT(o.id) as order_count,
    SUM(o.total_amount) as total_spent
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id
ORDER BY total_spent DESC
LIMIT 10;
```

### Usuarios por rol
```sql
SELECT r.name as role_name, COUNT(ur.user_id) as user_count
FROM roles r
LEFT JOIN user_roles ur ON r.id = ur.role_id
GROUP BY r.id;
```

### Métodos de pago por tipo
```sql
SELECT type, COUNT(*) as count
FROM payment_methods
GROUP BY type;
```

---

## 🚀 Mejoras Futuras

1. **Verificación de Email**
   - Enviar email de confirmación al registrarse
   - Activación de cuenta vía link

2. **Recuperación de Contraseña**
   - Solicitud de reset por email
   - Token temporal de un solo uso
   - Nueva contraseña

3. **Autenticación de Dos Factores (2FA)**
   - SMS
   - Authenticator app (Google Authenticator, Authy)

4. **OAuth2 / Social Login**
   - Login con Google
   - Login con Facebook
   - Login con Apple

5. **Perfil de Usuario Extendido**
   - Foto de perfil
   - Preferencias de películas
   - Notificaciones configurables
   - Idioma preferido

6. **Seguridad Avanzada**
   - Historial de inicios de sesión
   - Alertas de login desde dispositivo nuevo
   - Bloqueo temporal después de intentos fallidos
   - Blacklist de tokens revocados

7. **Gestión de Sesiones**
   - Ver sesiones activas
   - Cerrar sesión en otros dispositivos
   - Timeout de inactividad configurable

---

## ⚙️ Configuración

### application.properties

```properties
# JWT Configuration
jwt.secret=UnaClaveSecretaMuyLargaYSeguraParaJWTQueDebeSerDeAlMenos256Bits
jwt.expiration=86400000

# Session inactivity timeout in minutes
session.inactivity.minutes=5

# Security Configuration
spring.security.user.name=admin
spring.security.user.password=admin123
```

---

## 📚 Referencias

- **Entidad User**: `domain/entity/User.java`
- **Entidad Role**: `domain/entity/Role.java`
- **Entidad PaymentMethod**: `domain/entity/PaymentMethod.java`
- **DTOs**: `domain/dto/UserDto.java`, `domain/dto/LoginRequestDto.java`, `domain/dto/RegisterRequestDto.java`
- **Service Auth**: `persistence/service/impl/AuthServiceImpl.java`
- **Service User**: `persistence/service/impl/UserServiceImpl.java`
- **Service PaymentMethod**: `persistence/service/impl/PaymentMethodServiceImpl.java`
- **Controller Auth**: `web/controller/AuthController.java`
- **Controller User**: `web/controller/UserController.java`
- **Controller PaymentMethod**: `web/controller/PaymentMethodController.java`
- **JWT Service**: `web/security/jwt/JwtService.java`
- **Security Config**: `web/config/SecurityConfig.java`

---

## ❓ Preguntas Frecuentes

### ¿Cómo cambio mi contraseña?
Actualmente no hay endpoint. Futuro: `PATCH /api/users/{id}/password`.

### ¿Los datos de tarjetas son seguros?
Sí, se encriptan antes de guardar en la base de datos y solo se desencriptan cuando es necesario.

### ¿Cuánto dura una sesión?
El access token dura 24 horas. El refresh token dura 7 días.

### ¿Puedo tener múltiples métodos de pago?
Sí, sin límite. Solo uno puede ser predeterminado.

### ¿Qué pasa si olvido mi contraseña?
Actualmente no hay sistema de recuperación. Contactar a ADMIN.

---

**Última actualización:** Diciembre 2025  
**Versión:** 1.0
