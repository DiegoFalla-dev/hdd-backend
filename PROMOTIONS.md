# Sistema de Promociones - CinePlus

## 📋 Descripción General

El sistema de promociones permite gestionar cupones de descuento que los usuarios pueden aplicar al realizar compras de tickets y productos de confitería. El sistema incluye validaciones automáticas, control de uso y múltiples tipos de descuento.

---

## 🏗️ Estructura de Datos

### Entidad `Promotion`

```java
@Entity
@Table(name = "promotions")
public class Promotion {
    private Long id;
    private String code;              // Código único (ej: "VERANO2024")
    private String description;       // Descripción del descuento
    private DiscountType discountType; // PERCENTAGE o FIXED_AMOUNT
    private BigDecimal value;         // Valor del descuento
    private LocalDateTime startDate;  // Fecha de inicio
    private LocalDateTime endDate;    // Fecha de finalización
    private BigDecimal minAmount;     // Monto mínimo de compra
    private Integer maxUses;          // Límite de usos (opcional)
    private Integer currentUses;      // Contador de usos actuales
    private Boolean isActive;         // Estado activo/inactivo
}
```

### Tipos de Descuento

```java
public enum DiscountType {
    PERCENTAGE,    // Descuento porcentual (ej: 10% = 0.10)
    FIXED_AMOUNT   // Monto fijo (ej: $5.00)
}
```

---

## 💰 Cálculo de Descuentos

### Descuento Porcentual (PERCENTAGE)
```
Total con descuento = Total - (Total × valor)
Ejemplo: $100 - ($100 × 0.10) = $90
```

### Descuento Fijo (FIXED_AMOUNT)
```
Total con descuento = max(Total - valor, 0)
Ejemplo: $100 - $15 = $85
```

---

## ✅ Validaciones Automáticas

Cuando un usuario aplica un código de promoción, el sistema verifica:

1. **Código existe**: El código debe estar registrado en la base de datos
2. **Estado activo**: `isActive = true`
3. **Rango de fechas**: `startDate ≤ ahora ≤ endDate`
4. **Límite de usos**: `currentUses < maxUses` (si está definido)
5. **Monto mínimo**: `totalCompra ≥ minAmount` (si está definido)

Si todas las validaciones pasan, el descuento se aplica automáticamente.

---

## 🔄 Flujo de Aplicación

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Usuario crea orden con código promocional               │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. Sistema valida promoción (código, fechas, usos, monto)  │
└──────────────────────┬──────────────────────────────────────┘
                       │
          ┌────────────┴────────────┐
          │                         │
          ▼                         ▼
    ❌ Inválida              ✅ Válida
          │                         │
          │                         ▼
          │         ┌───────────────────────────────┐
          │         │ 3. Calcula descuento          │
          │         └────────┬──────────────────────┘
          │                  │
          │                  ▼
          │         ┌───────────────────────────────┐
          │         │ 4. Aplica al totalAmount      │
          │         └────────┬──────────────────────┘
          │                  │
          │                  ▼
          │         ┌───────────────────────────────┐
          │         │ 5. Incrementa currentUses     │
          │         └────────┬──────────────────────┘
          │                  │
          │                  ▼
          │         ┌───────────────────────────────┐
          │         │ 6. Guarda relación en Order   │
          │         └───────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────┐
│ Orden procesada (con o sin descuento)                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔌 API Endpoints

### Listar Todas las Promociones
```http
GET /api/promotions
```
**Respuesta:**
```json
[
  {
    "id": 1,
    "code": "VERANO2024",
    "description": "10% de descuento en verano",
    "discountType": "PERCENTAGE",
    "value": 0.10,
    "startDate": "2024-06-01T00:00:00",
    "endDate": "2024-08-31T23:59:59",
    "minAmount": 20.00,
    "maxUses": 1000,
    "currentUses": 245,
    "isActive": true
  }
]
```

### Obtener Promoción por ID
```http
GET /api/promotions/{id}
```

### Obtener Promoción Activa por Código
```http
GET /api/promotions/code/{code}
```
**Ejemplo:**
```http
GET /api/promotions/code/VERANO2024
```
Solo retorna promociones activas dentro del rango de fechas válido.

