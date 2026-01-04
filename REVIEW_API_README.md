# HƯỚNG DẪN SỬ DỤNG API ĐÁNH GIÁ SẢN PHẨM

## Tổng quan
API quản lý chức năng đánh giá sản phẩm đã được tạo thành công với đầy đủ các chức năng CRUD và các tính năng nâng cao.

## Các file đã được tạo

### 1. Entity
- ✅ `Review.java` - Entity đã có sẵn
- ✅ `Product.java` - Đã thêm quan hệ OneToMany với Review
- ✅ `User.java` - Đã thêm quan hệ OneToMany với Review

### 2. Repository
- ✅ `ReviewRepository.java` - Repository với các method query tùy chỉnh

### 3. DTOs
**Request:**
- ✅ `ReviewRequest.java` - DTO cho việc tạo đánh giá mới
- ✅ `UpdateReviewRequest.java` - DTO cho việc cập nhật đánh giá

**Response:**
- ✅ `ReviewResponse.java` - DTO cho response đánh giá
- ✅ `ProductRatingResponse.java` - DTO cho thống kê đánh giá sản phẩm

### 4. Service
- ✅ `ReviewService.java` - Business logic cho chức năng đánh giá

### 5. Controller
- ✅ `ReviewController.java` - REST API endpoints

### 6. Enums
- ✅ `ErrorCode.java` - Đã thêm các mã lỗi mới (6xxx)

### 7. Documentation
- ✅ `API_REVIEW_DOCUMENTATION.md` - Tài liệu API chi tiết
- ✅ `database_reviews_table.sql` - Script SQL tạo bảng (optional)

## Các tính năng chính

### 1. Tạo đánh giá
- User đăng nhập có thể tạo đánh giá cho sản phẩm
- Mỗi user chỉ được đánh giá 1 lần cho 1 sản phẩm
- Đánh giá từ 1-5 sao (bắt buộc)
- Comment tùy chọn (tối đa 1000 ký tự)

### 2. Cập nhật đánh giá
- User chỉ có thể cập nhật đánh giá của chính mình
- Có thể thay đổi rating và comment

### 3. Xóa đánh giá
- User có thể xóa đánh giá của mình
- Admin có thể xóa bất kỳ đánh giá nào

### 4. Xem đánh giá
- Xem đánh giá theo ID
- Xem tất cả đánh giá của 1 sản phẩm
- Xem tất cả đánh giá của user hiện tại
- Admin: Xem tất cả đánh giá trong hệ thống

### 5. Thống kê đánh giá
- Điểm đánh giá trung bình của sản phẩm
- Tổng số đánh giá
- Phân bố đánh giá theo số sao (1-5)

## API Endpoints

```
POST   /api/v1/reviews                          - Tạo đánh giá mới
PUT    /api/v1/reviews/{reviewId}               - Cập nhật đánh giá
DELETE /api/v1/reviews/{reviewId}               - Xóa đánh giá
GET    /api/v1/reviews/{reviewId}               - Lấy đánh giá theo ID
GET    /api/v1/reviews/product/{productId}      - Lấy đánh giá theo sản phẩm
GET    /api/v1/reviews/my-reviews               - Lấy đánh giá của tôi
GET    /api/v1/reviews/product/{productId}/rating - Thống kê đánh giá sản phẩm
GET    /api/v1/reviews/all                      - Lấy tất cả đánh giá (Admin)
```

## Quyền truy cập

### Public (Không cần đăng nhập)
- ✅ Xem đánh giá theo ID
- ✅ Xem danh sách đánh giá theo sản phẩm
- ✅ Xem thống kê đánh giá sản phẩm

### USER & ADMIN (Cần đăng nhập)
- ✅ Tạo đánh giá mới
- ✅ Cập nhật đánh giá của mình
- ✅ Xóa đánh giá của mình
- ✅ Xem đánh giá của mình

