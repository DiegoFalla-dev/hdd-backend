-- MIGRACIÓN DE BASE DE DATOS
-- Agregar columna promotion_id a la tabla orders
-- Esta migración permite guardar qué promoción fue aplicada a cada orden

ALTER TABLE orders
ADD COLUMN IF NOT EXISTS promotion_id BIGINT;

-- Agregar constraint de clave foránea
ALTER TABLE orders
ADD CONSTRAINT IF NOT EXISTS fk_order_promotion
FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE SET NULL;

-- Crear índice para mejorar búsquedas por promoción
ALTER TABLE orders
ADD KEY IF NOT EXISTS idx_promotion_id (promotion_id);

-- Comentario para documentación
-- promotion_id: ID de la promoción aplicada a esta orden (nullable si no hay promoción)
-- fk_order_promotion: Clave foránea que referencia la tabla promotions
-- Si se elimina una promoción, las órdenes que la usaron tendrán promotion_id = NULL
