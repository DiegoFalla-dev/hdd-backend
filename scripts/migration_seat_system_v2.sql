-- ============================================================================
-- SCRIPT DE MIGRACIÓN: Sistema de Gestión de Butacas CinePlus v2.0
-- Fecha: Noviembre 2025
-- Descripción: Agrega sistema de sesiones, coordenadas y estado CANCELLED
-- ============================================================================

-- 1. Agregar nuevas columnas a la tabla seats
-- ============================================================================

-- Agregar sessionId para tracking de sesiones
ALTER TABLE seats 
ADD COLUMN session_id VARCHAR(100) NULL AFTER status,
ADD INDEX idx_seat_session (session_id);

-- Agregar timestamp de reserva
ALTER TABLE seats 
ADD COLUMN reservation_time DATETIME NULL AFTER session_id;

-- Agregar número de orden/compra
ALTER TABLE seats 
ADD COLUMN purchase_number VARCHAR(50) NULL AFTER reservation_time;

-- Agregar coordenadas para matriz de asientos
ALTER TABLE seats 
ADD COLUMN row_position INT NOT NULL DEFAULT 0 AFTER purchase_number,
ADD COLUMN col_position INT NOT NULL DEFAULT 0 AFTER row_position,
ADD INDEX idx_seat_coordinates (row_position, col_position);

-- Agregar flag de cancelación permanente
ALTER TABLE seats 
ADD COLUMN is_cancelled BOOLEAN NOT NULL DEFAULT false AFTER col_position;

-- 2. Actualizar enum de estados (agregar CANCELLED)
-- ============================================================================
-- Nota: Dependiendo de tu versión de MySQL/H2, puede que necesites recrear la tabla
-- o simplemente JPA lo manejará automáticamente al arrancar la aplicación

-- Para MySQL, puedes hacer:
ALTER TABLE seats 
MODIFY COLUMN status ENUM('AVAILABLE', 'OCCUPIED', 'TEMPORARILY_RESERVED', 'CANCELLED') NOT NULL;

-- 3. Crear tabla seat_reservations
-- ============================================================================
CREATE TABLE IF NOT EXISTS seat_reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(100) NOT NULL UNIQUE,
    showtime_id BIGINT NOT NULL,
    user_id BIGINT NULL,
    created_at DATETIME NOT NULL,
    expiry_time DATETIME NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_confirmed BOOLEAN NOT NULL DEFAULT false,
    purchase_number VARCHAR(50) NULL,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_reservation_session (session_id),
    INDEX idx_reservation_showtime (showtime_id),
    INDEX idx_reservation_expiry (expiry_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Crear tabla para identificadores de asientos en reservas
-- ============================================================================
CREATE TABLE IF NOT EXISTS reservation_seat_identifiers (
    reservation_id BIGINT NOT NULL,
    seat_identifier VARCHAR(5) NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES seat_reservations(id) ON DELETE CASCADE,
    INDEX idx_reservation_seats (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Migrar datos existentes (asignar coordenadas a asientos existentes)
-- ============================================================================
-- Este script asume que los asientos siguen el formato A1, A2, ..., B1, B2, etc.
-- Ajusta según tu estructura actual

-- Función para extraer la letra de la fila (A=0, B=1, etc.)
-- Función para extraer el número de columna

-- IMPORTANTE: Ejecuta este script MANUALMENTE si tienes datos existentes
-- Revisa primero la estructura de tus identificadores de asiento

-- Ejemplo de actualización manual para asientos con formato estándar:
/*
UPDATE seats 
SET row_position = ASCII(SUBSTRING(seat_identifier, 1, 1)) - ASCII('A'),
    col_position = CAST(SUBSTRING(seat_identifier, 2) AS UNSIGNED) - 1
WHERE row_position = 0 AND col_position = 0;
*/

-- 6. Verificar índices existentes
-- ============================================================================
-- Verifica que el índice en showtime_id exista
CREATE INDEX IF NOT EXISTS idx_seat_showtime ON seats(showtime_id);

-- 7. Limpiar datos inconsistentes (opcional)
-- ============================================================================
-- Liberar cualquier reserva temporal antigua sin sessionId
UPDATE seats 
SET status = 'AVAILABLE', 
    session_id = NULL, 
    reservation_time = NULL
WHERE status = 'TEMPORARILY_RESERVED' 
  AND (session_id IS NULL OR reservation_time < DATE_SUB(NOW(), INTERVAL 10 MINUTE));

-- ============================================================================
-- FIN DEL SCRIPT DE MIGRACIÓN
-- ============================================================================

-- VALIDACIÓN: Consultas para verificar la migración
-- ============================================================================

-- Verificar estructura de seats
DESCRIBE seats;

-- Verificar estructura de seat_reservations
DESCRIBE seat_reservations;

-- Contar asientos por estado
SELECT status, COUNT(*) as count 
FROM seats 
GROUP BY status;

-- Verificar asientos con coordenadas
SELECT seat_identifier, row_position, col_position, status 
FROM seats 
LIMIT 10;

-- ============================================================================
-- ROLLBACK (solo si es necesario)
-- ============================================================================
/*
-- Para revertir los cambios (CUIDADO: perderás datos de las nuevas columnas)

ALTER TABLE seats DROP COLUMN session_id;
ALTER TABLE seats DROP COLUMN reservation_time;
ALTER TABLE seats DROP COLUMN purchase_number;
ALTER TABLE seats DROP COLUMN row_position;
ALTER TABLE seats DROP COLUMN col_position;
ALTER TABLE seats DROP COLUMN is_cancelled;
ALTER TABLE seats DROP INDEX idx_seat_session;
ALTER TABLE seats DROP INDEX idx_seat_coordinates;

DROP TABLE IF EXISTS reservation_seat_identifiers;
DROP TABLE IF EXISTS seat_reservations;
*/
