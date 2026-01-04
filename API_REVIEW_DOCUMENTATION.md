# API REVIEW DOCUMENTATION

## Tổng quan
API quản lý chức năng đánh giá sản phẩm cho phép người dùng tạo, cập nhật, xóa và xem đánh giá sản phẩm. API cũng cung cấp chức năng xem thống kê đánh giá của từng sản phẩm.

## Base URL
```
${api.prefix}/reviews
```

---

## 1. Tạo đánh giá mới

**Endpoint:** `POST /reviews`

**Yêu cầu xác thực:** Có (Role: USER, ADMIN)

**Request Body:**
```json
{
  "productId": "string",
  "rating": 5,
  "comment": "string (tùy chọn, tối đa 1000 ký tự)"
}
```

**Validation:**
- `productId`: Không được để trống
- `rating`: Bắt buộc, từ 1 đến 5 sao
- `comment`: Tối đa 1000 ký tự (tùy chọn)

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Tạo đánh giá thành công!",
  "result": {
    "id": "uuid",
    "userId": "uuid",
    "userName": "Nguyễn Văn A",
    "userEmail": "user@example.com",
    "productId": "uuid",
    "productName": "Tên sản phẩm",
    "rating": 5,
    "comment": "Sản phẩm rất tốt!",
    "createdAt": "2026-01-04T10:30:00"
  }
}
```

**Error Response:**
- `6002`: Bạn đã đánh giá sản phẩm này rồi
- `3001`: Không tìm thấy sản phẩm
- `6004`: Người dùng không tồn tại

---

## 2. Cập nhật đánh giá

**Endpoint:** `PUT /reviews/{reviewId}`

**Yêu cầu xác thực:** Có (Role: USER, ADMIN)

**Path Parameters:**
- `reviewId`: ID của đánh giá cần cập nhật

**Request Body:**
```json
{
  "rating": 4,
  "comment": "Cập nhật đánh giá mới"
}
```

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Cập nhật đánh giá thành công!",
  "result": {
    "id": "uuid",
    "userId": "uuid",
    "userName": "Nguyễn Văn A",
    "userEmail": "user@example.com",
    "productId": "uuid",
    "productName": "Tên sản phẩm",
    "rating": 4,
    "comment": "Cập nhật đánh giá mới",
    "createdAt": "2026-01-04T10:30:00"
  }
}
```

**Error Response:**
- `6001`: Không tìm thấy đánh giá
- `6003`: Bạn không có quyền thực hiện thao tác này

---

## 3. Xóa đánh giá

**Endpoint:** `DELETE /reviews/{reviewId}`

**Yêu cầu xác thực:** Có (Role: USER, ADMIN)

**Path Parameters:**
- `reviewId`: ID của đánh giá cần xóa

