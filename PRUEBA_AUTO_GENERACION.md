# 🧪 GUÍA DE PRUEBA: Auto-Generación Automática de Asientos

**Fecha:** 20 de noviembre de 2025  
**Objetivo:** Verificar que los asientos se generan automáticamente al crear un showtime

---

## 🎯 PRUEBA RÁPIDA (5 minutos)

### **PASO 1: Iniciar el Backend**

```powershell
# En PowerShell, dentro de C:\Github\hdd-backend
.\mvnw.cmd spring-boot:run
```

**Espera a ver este mensaje:**
```
Started ECommerceCineplusBackendApplication in X.XXX seconds
```

---

### **PASO 2: Verificar Estado Inicial**

```powershell
# Ver cuántos showtimes NO tienen asientos actualmente
$query = "SELECT COUNT(*) as sin_asientos FROM showtimes WHERE id NOT IN (SELECT DISTINCT showtime_id FROM seats)"

# Ejecutar en MySQL Workbench o HeidiSQL
```

**Ejemplo de resultado:**
```
sin_asientos: 32
```

---

### **PASO 3: Generar Asientos para Todos los Showtimes Existentes**

```powershell
# Llamar al endpoint que genera asientos para TODOS los showtimes sin asientos
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/showtimes/seats/generate-all" -Method POST
$response.Content
```

**✅ Resultado esperado:**
```
Se generaron asientos para 32 funciones
```

---

### **PASO 4: Verificar que Se Generaron**

```powershell
# Verificar en base de datos
# En MySQL Workbench:
SELECT COUNT(*) as sin_asientos 
FROM showtimes 
WHERE id NOT IN (SELECT DISTINCT showtime_id FROM seats);
```

**✅ Resultado esperado:**
```
sin_asientos: 0
```

---

### **PASO 5: Crear un Showtime NUEVO y Verificar Auto-Generación**

```powershell
# Crear un nuevo showtime (elige una película y sala que existan)
$body = @{
    movieId = 1
    theaterId = 1
    date = "2025-11-30"
    time = "20:00:00"
    format = "_2D"
    availableSeats = 330
} | ConvertTo-Json

$response = Invoke-WebRequest `
    -Uri "http://localhost:8080/api/showtimes" `
    -Method POST `
    -Body $body `
    -ContentType "application/json"

$newShowtime = $response.Content | ConvertFrom-Json
Write-Host "Showtime creado con ID: $($newShowtime.id)"
```

**✅ Resultado esperado:**
```
Showtime creado con ID: 100
```

---

### **PASO 6: Verificar que los Asientos se Generaron AUTOMÁTICAMENTE**

```powershell
# Verificar en base de datos
$showtimeId = 100  # Usar el ID del showtime que acabas de crear

# En MySQL Workbench:
SELECT COUNT(*) as total_asientos 
FROM seats 
WHERE showtime_id = $showtimeId;
```

**✅ Resultado esperado:**
```
total_asientos: 330
```

**🎉 Si ves 330 asientos, ¡LA AUTO-GENERACIÓN FUNCIONA!**

---

## 🔍 PRUEBA DETALLADA (Verificar Configuración Completa)

### **Verificar Distribución por Fila**

```sql
-- En MySQL Workbench
SET @showtime_id = 100; -- Cambiar por tu showtime ID

SELECT 
    SUBSTRING(seat_identifier, 1, 1) as fila,
    COUNT(*) as cantidad_asientos
FROM seats 
WHERE showtime_id = @showtime_id
GROUP BY SUBSTRING(seat_identifier, 1, 1)
ORDER BY fila;
```

**✅ Resultado esperado:**
```
fila | cantidad_asientos
-----|------------------
A    | 20
B    | 20
C    | 20
D    | 18
E    | 18
F    | 18
...
R    | 18
```

---

### **Verificar Pasillo Central (Columnas 14-15 Vacías)**

