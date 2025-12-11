-- MIGRACIÓN DE BASE DE DATOS
-- Agregar campos subtotalAmount y taxAmount a la tabla orders
-- Esta migración es necesaria para que el backend pueda guardar los valores desglosados

ALTER TABLE orders
ADD COLUMN IF NOT EXISTS subtotal_amount DECIMAL(10, 2),
ADD COLUMN IF NOT EXISTS tax_amount DECIMAL(10, 2);

-- Comentario para documentación
-- subtotal_amount: El subtotal sin impuestos
-- tax_amount: El IGV (18%) aplicado
-- totalAmount: Ya existía, ahora contiene el total CON impuestos

-- Para órdenes existentes sin estos valores, puedes calcularlos así:
-- UPDATE orders SET
--   subtotal_amount = totalAmount / 1.18,
--   tax_amount = totalAmount - (totalAmount / 1.18)
-- WHERE subtotal_amount IS NULL;

-- Crear índices si es necesario (opcional pero recomendado)
ALTER TABLE orders
ADD KEY IF NOT EXISTS idx_subtotal_amount (subtotal_amount),
ADD KEY IF NOT EXISTS idx_tax_amount (tax_amount);