### Crear Promoción (ADMIN)
```http
POST /api/promotions
Authorization: Bearer {token}
Content-Type: application/json

{
  "code": "BLACKFRIDAY",
  "description": "25% de descuento Black Friday",
  "discountType": "PERCENTAGE",
  "value": 0.25,
  "startDate": "2024-11-29T00:00:00",
  "endDate": "2024-11-29T23:59:59",
  "minAmount": 50.00,
  "maxUses": 500,
  "isActive": true
}
```

### Actualizar Promoción (ADMIN)
```http
PUT /api/promotions/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "code": "BLACKFRIDAY",
  "description": "30% de descuento Black Friday",
  "discountType": "PERCENTAGE",
  "value": 0.30,
  ...
}
```

### Eliminar Promoción (ADMIN)
```http
DELETE /api/promotions/{id}
Authorization: Bearer {token}
```

---

## 🔒 Seguridad y Permisos

| Acción | Rol Requerido | Endpoint |
|--------|---------------|----------|
| Listar promociones | Público | `GET /api/promotions` |
| Ver promoción por código | Público | `GET /api/promotions/code/{code}` |
| Ver promoción por ID | Público | `GET /api/promotions/{id}` |
| Crear promoción | `ADMIN` | `POST /api/promotions` |
| Actualizar promoción | `ADMIN` | `PUT /api/promotions/{id}` |
| Eliminar promoción | `ADMIN` | `DELETE /api/promotions/{id}` |
| Aplicar promoción | Autenticado | Durante creación de orden |

---

## 📝 Ejemplos de Uso

### Ejemplo 1: Descuento Porcentual Simple
```json
{
  "code": "DESC10",
  "description": "10% de descuento",
  "discountType": "PERCENTAGE",
  "value": 0.10,
  "startDate": "2024-01-01T00:00:00",
  "endDate": "2024-12-31T23:59:59",
  "isActive": true
}
```
**Aplicación:** Compra de $100 → Descuento de $10 → **Total: $90**

### Ejemplo 2: Descuento Fijo con Monto Mínimo
```json
{
  "code": "AHORRA20",
  "description": "$20 de descuento en compras mayores a $50",
  "discountType": "FIXED_AMOUNT",
  "value": 20.00,
  "startDate": "2024-03-01T00:00:00",
  "endDate": "2024-03-31T23:59:59",
  "minAmount": 50.00,
  "isActive": true
}
```
**Aplicación:**
- Compra de $45 → ❌ No aplica (menor a $50)
- Compra de $70 → ✅ Descuento de $20 → **Total: $50**

### Ejemplo 3: Promoción con Límite de Usos
```json
{
  "code": "PRIMEROS100",
  "description": "50% de descuento para los primeros 100 usuarios",
  "discountType": "PERCENTAGE",
  "value": 0.50,
  "startDate": "2024-06-15T00:00:00",
  "endDate": "2024-06-15T23:59:59",
  "maxUses": 100,
  "currentUses": 0,
  "isActive": true
}
```
**Aplicación:**
- Usuario #1-100 → ✅ 50% de descuento
- Usuario #101+ → ❌ No aplica (límite alcanzado)

---

## 🛡️ Protecciones Implementadas

### 1. Código Único
```java
// Al crear una promoción, se valida que el código no exista
if (promotionRepository.findByCode(code).isPresent()) {
    throw new IllegalArgumentException("Ya existe una promoción con el código");
}
```

### 2. Control de Usos
```java
// El campo currentUses NO se actualiza desde el DTO
// Solo se incrementa automáticamente al aplicar la promoción
appliedPromotion.setCurrentUses(appliedPromotion.getCurrentUses() + 1);
```

### 3. Validación de Fechas
```java
// Solo promociones dentro del rango de fechas son válidas
if (now.isBefore(startDate) || now.isAfter(endDate)) {
    return false;
}
```

### 4. Monto Mínimo Garantizado
```java
// El total nunca puede ser negativo después del descuento
return totalAmount.subtract(value).max(BigDecimal.ZERO);
```

---

