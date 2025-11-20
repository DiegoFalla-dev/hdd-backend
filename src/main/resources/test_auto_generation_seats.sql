-- ============================================================================
-- SCRIPT DE PRUEBA: Generación Automática de Asientos para Showtimes
-- ============================================================================
-- Fecha: 20 de noviembre de 2025
-- Propósito: Verificar la funcionalidad de auto-generación de asientos
-- ============================================================================

-- ============================================================================
-- PASO 1: VERIFICAR ESTADO ACTUAL
-- ============================================================================

-- 1.1 Ver total de showtimes en el sistema
SELECT COUNT(*) as total_showtimes FROM showtimes;

-- 1.2 Ver showtimes que NO tienen asientos
SELECT s.id, s.date, s.time, s.format, 
       m.title as pelicula,
       t.name as sala,
       (SELECT COUNT(*) FROM seats WHERE showtime_id = s.id) as cant_asientos
FROM showtimes s
LEFT JOIN movies m ON s.movie_id = m.id
LEFT JOIN theaters t ON s.theater_id = t.id
HAVING cant_asientos = 0
ORDER BY s.date, s.time;

-- 1.3 Contar showtimes sin asientos
SELECT COUNT(*) as showtimes_sin_asientos
FROM showtimes s
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE showtime_id = s.id);

-- ============================================================================
-- PASO 2: GENERAR ASIENTOS PARA TODOS LOS SHOWTIMES SIN ASIENTOS
-- ============================================================================
-- IMPORTANTE: Esto se hace mediante llamada al endpoint REST, no SQL directo
-- Ejecutar en PowerShell, CMD o Postman:

/*
PowerShell:
-----------
Invoke-WebRequest -Uri "http://localhost:8080/api/showtimes/seats/generate-all" -Method POST

cURL:
-----
curl -X POST http://localhost:8080/api/showtimes/seats/generate-all

Postman:
--------
POST http://localhost:8080/api/showtimes/seats/generate-all
*/

-- ============================================================================
-- PASO 3: VERIFICAR RESULTADOS DESPUÉS DE LA GENERACIÓN
-- ============================================================================

-- 3.1 Verificar que todos los showtimes ahora tienen asientos
SELECT COUNT(*) as showtimes_sin_asientos
FROM showtimes s
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE showtime_id = s.id);
-- Expected: 0

-- 3.2 Ver distribución de asientos por showtime
SELECT s.id as showtime_id,
       s.date,
       s.time,
       m.title as pelicula,
       COUNT(seats.id) as total_asientos,
       SUM(CASE WHEN seats.status = 'AVAILABLE' THEN 1 ELSE 0 END) as disponibles,
       SUM(CASE WHEN seats.status = 'OCCUPIED' THEN 1 ELSE 0 END) as ocupados,
       SUM(CASE WHEN seats.status = 'TEMPORARILY_RESERVED' THEN 1 ELSE 0 END) as reservados
FROM showtimes s
LEFT JOIN movies m ON s.movie_id = m.id
LEFT JOIN seats ON seats.showtime_id = s.id
GROUP BY s.id, s.date, s.time, m.title
ORDER BY s.date, s.time;

-- 3.3 Verificar que cada showtime tiene exactamente 330 asientos
SELECT s.id as showtime_id,
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

-- ============================================================================
-- PASO 4: VALIDAR CONFIGURACIÓN DE ASIENTOS (PARA UN SHOWTIME ESPECÍFICO)
-- ============================================================================
-- Cambiar el showtime_id según necesites verificar

SET @showtime_id = 1; -- Cambiar por el showtime que quieras verificar

-- 4.1 Verificar distribución por fila
SELECT 
    SUBSTRING(seat_identifier, 1, 1) as fila,
    COUNT(*) as cantidad_asientos,
    CASE 
        WHEN SUBSTRING(seat_identifier, 1, 1) IN ('A', 'B', 'C') AND COUNT(*) = 20 THEN '✅ Correcto'
        WHEN SUBSTRING(seat_identifier, 1, 1) NOT IN ('A', 'B', 'C') AND COUNT(*) = 18 THEN '✅ Correcto'
        ELSE '❌ Cantidad incorrecta'
    END as validacion
FROM seats 
WHERE showtime_id = @showtime_id
GROUP BY SUBSTRING(seat_identifier, 1, 1)
ORDER BY fila;

-- Expected:
-- A: 20 asientos ✅
-- B: 20 asientos ✅
-- C: 20 asientos ✅
-- D: 18 asientos ✅
-- E: 18 asientos ✅
-- ... (hasta R)

-- 4.2 Verificar que el pasillo central está vacío (columnas 14-15)
SELECT COUNT(*) as asientos_en_pasillo
FROM seats 
WHERE showtime_id = @showtime_id 
  AND col_position IN (14, 15);
-- Expected: 0

-- 4.3 Verificar asientos de la fila A (ejemplo)
SELECT seat_identifier, row_position, col_position, status
FROM seats 
WHERE showtime_id = @showtime_id 
  AND seat_identifier LIKE 'A%'
ORDER BY col_position;

