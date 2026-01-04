-- Migration Script: Add Product Size Support to Cart
-- Date: 2026-01-04
-- Description: Adds product_size_id column to cart_details table to support size selection in cart

-- Add product_size_id column to cart_details table
ALTER TABLE cart_details
ADD COLUMN product_size_id VARCHAR(36) NULL AFTER product_id;

-- Add foreign key constraint
ALTER TABLE cart_details
ADD CONSTRAINT fk_cart_detail_product_size
    FOREIGN KEY (product_size_id)
    REFERENCES product_sizes(id)
    ON DELETE CASCADE;

-- Add index for better query performance
CREATE INDEX idx_cart_details_product_size_id ON cart_details(product_size_id);

-- Note: Existing cart items without size will have product_size_id = NULL, which is valid