```sql
-- Verificar que NO hay asientos en las columnas 14-15
SELECT COUNT(*) as asientos_en_pasillo
FROM seats 
WHERE showtime_id = @showtime_id 
  AND col_position IN (14, 15);
```

**✅ Resultado esperado:**
```
asientos_en_pasillo: 0
```

---

### **Verificar Asientos de la Fila A**

```sql
SELECT seat_identifier, row_position, col_position, status
FROM seats 
WHERE showtime_id = @showtime_id 
  AND seat_identifier LIKE 'A%'
ORDER BY col_position;
```

**✅ Resultado esperado:**
```
A1  | 0 | 0  | AVAILABLE
A2  | 0 | 1  | AVAILABLE
...
A14 | 0 | 13 | AVAILABLE
--- PASILLO (14-15) ---
A15 | 0 | 16 | AVAILABLE
A16 | 0 | 17 | AVAILABLE
...
A20 | 0 | 21 | AVAILABLE
```

---

## 📊 SCRIPTS COMPLETOS DE POWERSHELL

### **Script 1: Generar Asientos para Showtimes Existentes**

```powershell
# ============================================================================
# SCRIPT: Generar asientos para todos los showtimes sin asientos
# ============================================================================

Write-Host "🎬 Generando asientos para showtimes existentes..." -ForegroundColor Cyan

try {
    $response = Invoke-WebRequest `
        -Uri "http://localhost:8080/api/showtimes/seats/generate-all" `
        -Method POST `
        -UseBasicParsing `
        -TimeoutSec 30
    
    Write-Host "✅ Éxito: $($response.Content)" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}
```

---

### **Script 2: Crear Showtime y Verificar Auto-Generación**

```powershell
# ============================================================================
# SCRIPT: Crear showtime y verificar auto-generación de asientos
# ============================================================================

Write-Host "🎬 Creando nuevo showtime..." -ForegroundColor Cyan

# Datos del nuevo showtime
$newShowtime = @{
    movieId = 1
    theaterId = 1
    date = "2025-12-01"
    time = "19:00:00"
    format = "_2D"
    availableSeats = 330
} | ConvertTo-Json

try {
    # Crear showtime
    $response = Invoke-WebRequest `
        -Uri "http://localhost:8080/api/showtimes" `
        -Method POST `
        -Body $newShowtime `
        -ContentType "application/json" `
        -UseBasicParsing
    
    $showtime = $response.Content | ConvertFrom-Json
    $showtimeId = $showtime.id
    
    Write-Host "✅ Showtime creado con ID: $showtimeId" -ForegroundColor Green
    
    # Esperar 2 segundos para asegurar que la generación termine
    Write-Host "⏳ Esperando generación de asientos..." -ForegroundColor Yellow
    Start-Sleep -Seconds 2
    
    # Verificar asientos generados (necesitas ejecutar esto en MySQL)
    Write-Host ""
    Write-Host "📝 Ejecuta este query en MySQL para verificar:" -ForegroundColor Cyan
    Write-Host "SELECT COUNT(*) FROM seats WHERE showtime_id = $showtimeId;" -ForegroundColor White
    Write-Host ""
    Write-Host "✅ Deberías ver: 330 asientos" -ForegroundColor Green
    
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}
```

---

### **Script 3: Verificación Completa (Requiere MySQL)**

```powershell
# ============================================================================
# SCRIPT: Verificación completa del sistema
# ============================================================================

Write-Host "🔍 VERIFICACIÓN COMPLETA DEL SISTEMA" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Nota: Este script genera los queries SQL que debes ejecutar en MySQL Workbench

$queries = @"
-- 1. Total de showtimes en el sistema
SELECT COUNT(*) as total_showtimes FROM showtimes;

-- 2. Showtimes SIN asientos
SELECT COUNT(*) as sin_asientos 
FROM showtimes 
WHERE id NOT IN (SELECT DISTINCT showtime_id FROM seats);

-- 3. Total de asientos generados
SELECT COUNT(*) as total_asientos FROM seats;