-- Expected:
-- A1  (0, 0)   AVAILABLE
-- A2  (0, 1)   AVAILABLE
-- ...
-- A14 (0, 13)  AVAILABLE
-- --- PASILLO (14-15) ---
-- A15 (0, 16)  AVAILABLE
-- A16 (0, 17)  AVAILABLE
-- ...
-- A20 (0, 21)  AVAILABLE

-- 4.4 Verificar que todos los asientos están AVAILABLE
SELECT status, COUNT(*) as cantidad
FROM seats 
WHERE showtime_id = @showtime_id
GROUP BY status;
-- Expected: AVAILABLE: 330

-- ============================================================================
-- PASO 5: CREAR UN SHOWTIME NUEVO Y VERIFICAR AUTO-GENERACIÓN
-- ============================================================================

-- 5.1 Insertar un showtime nuevo manualmente
INSERT INTO showtimes (movie_id, theater_id, date, time, format, available_seats) 
VALUES (1, 1, '2025-11-30', '20:00:00', '_2D', 330);

-- 5.2 Obtener el ID del showtime recién creado
SET @nuevo_showtime_id = LAST_INSERT_ID();

-- 5.3 Verificar que los asientos se generaron automáticamente
SELECT COUNT(*) as total_asientos 
FROM seats 
WHERE showtime_id = @nuevo_showtime_id;
-- Expected: 330 (si el trigger/método funciona automáticamente)
-- Si es 0, llamar al endpoint: POST /api/showtimes/{id}/seats/generate

-- ============================================================================
-- PASO 6: LIMPIAR ASIENTOS DE UN SHOWTIME (SI NECESITAS REGENERAR)
-- ============================================================================

-- 6.1 Borrar asientos de un showtime específico
-- CUIDADO: Esto borra TODOS los asientos del showtime
SET @showtime_to_clean = 4; -- Cambiar por el showtime que necesites limpiar
DELETE FROM seats WHERE showtime_id = @showtime_to_clean;

-- 6.2 Regenerar asientos (vía API)
/*
curl -X POST http://localhost:8080/api/showtimes/4/seats/generate
*/

-- 6.3 Verificar que se regeneraron
SELECT COUNT(*) as total_asientos 
FROM seats 
WHERE showtime_id = @showtime_to_clean;
-- Expected: 330

-- ============================================================================
-- PASO 7: ESTADÍSTICAS GENERALES DEL SISTEMA
-- ============================================================================

-- 7.1 Total de asientos en el sistema
SELECT COUNT(*) as total_asientos_sistema FROM seats;

-- 7.2 Distribución de asientos por estado
SELECT status, COUNT(*) as cantidad, 
       ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM seats), 2) as porcentaje
FROM seats
GROUP BY status
ORDER BY cantidad DESC;

-- 7.3 Showtimes con más asientos ocupados
SELECT s.id, s.date, s.time, m.title,
       COUNT(CASE WHEN seats.status = 'OCCUPIED' THEN 1 END) as ocupados,
       COUNT(seats.id) as total,
       ROUND(COUNT(CASE WHEN seats.status = 'OCCUPIED' THEN 1 END) * 100.0 / COUNT(seats.id), 2) as porcentaje_ocupacion
FROM showtimes s
LEFT JOIN movies m ON s.movie_id = m.id
LEFT JOIN seats ON seats.showtime_id = s.id
GROUP BY s.id, s.date, s.time, m.title
HAVING total > 0
ORDER BY porcentaje_ocupacion DESC
LIMIT 10;

-- 7.4 Showtimes con asientos disponibles hoy
SELECT s.id, s.date, s.time, m.title,
       COUNT(CASE WHEN seats.status = 'AVAILABLE' THEN 1 END) as disponibles
FROM showtimes s
LEFT JOIN movies m ON s.movie_id m.id
LEFT JOIN seats ON seats.showtime_id = s.id
WHERE s.date = CURDATE()
GROUP BY s.id, s.date, s.time, m.title
HAVING disponibles > 0
ORDER BY s.time;

-- ============================================================================
-- NOTAS IMPORTANTES
-- ============================================================================

/*
1. GENERACIÓN AUTOMÁTICA:
   - Los asientos se generan AUTOMÁTICAMENTE al crear un showtime vía API
   - Si creas showtimes por SQL directo, debes llamar al endpoint manualmente

2. ENDPOINT PRINCIPAL:
   POST /api/showtimes/seats/generate-all
   - Genera asientos para TODOS los showtimes sin asientos
   - Devuelve: "Se generaron asientos para X funciones"

3. ENDPOINT INDIVIDUAL:
   POST /api/showtimes/{id}/seats/generate
   - Genera asientos para UN showtime específico
   - No duplica si ya existen asientos

4. CONFIGURACIÓN DE ASIENTOS:
   - 18 filas: A-R
   - Filas A-C: 20 asientos (14 izq + 6 der)
   - Filas D-R: 18 asientos (14 izq + 4 der)
   - Pasillo central: columnas 14-15 (vacías)
   - Total por showtime: 330 asientos

5. TROUBLESHOOTING:
   - Si un showtime tiene 0 asientos: Llamar a /seats/generate
   - Si tiene menos de 330: Borrar y regenerar
   - Si falla la generación: Ver logs del backend
*/

-- ============================================================================
-- FIN DEL SCRIPT
-- ============================================================================
