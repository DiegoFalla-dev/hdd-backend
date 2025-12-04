-- Fix foreign key constraint in order_concessions table
-- The FK was pointing to 'products' table but should point to 'concession_products'

USE cineplus_db;

-- Drop the old foreign key constraint
ALTER TABLE order_concessions 
DROP FOREIGN KEY FKjt1x8imuwte8bwwhoidxdi112;

-- Add the new foreign key constraint pointing to concession_products
ALTER TABLE order_concessions 
ADD CONSTRAINT FKjt1x8imuwte8bwwhoidxdi112 
FOREIGN KEY (product_id) REFERENCES concession_products(id);

-- Verify the change
SHOW CREATE TABLE order_concessions;