-- 4. Distribución de asientos por showtime
SELECT s.id, 
       COUNT(seats.id) as total_asientos,
       CASE 
           WHEN COUNT(seats.id) = 330 THEN '✅ Correcto'
           WHEN COUNT(seats.id) = 0 THEN '⚠️ Sin asientos'
           ELSE '❌ Cantidad incorrecta'
       END as validacion
FROM showtimes s
LEFT JOIN seats ON seats.showtime_id = s.id
GROUP BY s.id
ORDER BY s.id;
"@

Write-Host "📋 Ejecuta estos queries en MySQL Workbench:" -ForegroundColor Yellow
Write-Host $queries -ForegroundColor White
Write-Host ""
Write-Host "✅ Todos los showtimes deberían tener 330 asientos" -ForegroundColor Green
```

---

## 🎯 CHECKLIST DE VALIDACIÓN

Marca cada item cuando lo hayas verificado:

### **Generación para Showtimes Existentes:**
- [ ] Endpoint `/seats/generate-all` responde correctamente
- [ ] Mensaje indica cuántos showtimes fueron procesados
- [ ] Todos los showtimes ahora tienen asientos (query devuelve 0 sin asientos)
- [ ] Cada showtime tiene exactamente 330 asientos

### **Auto-Generación para Showtimes Nuevos:**
- [ ] Crear un showtime nuevo vía API funciona sin errores
- [ ] El showtime recibe un ID único
- [ ] Los asientos se generan AUTOMÁTICAMENTE (sin llamar a /generate)
- [ ] Total de asientos = 330

### **Configuración de Asientos:**
- [ ] Filas A-C tienen 20 asientos cada una
- [ ] Filas D-R tienen 18 asientos cada una
- [ ] Pasillo central (columnas 14-15) está vacío
- [ ] Todos los asientos tienen status AVAILABLE
- [ ] Coordenadas (row_position, col_position) son correctas

### **Logs del Backend:**
- [ ] No hay errores en la consola del backend
- [ ] Se ven mensajes de generación de asientos
- [ ] No hay excepciones o stack traces

---

## ❓ TROUBLESHOOTING

### **Problema: "Connection refused" al llamar al endpoint**

**Solución:**
```powershell
# Verificar que el backend está corriendo
$response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing
$response.StatusCode  # Debería ser 200
```

---

### **Problema: Showtime se crea pero NO tiene asientos**

**Posibles causas:**
1. Error en el método `saveShowtime()` (revisar logs)
2. Excepción durante la generación (revisar try-catch)

**Solución:**
```powershell
# Generar manualmente para ese showtime
$showtimeId = 100  # Cambiar por tu ID
Invoke-WebRequest -Uri "http://localhost:8080/api/showtimes/$showtimeId/seats/generate" -Method POST
```

---

### **Problema: Cantidad de asientos incorrecta (no son 330)**

**Solución:**
```sql
-- 1. Borrar asientos incorrectos
DELETE FROM seats WHERE showtime_id = 100;

-- 2. Regenerar vía API
-- curl -X POST http://localhost:8080/api/showtimes/100/seats/generate
```

---

## 🎬 RESULTADO ESPERADO FINAL

Después de seguir esta guía, deberías tener:

✅ **Todos los showtimes existentes** con 330 asientos cada uno  
✅ **Nuevos showtimes** generan asientos automáticamente al crearse  
✅ **Configuración correcta:** 18 filas (A-R) con pasillo central  
✅ **Sin errores** en el backend  

---

## 📞 SOPORTE

Si algo no funciona:

1. **Revisar logs del backend** en la consola donde corre Spring Boot
2. **Ejecutar queries SQL** del archivo `test_auto_generation_seats.sql`
3. **Verificar que Hibernate** creó las tablas correctamente
4. **Comprobar que MySQL** está corriendo y accesible

---

**✅ Sistema de Auto-Generación Listo para Usar** 🎉
