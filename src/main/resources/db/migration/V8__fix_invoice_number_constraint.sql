-- MIGRACIÓN DE BASE DE DATOS
-- Arreglar constraint de invoice_number para permitir NULLs duplicados
-- El problema anterior era que intentaba usar UUID truncado, causando colisiones

-- Primero, eliminar el constraint anterior si existe
ALTER TABLE orders DROP KEY IF EXISTS invoice_number;

-- Recrear la columna sin el constraint UNIQUE si esto falla, podemos simplemente quitar el unique
-- MySQL permite valores NULL múltiples si se usa una columna calculada o índice parcial
-- La solución más simple es hacer que solo los valores no-NULL sean únicos

-- Alternativa: si tu versión de MySQL lo soporta (5.7.8+), usar índice parcial
-- Para versiones más viejas, simplemente removemos el UNIQUE y dejamos que la aplicación lo maneje
ALTER TABLE orders MODIFY invoice_number VARCHAR(255) NULL UNIQUE KEY;

-- Comentario para documentación
-- invoice_number: Número único de factura electrónica (NULL inicialmente, se genera después)
-- Se permite NULL para órdenes que aún no tienen factura generada
-- El constraint UNIQUE permite múltiples NULLs en MySQL
