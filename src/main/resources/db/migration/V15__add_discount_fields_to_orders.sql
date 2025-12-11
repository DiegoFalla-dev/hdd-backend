-- V15: Add discount_amount and fidelity_discount_amount columns to orders table
ALTER TABLE orders 
ADD COLUMN discount_amount DECIMAL(10, 2) DEFAULT 0.00 AFTER tax_amount,
ADD COLUMN fidelity_discount_amount DECIMAL(10, 2) DEFAULT 0.00 AFTER discount_amount;

-- Create indexes for potential future queries
CREATE INDEX idx_discount_amount ON orders(discount_amount);
CREATE INDEX idx_fidelity_discount_amount ON orders(fidelity_discount_amount);
