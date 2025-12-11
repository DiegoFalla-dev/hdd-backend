-- Migración V9: Agregar puntos de fidelización de prueba a usuarios
-- Fecha: 2025-12-11

-- Agregar puntos de prueba al usuario diego123
UPDATE users SET fidelity_points = 500 WHERE username = 'diego123';

-- Agregar puntos de prueba al usuario admin
UPDATE users SET fidelity_points = 1200 WHERE username = 'admin';

-- Log
COMMIT;
