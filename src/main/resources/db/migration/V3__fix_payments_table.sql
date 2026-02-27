-- V3: Fix payments table to match entity definition

-- Add missing columns
ALTER TABLE payments ADD COLUMN order_number VARCHAR(50) NOT NULL DEFAULT '' AFTER id;
ALTER TABLE payments ADD COLUMN fail_reason VARCHAR(255) AFTER status;

-- Rename mismatched columns
ALTER TABLE payments CHANGE COLUMN amount total_amount INT NOT NULL;
ALTER TABLE payments CHANGE COLUMN transaction_id pg_transaction_id VARCHAR(100);

-- Make build_id nullable (entity allows null)
ALTER TABLE payments MODIFY COLUMN build_id BIGINT NULL;

-- Add unique constraint on order_number
ALTER TABLE payments ADD UNIQUE KEY uk_order_number (order_number);
