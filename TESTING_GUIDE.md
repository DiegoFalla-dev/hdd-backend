# Guía de Pruebas - Nuevas Funcionalidades

## 🎯 Funcionalidades Implementadas

### 1. Pasarela de Pago Sandbox
- **Endpoint**: `POST /api/payments/sandbox`
- **Descripción**: Simula aprobación/declinación de pagos (90% aprobados, 10% declinados)
- **Base de datos**: Nueva tabla `payment_transactions`

### 2. QR Mejorado con Payload JSON
- **Descripción**: Los códigos QR ahora contienen JSON estructurado con `orderId`, `purchaseNumber`, `showtimeId`, `seat`, `invoice`
- **Ubicación**: Generación de tickets en `OrderServiceImpl`

### 3. Notificaciones por Email
- **Servicio**: `MailService` con Spring Mail
- **Configuración**: Ver `application.properties` (puerto 1025 para MailHog/sandbox)
- **Trigger**: Automático al crear una orden

### 4. Reporte de Ventas Diarias
- **Endpoint**: `GET /api/reports/sales/daily`
- **Descripción**: Agrega ventas por día
- **Acceso**: Solo ADMIN y STAFF

### 5. Migraciones con Flyway
- **V1__baseline.sql**: Marcador de línea base
- **V2__ticket_types_and_payments.sql**: Crea tabla de transacciones y seed de tickets

## 📝 Pasos para Probar

### Prerequisitos
```bash
# 1. MySQL debe estar corriendo (Railway o local)
# 2. Redis debe estar corriendo en localhost:6379
# 3. (Opcional) MailHog para capturar emails
docker run -d -p 1025:1025 -p 8025:8025 mailhog/mailhog
```

### Backend

```bash
cd c:\Users\df10x\OneDrive\Documentos\IntelliJ\hdd-backend

# Compilar y ejecutar
./mvnw spring-boot:run
```

**Verificar en consola:**
- ✅ Flyway ejecuta migraciones
- ✅ Tabla `payment_transactions` creada
- ✅ Seed de `ticket_types` insertado
- ✅ No hay errores de compilación

### Frontend

```bash
cd c:\Users\df10x\OneDrive\Documentos\GitHub\hdd-frontend

# Instalar dependencias si es necesario
npm install

# Ejecutar en desarrollo
npm run dev
```

### Flujo de Prueba E2E

#### 1. Probar Pago Sandbox

1. **Navega al flujo de compra**:
   - Login → Seleccionar película → Elegir horario
   - Seleccionar entradas → Elegir butacas
   - (Opcional) Añadir productos de dulcería

2. **En la página de pago** (`/pago`):
   - Agrega o selecciona método de pago
   - Verifica que aparece el preview con totales
   - Clic en "Confirmar y Pagar"

