-- Change favoriteCinema from string to foreign key relationship
-- This migration renames the existing column and creates a new one with foreign key

-- First, create the new column
ALTER TABLE users
ADD COLUMN favorite_cinema_id BIGINT NULL;

-- Add foreign key constraint
ALTER TABLE users
ADD CONSTRAINT fk_users_favorite_cinema 
FOREIGN KEY (favorite_cinema_id) REFERENCES cinemas(id) ON DELETE SET NULL;

-- Drop the old column if it exists
ALTER TABLE users DROP COLUMN favorite_cinema;