### ADMIN
- ✅ Xóa bất kỳ đánh giá nào
- ✅ Xem tất cả đánh giá trong hệ thống

## Các mã lỗi mới

```java
REVIEW_NOT_FOUND(6001, "Không tìm thấy đánh giá.", HttpStatus.NOT_FOUND)
REVIEW_ALREADY_EXISTS(6002, "Bạn đã đánh giá sản phẩm này rồi.", HttpStatus.BAD_REQUEST)
UNAUTHORIZED(6003, "Bạn không có quyền thực hiện thao tác này.", HttpStatus.FORBIDDEN)
USER_NOT_EXISTED(6004, "Người dùng không tồn tại.", HttpStatus.NOT_FOUND)
```

## Validation

### ReviewRequest
- `productId`: Không được để trống
- `rating`: Bắt buộc, từ 1 đến 5 sao
- `comment`: Tùy chọn, tối đa 1000 ký tự

### UpdateReviewRequest
- `rating`: Bắt buộc, từ 1 đến 5 sao
- `comment`: Tùy chọn, tối đa 1000 ký tự

## Cấu trúc Database

Bảng `reviews`:
```sql
- id (VARCHAR/UUID) - Primary Key
- user_id (VARCHAR) - Foreign Key -> users.id
- product_id (VARCHAR) - Foreign Key -> products.id
- rating (INT) - 1-5 sao
- comment (TEXT) - Tùy chọn
- created_at (TIMESTAMP)
- UNIQUE(user_id, product_id) - Mỗi user chỉ review 1 lần/sản phẩm
```

## Cách chạy

1. **Cấu hình database** (nếu chưa có)
   - JPA sẽ tự động tạo bảng `reviews` nếu `spring.jpa.hibernate.ddl-auto=update`
   - Hoặc chạy script `database_reviews_table.sql` thủ công

2. **Build project**
   ```bash
   mvn clean install
   ```

3. **Chạy ứng dụng**
   ```bash
   mvn spring-boot:run
   ```

4. **Test API**
   - Xem chi tiết trong file `API_REVIEW_DOCUMENTATION.md`
   - Sử dụng Postman hoặc curl để test

## Ví dụ sử dụng

### 1. Tạo đánh giá mới
```bash
curl -X POST "http://localhost:8080/api/v1/reviews" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "product-uuid",
    "rating": 5,
    "comment": "Sản phẩm rất tốt!"
  }'
```

### 2. Xem thống kê đánh giá sản phẩm
```bash
curl -X GET "http://localhost:8080/api/v1/reviews/product/{productId}/rating"
```

### 3. Xem đánh giá của sản phẩm
```bash
curl -X GET "http://localhost:8080/api/v1/reviews/product/{productId}"
```

## Lưu ý quan trọng

1. **Security**: API sử dụng JWT Authentication
2. **Unique Constraint**: Mỗi user chỉ được review 1 lần cho 1 sản phẩm
3. **Cascade Delete**: Khi xóa user hoặc product, các review liên quan cũng bị xóa
4. **JSON Serialization**: Đã thêm @JsonIgnore để tránh vòng lặp JSON

## Tính năng nâng cao có thể mở rộng

- [ ] Thêm hình ảnh vào review
- [ ] Phản hồi review (reply)
- [ ] Báo cáo review không phù hợp
- [ ] Vote helpful/not helpful cho review
- [ ] Filter và sort reviews (theo ngày, rating, helpful votes)
- [ ] Pagination cho danh sách reviews
- [ ] Email notification khi có review mới
- [ ] Verified purchase badge

## Kết luận

API đánh giá sản phẩm đã được implement đầy đủ với các tính năng cơ bản và nâng cao. Code được viết theo best practices với:
- ✅ Clean Architecture
- ✅ Validation đầy đủ
- ✅ Exception handling
- ✅ Security với JWT
- ✅ Role-based access control
- ✅ Documentation chi tiết

Chúc bạn code vui vẻ! 🚀

