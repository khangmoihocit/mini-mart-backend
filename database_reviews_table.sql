-- Script SQL để tạo bảng reviews
-- Chạy script này nếu bảng chưa được tạo tự động bởi JPA

CREATE TABLE IF NOT EXISTS reviews (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tạo index để tăng performance khi query
CREATE INDEX idx_reviews_product_id ON reviews(product_id);
CREATE INDEX idx_reviews_user_id ON reviews(user_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);
CREATE INDEX idx_reviews_created_at ON reviews(created_at);

-- Lưu ý:
-- 1. Bảng này sẽ tự động được tạo bởi JPA nếu spring.jpa.hibernate.ddl-auto=update hoặc create
-- 2. Unique constraint (user_id, product_id) đảm bảo mỗi user chỉ review 1 lần cho 1 sản phẩm
-- 3. ON DELETE CASCADE đảm bảo khi xóa user hoặc product thì các review liên quan cũng bị xóa

