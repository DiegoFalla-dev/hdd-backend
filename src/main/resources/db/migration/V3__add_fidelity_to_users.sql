-- Migración V3: Agregar campos de fidelización a la tabla users
-- Fecha: 2024-12-11

ALTER TABLE users 
ADD COLUMN fidelity_points INT NOT NULL DEFAULT 0 COMMENT 'Puntos de fidelización acumulados (1 punto cada S/.10)',
ADD COLUMN last_purchase_date DATETIME COMMENT 'Fecha de última compra realizada';

-- Crear índice para búsquedas por puntos de fidelización
CREATE INDEX idx_fidelity_points ON users(fidelity_points);

-- Log
COMMIT;