3. **Verificaciones**:
   - ✅ Si el pago es **APROBADO** (90% de probabilidad):
     - Redirección a `/confirmacion/{orderId}`
     - Orden en estado `COMPLETED`
     - Email de confirmación enviado (revisar MailHog en http://localhost:8025)
   
   - ❌ Si el pago es **DECLINADO** (10% de probabilidad):
     - Toast de error "Pago simulado declinado"
     - Orden NO se crea
     - Usuario puede reintentar

4. **Revisar en BD**:
```sql
SELECT * FROM payment_transactions ORDER BY created_at DESC LIMIT 5;
SELECT * FROM orders ORDER BY order_date DESC LIMIT 5;
```

#### 2. Verificar QR Mejorado

1. Después de una compra exitosa:
   - Ve a "Mis Compras" (`/mis-compras`)
   - Clic en "PDF" para descargar ticket
   - Escanea el QR con un lector (app móvil o https://qr.io/scan)

2. **Payload esperado** (JSON):
```json
{
  "orderId": 123,
  "orderItemId": 456,
  "showtimeId": 789,
  "seat": "A5",
  "invoice": "ABC12345"
}
```

#### 3. Verificar Notificación Email

1. Abre MailHog: http://localhost:8025
2. Después de una compra, deberías ver un email con:
   - **Para**: Email del usuario
   - **Asunto**: "Confirmación de compra #{orderId}"
   - **Cuerpo**: Número de orden y total

**Si no tienes MailHog:**
- Revisa los logs del backend
- Verás: `"No se pudo enviar correo de confirmación: Connection refused"`
- Esto es normal en sandbox, el flujo continúa sin bloquear

#### 4. Verificar Reporte de Ventas

1. **Login como STAFF o ADMIN**
2. Ve al Dashboard de Staff (`/staff`)
3. Desplázate hasta "Ventas diarias (sandbox)"
4. Verifica que aparecen las ventas agrupadas por día

**Ejemplo visual esperado:**
```
📈 Ventas diarias (sandbox)
┌──────────────┬──────────────┐
│ Fecha        │ Total (S/)   │
├──────────────┼──────────────┤
│ 2025-12-09   │    350.50    │
│ 2025-12-08   │    125.00    │
└──────────────┴──────────────┘
```

#### 5. Verificar Migraciones Flyway

```bash
# Conectar a MySQL
mysql -h yamabiko.proxy.rlwy.net -P 53398 -u root -p railway

# Revisar historial de migraciones
SELECT * FROM flyway_schema_history;
```

**Output esperado:**
```
installed_rank | version | description                    | success
---------------+---------+--------------------------------+--------
1              | 1       | baseline                       | 1
2              | 2       | ticket types and payments      | 1
```

## 🔧 Configuración Adicional

### application.properties (Backend)

```properties
# Mail (sandbox con MailHog)
spring.mail.host=localhost
spring.mail.port=1025
app.mail.from=no-reply@cineplus.local

# Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration
```

### Producción

Para producción, ajustar:

1. **Mail real** (Gmail, SendGrid, AWS SES):
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

2. **Desactivar DDL auto**:
```properties
spring.jpa.hibernate.ddl-auto=validate
```

3. **Pasarela real** (Culqi, Niubiz, PayPal):
- Reemplazar `PaymentServiceImpl` con cliente SDK real
- Actualizar endpoint `/payments/sandbox` → `/payments/process`

## 🐛 Troubleshooting

### Error: "No se pudo procesar el pago"
- Verificar que el backend responde en http://localhost:8080
- Revisar consola del navegador (F12)
- Verificar logs del backend

### Error: "Tabla payment_transactions no existe"
- Flyway no ejecutó migraciones
- Revisar logs de inicio del backend
- Ejecutar manualmente: `./mvnw flyway:migrate`

### Email no llega
- Verificar MailHog corriendo: http://localhost:8025
- Revisar configuración de mail en `application.properties`
- Si no tienes SMTP, es normal ver warning en logs

### Reporte de ventas vacío
- Normal si no hay órdenes en la BD
- Crear una compra de prueba primero
- Verificar rol de usuario (debe ser STAFF o ADMIN)

## 📊 Base de Datos

### Tablas Nuevas

```sql
-- payment_transactions
CREATE TABLE payment_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,  -- APPROVED, DECLINED, PENDING
    provider VARCHAR(50),
    reference VARCHAR(80),
    created_at DATETIME NOT NULL,
    raw_response VARCHAR(2000)
);
```

### Consultas Útiles

```sql
-- Ver últimas transacciones
SELECT 
    pt.id, 
    pt.status, 
    pt.amount, 
    o.invoice_number,
    pt.created_at
FROM payment_transactions pt
LEFT JOIN orders o ON pt.order_id = o.id
ORDER BY pt.created_at DESC
LIMIT 10;

-- Ventas por día
SELECT 
    DATE(order_date) as day, 
    SUM(total_amount) as total
FROM orders 
GROUP BY DATE(order_date)
ORDER BY day DESC;

-- Órdenes con sus pagos
SELECT 
    o.id,
    o.invoice_number,
    o.order_status,
    o.total_amount,
    pt.status as payment_status,
    pt.reference
FROM orders o
LEFT JOIN payment_transactions pt ON o.id = pt.order_id
ORDER BY o.order_date DESC
LIMIT 10;
```

## ✅ Checklist de Verificación

### Backend
- [ ] Compila sin errores (`./mvnw clean compile`)
- [ ] Arranca correctamente (`./mvnw spring-boot:run`)
- [ ] Flyway ejecuta migraciones (ver logs)
- [ ] Tabla `payment_transactions` existe en BD
- [ ] Endpoint `/api/payments/sandbox` responde
- [ ] Endpoint `/api/reports/sales/daily` responde
- [ ] Mail service registra intento de envío (ver logs)

### Frontend
- [ ] Compila sin errores (`npm run build`)
- [ ] Arranca en dev (`npm run dev`)
- [ ] Página de pago muestra métodos
- [ ] Flujo de pago llama a sandbox
- [ ] Muestra toast según resultado (approved/declined)
- [ ] Redirección a confirmación si aprobado
- [ ] Dashboard staff muestra ventas diarias

### Integración
- [ ] Compra E2E completa exitosamente
- [ ] QR contiene payload JSON estructurado
- [ ] Email enviado/logueado en confirmación
- [ ] Transacción guardada en BD
- [ ] Orden actualizada a COMPLETED
- [ ] Reporte muestra venta del día

## 🚀 Próximos Pasos

1. **Integración real de pasarela** (Culqi/Niubiz/PayPal)
2. **SUNAT / Boleta electrónica** (API mock o piloto)
3. **Notificación SMS** (Twilio/AWS SNS)
4. **Despliegue completo** (Docker Compose + Railway/AWS)
5. **Tests E2E** (Playwright/Cypress)
6. **CI/CD pipeline** (GitHub Actions)

---

**Fecha de implementación**: 9 de diciembre de 2025
**Versión**: 0.0.1-SNAPSHOT
