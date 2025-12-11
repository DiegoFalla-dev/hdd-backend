-- V6__insert_promotional_codes.sql
-- Insertar códigos promocionales de ejemplo para demostración

INSERT INTO promotions (code, description, discount_type, value, start_date, end_date, min_amount, max_uses, current_uses, is_active) VALUES

-- Promoción 1: Descuento por porcentaje - Navidad
('NAVIDAD2024', 'Descuento especial de Navidad - 20% en todas tus compras', 'PERCENTAGE', 20.00, '2024-12-01 00:00:00', '2024-12-25 23:59:59', 50.00, 1000, 45, TRUE),

-- Promoción 2: Descuento fijo - Fin de semana
('WEEKEND50', 'Descuento de S/ 50 en compras mayores a S/ 150', 'FIXED_AMOUNT', 50.00, '2024-12-01 00:00:00', '2024-12-31 23:59:59', 150.00, 500, 120, TRUE),

-- Promoción 3: Descuento estudiante
('ESTUDIANTE15', 'Estudiantes: 15% de descuento con carné válido - Mínimo S/ 80', 'PERCENTAGE', 15.00, '2024-12-01 00:00:00', '2025-03-31 23:59:59', 80.00, 300, 87, TRUE),

-- Promoción 4: Descuento por referral
('REFIERE20', 'Refiere a un amigo y obtén S/ 20 de descuento', 'FIXED_AMOUNT', 20.00, '2024-12-01 00:00:00', '2025-02-28 23:59:59', 100.00, 800, 234, TRUE),

-- Promoción 5: Black Friday (próxima)
('BLACK2025', 'Black Friday 2025 - 30% en todo - ¡Próximamente!', 'PERCENTAGE', 30.00, '2025-01-15 00:00:00', '2025-01-20 23:59:59', 80.00, 2000, 0, TRUE),

-- Promoción 6: Promoción por volumen
('PROMO6ENTRADAS', 'Compra 6 entradas y obtén 25% de descuento', 'PERCENTAGE', 25.00, '2024-12-01 00:00:00', '2024-12-31 23:59:59', 200.00, 150, 32, TRUE),

-- Promoción 7: Descuento dulcería
('DULCERIA35', 'S/ 35 de descuento en dulcería con compra de entradas', 'FIXED_AMOUNT', 35.00, '2024-12-01 00:00:00', '2025-01-15 23:59:59', 120.00, 400, 98, TRUE),

-- Promoción 8: Año Nuevo
('NEWYEAR2025', 'Bienvenida 2025 - 12% de descuento', 'PERCENTAGE', 12.00, '2024-12-31 00:00:00', '2025-01-07 23:59:59', 60.00, 600, 156, TRUE);

-- Promotions already in database will not be duplicated if they exist
-- This script assumes the promotions table exists and is empty or these codes don't exist
