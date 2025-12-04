-- Script para insertar/actualizar los tipos de entrada con sus precios
-- Ejecutar este script en tu base de datos MySQL

-- Limpiar tipos de entrada existentes (opcional, comentar si quieres mantener datos existentes)
-- DELETE FROM ticket_types;

-- Insertar tipos de entrada GENERAL
INSERT INTO ticket_types (code, name, price, active) VALUES
('PROMO_ONLINE', 'PROMO ONLINE', 14.96, true),
('PERSONA_CON_DISCAPACIDAD', 'PERSONA CON DISCAPACIDAD', 17.70, true),
('SILLA_DE_RUEDAS', 'SILLA DE RUEDAS', 17.70, true),
('NINO', 'NIÑO', 21.60, true),
('ADULTO', 'ADULTO', 23.60, true)
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    price = VALUES(price),
    active = VALUES(active);

-- Insertar tipos de entrada CONVENIOS
INSERT INTO ticket_types (code, name, price, active) VALUES
('50_DCTO_BANCO_RIPLEY', '50% DCTO BANCO RIPLEY', 12.80, true)
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    price = VALUES(price),
    active = VALUES(active);

-- Verificar que se insertaron correctamente
SELECT * FROM ticket_types ORDER BY price;