**Lưu ý:** 
- User chỉ có thể xóa đánh giá của chính mình
- Admin có thể xóa bất kỳ đánh giá nào

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Xóa đánh giá thành công!",
  "result": null
}
```

**Error Response:**
- `6001`: Không tìm thấy đánh giá
- `6003`: Bạn không có quyền thực hiện thao tác này

---

## 4. Lấy thông tin đánh giá theo ID

**Endpoint:** `GET /reviews/{reviewId}`

**Yêu cầu xác thực:** Không

**Path Parameters:**
- `reviewId`: ID của đánh giá

**Response Success (200):**
```json
{
  "code": 1000,
  "message": null,
  "result": {
    "id": "uuid",
    "userId": "uuid",
    "userName": "Nguyễn Văn A",
    "userEmail": "user@example.com",
    "productId": "uuid",
    "productName": "Tên sản phẩm",
    "rating": 5,
    "comment": "Sản phẩm rất tốt!",
    "createdAt": "2026-01-04T10:30:00"
  }
}
```

**Error Response:**
- `6001`: Không tìm thấy đánh giá

---

## 5. Lấy danh sách đánh giá theo sản phẩm

**Endpoint:** `GET /reviews/product/{productId}`

**Yêu cầu xác thực:** Không

**Path Parameters:**
- `productId`: ID của sản phẩm

**Response Success (200):**
```json
{
  "code": 1000,
  "message": null,
  "result": [
    {
      "id": "uuid",
      "userId": "uuid",
      "userName": "Nguyễn Văn A",
      "userEmail": "user@example.com",
      "productId": "uuid",
      "productName": "Tên sản phẩm",
      "rating": 5,
      "comment": "Sản phẩm rất tốt!",
      "createdAt": "2026-01-04T10:30:00"
    },
    {
      "id": "uuid",
      "userId": "uuid",
      "userName": "Trần Thị B",
      "userEmail": "user2@example.com",
      "productId": "uuid",
      "productName": "Tên sản phẩm",
      "rating": 4,
      "comment": "Khá tốt",
      "createdAt": "2026-01-03T15:20:00"
    }
  ]
}
```

**Error Response:**
- `3001`: Không tìm thấy sản phẩm

---

## 6. Lấy đánh giá của tôi

**Endpoint:** `GET /reviews/my-reviews`

**Yêu cầu xác thực:** Có (Role: USER, ADMIN)

**Response Success (200):**
```json
{
  "code": 1000,
  "message": null,
  "result": [
    {
      "id": "uuid",
      "userId": "uuid",
      "userName": "Nguyễn Văn A",
      "userEmail": "user@example.com",
      "productId": "uuid",
      "productName": "Tên sản phẩm 1",
      "rating": 5,
      "comment": "Sản phẩm rất tốt!",
      "createdAt": "2026-01-04T10:30:00"
    },
    {
      "id": "uuid",
      "userId": "uuid",
      "userName": "Nguyễn Văn A",
      "userEmail": "user@example.com",
      "productId": "uuid",
      "productName": "Tên sản phẩm 2",
      "rating": 4,
      "comment": "Tạm được",
      "createdAt": "2026-01-03T14:20:00"
    }
  ]
}
```

**Error Response:**
- `6004`: Người dùng không tồn tại

---

## 7. Lấy thống kê đánh giá sản phẩm

**Endpoint:** `GET /reviews/product/{productId}/rating`

**Yêu cầu xác thực:** Không

**Path Parameters:**
- `productId`: ID của sản phẩm

**Response Success (200):**
```json
{
  "code": 1000,
  "message": null,
  "result": {
    "productId": "uuid",
    "productName": "Tên sản phẩm",
    "averageRating": 4.5,
    "totalReviews": 120,
    "ratingDistribution": {
      "1": 5,
      "2": 8,
      "3": 15,
      "4": 42,
      "5": 50
    }
  }
}
```

**Giải thích:**
- `averageRating`: Điểm đánh giá trung bình (làm tròn 1 chữ số thập phân)
- `totalReviews`: Tổng số đánh giá
- `ratingDistribution`: Phân bố số lượng đánh giá theo từng mức sao (1-5)

**Error Response:**
- `3001`: Không tìm thấy sản phẩm

---

## 8. Lấy tất cả đánh giá (Admin)

**Endpoint:** `GET /reviews/all`

**Yêu cầu xác thực:** Có (Role: ADMIN)

**Response Success (200):**
```json
{
  "code": 1000,
  "message": null,
  "result": [
    {
      "id": "uuid",
      "userId": "uuid",
      "userName": "Nguyễn Văn A",
      "userEmail": "user@example.com",
      "productId": "uuid",
      "productName": "Tên sản phẩm",
      "rating": 5,
      "comment": "Sản phẩm rất tốt!",
      "createdAt": "2026-01-04T10:30:00"
    }
  ]
}
```

---

## Lưu ý quan trọng

### Quyền truy cập
- **Public (Không cần xác thực):**
  - Xem đánh giá theo ID
  - Xem danh sách đánh giá theo sản phẩm
  - Xem thống kê đánh giá sản phẩm

- **USER & ADMIN:**
  - Tạo đánh giá mới
  - Cập nhật đánh giá của mình
  - Xóa đánh giá của mình
  - Xem đánh giá của mình

- **ADMIN:**
  - Xóa bất kỳ đánh giá nào
  - Xem tất cả đánh giá trong hệ thống

### Ràng buộc nghiệp vụ
1. Mỗi user chỉ được đánh giá 1 lần cho 1 sản phẩm
2. Đánh giá từ 1-5 sao (bắt buộc)
3. Comment tùy chọn, tối đa 1000 ký tự
4. User chỉ có thể sửa/xóa đánh giá của chính mình
5. Admin có thể xóa bất kỳ đánh giá nào

### Các mã lỗi (Error Code)
- `6001`: Không tìm thấy đánh giá
- `6002`: Bạn đã đánh giá sản phẩm này rồi
- `6003`: Bạn không có quyền thực hiện thao tác này
- `6004`: Người dùng không tồn tại
- `3001`: Không tìm thấy sản phẩm

---

## Ví dụ sử dụng

### 1. Tạo đánh giá mới
```bash
curl -X POST "http://localhost:8080/api/v1/reviews" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "123e4567-e89b-12d3-a456-426614174000",
    "rating": 5,
    "comment": "Sản phẩm rất tốt, giao hàng nhanh!"
  }'
```

### 2. Lấy đánh giá của sản phẩm
```bash
curl -X GET "http://localhost:8080/api/v1/reviews/product/123e4567-e89b-12d3-a456-426614174000"
```

### 3. Xem thống kê đánh giá
```bash
curl -X GET "http://localhost:8080/api/v1/reviews/product/123e4567-e89b-12d3-a456-426614174000/rating"
```

### 4. Cập nhật đánh giá
```bash
curl -X PUT "http://localhost:8080/api/v1/reviews/789e4567-e89b-12d3-a456-426614174000" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "rating": 4,
    "comment": "Cập nhật: Sản phẩm tốt nhưng giao hơi chậm"
  }'
```

### 5. Xóa đánh giá
```bash
curl -X DELETE "http://localhost:8080/api/v1/reviews/789e4567-e89b-12d3-a456-426614174000" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