## 🗄️ Relación con Otras Entidades

### Order
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "promotion_id")
private Promotion promotion;
```

Una orden puede tener **una promoción aplicada** (relación Many-to-One).
- Una promoción puede usarse en múltiples órdenes
- Una orden solo puede tener una promoción

---

## 🧪 Testing

### Casos de Prueba Sugeridos

1. **Validación de código existente**
   - ✅ Código válido retorna promoción
   - ❌ Código inexistente retorna error

2. **Validación de fechas**
   - ✅ Dentro del rango → válida
   - ❌ Antes de startDate → inválida
   - ❌ Después de endDate → inválida

3. **Límite de usos**
   - ✅ currentUses < maxUses → válida
   - ❌ currentUses ≥ maxUses → inválida

4. **Monto mínimo**
   - ✅ totalCompra ≥ minAmount → válida
   - ❌ totalCompra < minAmount → inválida

5. **Cálculo de descuentos**
   - Porcentual: $100 con 10% = $90
   - Fijo: $100 - $25 = $75
   - No negativo: $10 - $20 = $0 (no $-10)

---

## 📊 Monitoreo y Métricas

### Campos Útiles para Analytics

- `currentUses`: Número de veces que se ha usado la promoción
- `maxUses`: Límite de usos configurado
- `minAmount`: Ayuda a calcular el ticket promedio con promoción
- `value`: Monto de descuento otorgado

### Consultas Útiles

```sql
-- Promociones más usadas
SELECT code, description, current_uses 
FROM promotions 
ORDER BY current_uses DESC 
LIMIT 10;

-- Promociones activas próximas a vencer
SELECT code, end_date, DATEDIFF(end_date, NOW()) as days_remaining
FROM promotions 
WHERE is_active = true 
AND end_date > NOW() 
ORDER BY days_remaining ASC;

-- Promociones que alcanzaron su límite
SELECT code, description, max_uses, current_uses
FROM promotions 
WHERE max_uses IS NOT NULL 
AND current_uses >= max_uses;
```

---

## 🚀 Mejoras Futuras Sugeridas

1. **Promociones por Usuario**
   - Limitar usos por usuario (ej: "una vez por cliente")
   - Tabla intermedia: `user_promotion_usage`

2. **Promociones por Categoría**
   - Descuentos solo en películas específicas
   - Descuentos solo en confitería

3. **Códigos Auto-generados**
   - Sistema de generación automática de códigos únicos
   - Ejemplo: `CINE-A8F3-92D1`

4. **Stack de Promociones**
   - Permitir múltiples promociones en una orden
   - Reglas de compatibilidad entre promociones

5. **Notificaciones**
   - Alertas cuando una promoción está próxima a vencer
   - Notificaciones cuando se alcanza el límite de usos

6. **Analytics Dashboard**
   - ROI de promociones
   - Impacto en ventas
   - Usuarios que más usan promociones

---

## 📚 Referencias

- **Entidad**: `domain/entity/Promotion.java`
- **DTO**: `domain/dto/PromotionDTO.java`
- **Service**: `persistence/service/impl/PromotionServiceImpl.java`
- **Controller**: `web/controller/PromotionController.java`
- **Repository**: `domain/repository/PromotionRepository.java`
- **Enum**: `domain/entity/DiscountType.java`

---

## ❓ Preguntas Frecuentes

### ¿Puedo cambiar el código de una promoción existente?
Sí, pero debes asegurarte de que el nuevo código no exista. Es mejor crear una nueva promoción.

### ¿Qué pasa si una promoción expira mientras un usuario está pagando?
La validación se hace al momento de crear la orden. Si expira durante el proceso, no se aplicará.

### ¿Puedo desactivar temporalmente una promoción sin eliminarla?
Sí, cambia `isActive` a `false`.

### ¿El campo currentUses se puede manipular manualmente?
No, está protegido y solo se incrementa automáticamente al aplicar la promoción.

### ¿Qué pasa si el descuento es mayor que el total?
El sistema asegura que el total nunca sea negativo usando `.max(BigDecimal.ZERO)`.

---

**Última actualización:** Diciembre 2025  
**Versión:** 1.0
