# Mini Mart Backend - API Documentation

## 📋 Mô tả dự án

** Backend** là một hệ thống backend RESTful API cho ứng dụng bán hàng mini mart, được phát triển bằng Spring Boot 3.5.4 với Java 21.

### Công nghệ sử dụng
- **Framework**: Spring Boot 3.5.4
- **Java Version**: 21
- **Database**: MySQL
- **Security**: Spring Security với JWT Authentication
- **ORM**: Spring Data JPA (Hibernate)
- **Build Tool**: Maven
- **Mapping**: MapStruct
- **Libraries**: Lombok, P6Spy

### Tính năng chính
1. ✅ Xác thực và phân quyền (JWT Authentication)
2. ✅ Quản lý người dùng (User Management)
3. ✅ Quản lý sản phẩm (Product Management)
4. ✅ Quản lý danh mục (Category Management)
5. ✅ Giỏ hàng (Shopping Cart)
6. ✅ Đánh giá sản phẩm (Product Reviews)
7. ✅ Upload và quản lý hình ảnh

### Cấu trúc Database
- **users**: Thông tin người dùng
- **roles**: Vai trò (ADMIN, USER)
- **products**: Sản phẩm
- **categories**: Danh mục sản phẩm
- **product_images**: Hình ảnh sản phẩm
- **product_sizes**: Kích thước sản phẩm
- **reviews**: Đánh giá sản phẩm
- **carts**: Giỏ hàng
- **cart_details**: Chi tiết giỏ hàng
- **orders**: Đơn hàng
- **order_details**: Chi tiết đơn hàng
- **tokens**: JWT tokens
- **coupons**: Mã giảm giá
- **coupon_conditions**: Điều kiện mã giảm giá

---

## 🔐 Authentication APIs

**Base URL**: `/api/v1/auth`

### 1. Đăng nhập
**Endpoint**: `POST /api/v1/auth/log-in`

**Description**: Xác thực người dùng và tạo JWT token

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "authenticated": true
  }
}
```

**Note**: 
- Access Token có hiệu lực: 2 giờ
- Refresh Token có hiệu lực: 5 ngày
- Refresh Token sẽ được lưu trong HTTP-only cookie

---

### 2. Làm mới token
**Endpoint**: `POST /api/v1/auth/refresh`

**Description**: Làm mới access token khi hết hạn

**Headers**:
```
Cookie: refreshToken=<refresh_token>
```

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "authenticated": true
  }
}
```

---

### 3. Đăng xuất
**Endpoint**: `POST /api/v1/auth/logout`

**Description**: Đăng xuất và vô hiệu hóa token

**Headers**:
```
Authorization: Bearer <access_token>
Cookie: refreshToken=<refresh_token>
```

**Response**:
```json
{
  "code": 1000,
  "message": "Logged out successfully."
}
```

---

## 👤 User APIs

**Base URL**: `/api/v1/users`

### 1. Tạo người dùng mới
**Endpoint**: `POST /api/v1/users`

**Description**: Đăng ký tài khoản mới

**Request Body**:
```json
{
  "fullName": "Nguyen Van A",
  "email": "nguyenvana@example.com",
  "phoneNumber": "0123456789",
  "address": "123 Nguyen Hue, TPHCM",
  "password": "password123",
  "dateOfBirth": "1990-01-01"
}
```

**Validation**:
- `email`: Phải là email hợp lệ, không được trùng
- `phoneNumber`: Bắt buộc
- `password`: Tối thiểu 6 ký tự
- `fullName`: Bắt buộc

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "uuid-string",
    "fullName": "Nguyen Van A",
    "email": "nguyenvana@example.com",
    "phoneNumber": "0123456789",
    "address": "123 Nguyen Hue, TPHCM",
    "dateOfBirth": "1990-01-01",
    "isActive": true,
    "role": "USER"
  }
}
```

---

### 2. Lấy thông tin cá nhân
**Endpoint**: `GET /api/v1/users/myInfo`

**Description**: Lấy thông tin người dùng đang đăng nhập

**Headers**:
```
Authorization: Bearer <access_token>
```

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "uuid-string",
    "fullName": "Nguyen Van A",
    "email": "nguyenvana@example.com",
    "phoneNumber": "0123456789",
    "address": "123 Nguyen Hue, TPHCM",
    "dateOfBirth": "1990-01-01",
    "isActive": true,
    "role": "USER"
  }
}
```

---

### 3. Cập nhật thông tin người dùng
**Endpoint**: `PUT /api/v1/users/{id}`

**Description**: Cập nhật thông tin người dùng

**Headers**:
```
Authorization: Bearer <access_token>
```

**Request Body**:
```json
{
  "fullName": "Nguyen Van B",
  "phoneNumber": "0987654321",
  "address": "456 Le Loi, TPHCM",
  "dateOfBirth": "1990-01-01"
}
```

**Response**: Tương tự như response của API tạo người dùng

---

### 4. Lấy danh sách người dùng (Admin)
**Endpoint**: `GET /api/v1/users`

**Description**: Lấy tất cả người dùng (chỉ ADMIN)

**Headers**:
```
Authorization: Bearer <access_token>
```

**Authorization**: `ROLE_ADMIN`

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "id": "uuid-string",
      "fullName": "Nguyen Van A",
      "email": "nguyenvana@example.com",
      "phoneNumber": "0123456789",
      "role": "USER"
    }
  ]
}
```

---

### 5. Tìm kiếm người dùng (Admin)
**Endpoint**: `GET /api/v1/users/search`

**Description**: Tìm kiếm người dùng theo từ khóa với phân trang

**Headers**:
```
Authorization: Bearer <access_token>
```

**Authorization**: `ROLE_ADMIN`

**Query Parameters**:
- `pageNo` (optional, default: 1): Số trang
- `pageSize` (optional, default: 5): Số lượng mỗi trang
- `keyword` (optional): Từ khóa tìm kiếm (tên, email, số điện thoại)

**Example**: `GET /api/v1/users/search?pageNo=1&pageSize=10&keyword=nguyen`

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "content": [...],
    "totalElements": 50,
    "totalPages": 5,
    "size": 10,
    "number": 0
  }
}
```

---

### 6. Xóa người dùng (Admin)
**Endpoint**: `DELETE /api/v1/users/{id}`

**Description**: Xóa người dùng

**Headers**:
```
Authorization: Bearer <access_token>
```

**Authorization**: `ROLE_ADMIN`

**Response**:
```json
{
  "code": 1000,
  "message": "Xóa user thành công!"
}
```

---

## 📦 Product APIs

**Base URL**: `/api/v1/products`

### 1. Tạo sản phẩm mới (Admin)
**Endpoint**: `POST /api/v1/products`

**Description**: Tạo sản phẩm mới với hình ảnh

**Headers**:
```
Authorization: Bearer <access_token>
Content-Type: multipart/form-data
```

**Authorization**: `ROLE_ADMIN`

**Request Body** (Form Data):
```
name: "Coca Cola 330ml"
price: 15000
salePrice: 12000
description: "Nước ngọt có ga Coca Cola"
stockQuantity: 100
categoryId: "category-uuid"
files: [file1.jpg, file2.jpg]
```

**Response**:
```json
{
  "code": 1000,
  "message": "Tạo sản phẩm thành công!",
  "result": {
    "id": "product-uuid",
    "name": "Coca Cola 330ml",
    "price": 15000,
    "salePrice": 12000,
    "description": "Nước ngọt có ga Coca Cola",
    "stockQuantity": 100,
    "category": {
      "id": "category-uuid",
      "name": "Đồ uống"
    },
    "images": [
      {
        "id": "image-uuid",
        "imageUrl": "uploads/abc-123.jpg"
      }
    ]
  }
}
```

---

### 2. Lấy tất cả sản phẩm
**Endpoint**: `GET /api/v1/products`

**Description**: Lấy danh sách tất cả sản phẩm

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "id": "product-uuid",
      "name": "Coca Cola 330ml",
      "price": 15000,
      "salePrice": 12000,
      "stockQuantity": 100,
      "category": {...},
      "images": [...]
    }
  ]
}
```

---

### 3. Lấy sản phẩm theo danh mục
**Endpoint**: `GET /api/v1/products/by-category`

**Description**: Lấy sản phẩm theo ID danh mục

**Query Parameters**:
- `categoryId` (required): ID của danh mục

**Example**: `GET /api/v1/products/by-category?categoryId=category-uuid`

**Response**: Tương tự API lấy tất cả sản phẩm

---

### 4. Tìm kiếm sản phẩm
**Endpoint**: `GET /api/v1/products/search`

**Description**: Tìm kiếm sản phẩm với phân trang

**Query Parameters**:
- `pageNo` (optional, default: 1): Số trang
- `pageSize` (optional, default: 5): Số lượng mỗi trang
- `keyword` (optional): Từ khóa tìm kiếm

**Example**: `GET /api/v1/products/search?pageNo=1&pageSize=10&keyword=coca`

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "content": [...],
    "totalElements": 20,
    "totalPages": 2,
    "size": 10,
    "number": 0
  }
}
```

---

### 5. Tìm kiếm nâng cao
**Endpoint**: `GET /api/v1/products/advanced-search`

**Description**: Tìm kiếm sản phẩm với nhiều điều kiện lọc

**Query Parameters**:
- `keyword` (optional): Từ khóa tìm kiếm
- `categoryId` (optional): Lọc theo danh mục
- `minPrice` (optional): Giá tối thiểu
- `maxPrice` (optional): Giá tối đa
- `pageNo` (optional, default: 1): Số trang
- `pageSize` (optional, default: 10): Số lượng mỗi trang
- `sortBy` (optional): Sắp xếp theo (price, name)
- `sortOrder` (optional): Thứ tự (asc, desc)

**Example**: 
```
GET /api/v1/products/advanced-search?categoryId=cat-uuid&minPrice=10000&maxPrice=50000&sortBy=price&sortOrder=asc
```

---

### 6. Cập nhật sản phẩm (Admin)
**Endpoint**: `PUT /api/v1/products/{id}`

**Description**: Cập nhật thông tin sản phẩm

**Headers**:
```
Authorization: Bearer <access_token>
Content-Type: multipart/form-data
```

**Authorization**: `ROLE_ADMIN`

**Request Body** (Form Data): Tương tự như tạo sản phẩm

**Response**:
```json
{
  "code": 1000,
  "message": "Cập nhật sản phẩm thành công!",
  "result": {...}
}
```

---

### 7. Cập nhật hình ảnh sản phẩm (Admin)
**Endpoint**: `POST /api/v1/products/update-images/{id}`

**Description**: Cập nhật hình ảnh của sản phẩm

**Headers**:
```
Authorization: Bearer <access_token>
Content-Type: multipart/form-data
```

**Authorization**: `ROLE_ADMIN`

**Request Body** (Form Data):
```
files: [newfile1.jpg, newfile2.jpg]
keepImageIds: ["image-uuid-1", "image-uuid-2"]
```

**Response**:
```json
{
  "code": 1000,
  "message": "Cập nhật hình ảnh sản phẩm thành công!",
  "result": {...}
}
```

---

### 8. Xóa sản phẩm (Admin)
**Endpoint**: `DELETE /api/v1/products/{id}`

**Description**: Xóa sản phẩm

**Headers**:
```
Authorization: Bearer <access_token>
```

**Authorization**: `ROLE_ADMIN`

**Response**:
```json
{
  "code": 1000,
  "message": "Xóa sản phẩm thành công!"
}
```

---

## 📑 Category APIs

**Base URL**: `/api/v1/categories`

### 1. Tạo danh mục (Admin)
**Endpoint**: `POST /api/v1/categories`

**Description**: Tạo danh mục sản phẩm mới

**Headers**:
```
Authorization: Bearer <access_token>
```

**Authorization**: `ROLE_ADMIN`

**Request Body**:
```json
{
  "name": "Đồ uống"
}
```

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "category-uuid",
    "name": "Đồ uống"
  }
}
```

---

### 2. Lấy tất cả danh mục
**Endpoint**: `GET /api/v1/categories`

**Description**: Lấy danh sách tất cả danh mục

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "id": "category-uuid",
      "name": "Đồ uống"
    },
    {
      "id": "category-uuid-2",
      "name": "Snack"
    }
  ]
}
```

---

### 3. Lấy danh mục theo ID
**Endpoint**: `GET /api/v1/categories/{id}`

**Description**: Lấy thông tin chi tiết danh mục

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "category-uuid",
    "name": "Đồ uống"
  }
}
```

---

### 4. Cập nhật danh mục (Admin)
**Endpoint**: `PUT /api/v1/categories/{id}`

**Description**: Cập nhật thông tin danh mục

**Headers**:
```
Authorization: Bearer <access_token>
```

**Authorization**: `ROLE_ADMIN`

**Request Body**:
```json
{
  "name": "Nước giải khát"
}
```

**Response**: Tương tự như response của API tạo danh mục

---

## 🛒 Cart APIs

**Base URL**: `/api/v1/cart`

### 1. Lấy giỏ hàng
**Endpoint**: `GET /api/v1/cart`

**Description**: Lấy giỏ hàng của người dùng hiện tại

**Headers**:
```
Authorization: Bearer <access_token>
```

**Response**:
```json
{
  "code": 1000,
  "message": "Lấy giỏ hàng thành công!",
  "result": {
    "id": "cart-uuid",
    "userId": "user-uuid",
    "items": [
      {
        "id": "cart-detail-uuid",
        "product": {
          "id": "product-uuid",
          "name": "Coca Cola 330ml",
          "price": 15000,
          "salePrice": 12000,
          "images": [...]
        },
        "quantity": 2,
        "subtotal": 24000
      }
    ],
    "totalAmount": 24000,
    "itemCount": 2
  }
}
```

---

### 2. Thêm sản phẩm vào giỏ
**Endpoint**: `POST /api/v1/cart/add`

**Description**: Thêm sản phẩm vào giỏ hàng

**Headers**:
```
Authorization: Bearer <access_token>
```

**Request Body**:
```json
{
  "productId": "product-uuid",
  "quantity": 2,
  "productSizeId": "size-uuid"
}
```

**Validation**:
- `productId`: Bắt buộc
- `quantity`: Phải > 0

**Response**:
```json
{
  "code": 1000,
  "message": "Thêm sản phẩm vào giỏ hàng thành công!",
  "result": {...}
}
```

---

### 3. Cập nhật số lượng
**Endpoint**: `PUT /api/v1/cart/items/{cartDetailId}`

**Description**: Cập nhật số lượng của sản phẩm trong giỏ

**Headers**:
```
Authorization: Bearer <access_token>
```

**Request Body**:
```json
{
  "quantity": 5
}
```

**Response**:
```json
{
  "code": 1000,
  "message": "Cập nhật giỏ hàng thành công!",
  "result": {...}
}
```

---

### 4. Xóa sản phẩm khỏi giỏ
**Endpoint**: `DELETE /api/v1/cart/items/{cartDetailId}`

**Description**: Xóa một sản phẩm khỏi giỏ hàng

**Headers**:
```
Authorization: Bearer <access_token>
```

**Response**:
```json
{
  "code": 1000,
  "message": "Xóa sản phẩm khỏi giỏ hàng thành công!",
  "result": {...}
}
```

---

### 5. Xóa toàn bộ giỏ hàng
**Endpoint**: `DELETE /api/v1/cart/clear`

**Description**: Xóa tất cả sản phẩm trong giỏ hàng

**Headers**:
```
Authorization: Bearer <access_token>
```

**Response**:
```json
{
  "code": 1000,
  "message": "Đã xóa toàn bộ giỏ hàng!"
}
```

---

### 6. Đếm số lượng items
**Endpoint**: `GET /api/v1/cart/count`

**Description**: Lấy tổng số lượng items trong giỏ hàng

**Headers**:
```
Authorization: Bearer <access_token>
```

**Response**:
```json
{
  "code": 1000,
  "message": "Lấy số lượng items thành công!",
  "result": 5
}
```

---

## ⭐ Review APIs

**Base URL**: `/api/v1/reviews`

### 1. Tạo đánh giá
**Endpoint**: `POST /api/v1/reviews`

**Description**: Tạo đánh giá cho sản phẩm

**Headers**:
```
Authorization: Bearer <access_token>
```

**Authorization**: `ROLE_USER` hoặc `ROLE_ADMIN`

**Request Body**:
```json
{
  "productId": "product-uuid",
  "rating": 5,
  "comment": "Sản phẩm rất tốt, giao hàng nhanh!"
}
```

**Validation**:
- `productId`: Bắt buộc
- `rating`: Từ 1 đến 5
- `comment`: Optional

**Response**:
```json
{
  "code": 1000,
  "message": "Tạo đánh giá thành công!",
  "result": {
    "id": "review-uuid",
    "user": {
      "id": "user-uuid",
      "fullName": "Nguyen Van A"
    },
    "product": {
      "id": "product-uuid",
      "name": "Coca Cola 330ml"
    },
    "rating": 5,
    "comment": "Sản phẩm rất tốt, giao hàng nhanh!",
    "createdAt": "2026-01-04T10:30:00"
  }
}
```

---

### 2. Cập nhật đánh giá
**Endpoint**: `PUT /api/v1/reviews/{reviewId}`

**Description**: Cập nhật đánh giá của mình

**Headers**:
```
Authorization: Bearer <access_token>
```

**Authorization**: `ROLE_USER` hoặc `ROLE_ADMIN`

**Request Body**:
```json
{
  "rating": 4,
  "comment": "Sản phẩm tốt"
}
```

**Response**:
```json
{
  "code": 1000,
  "message": "Cập nhật đánh giá thành công!",
  "result": {...}
}
```

**Note**: Chỉ có thể cập nhật đánh giá của chính mình

---

### 3. Xóa đánh giá
**Endpoint**: `DELETE /api/v1/reviews/{reviewId}`

**Description**: Xóa đánh giá

**Headers**:
```
Authorization: Bearer <access_token>
```

**Authorization**: `ROLE_USER` hoặc `ROLE_ADMIN`

**Response**:
```json
{
  "code": 1000,
  "message": "Xóa đánh giá thành công!"
}
```

**Note**: User chỉ có thể xóa đánh giá của mình, Admin có thể xóa mọi đánh giá

---

### 4. Lấy đánh giá theo ID
**Endpoint**: `GET /api/v1/reviews/{reviewId}`

**Description**: Lấy thông tin chi tiết một đánh giá

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "review-uuid",
    "user": {...},
    "product": {...},
    "rating": 5,
    "comment": "Sản phẩm rất tốt!",
    "createdAt": "2026-01-04T10:30:00"
  }
}
```

---

### 5. Lấy đánh giá theo sản phẩm
**Endpoint**: `GET /api/v1/reviews/product/{productId}`

**Description**: Lấy tất cả đánh giá của một sản phẩm

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "id": "review-uuid-1",
      "user": {...},
      "rating": 5,
      "comment": "Tuyệt vời!",
      "createdAt": "2026-01-04T10:30:00"
    },
    {
      "id": "review-uuid-2",
      "user": {...},
      "rating": 4,
      "comment": "Tốt",
      "createdAt": "2026-01-03T15:20:00"
    }
  ]
}
```

---

### 6. Lấy đánh giá của tôi
**Endpoint**: `GET /api/v1/reviews/my-reviews`

**Description**: Lấy tất cả đánh giá của người dùng hiện tại

**Headers**:
```
Authorization: Bearer <access_token>
```

**Authorization**: `ROLE_USER` hoặc `ROLE_ADMIN`

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "id": "review-uuid",
      "product": {
        "id": "product-uuid",
        "name": "Coca Cola 330ml"
      },
      "rating": 5,
      "comment": "Sản phẩm tốt!",
      "createdAt": "2026-01-04T10:30:00"
    }
  ]
}
```

---

### 7. Lấy thống kê đánh giá sản phẩm
**Endpoint**: `GET /api/v1/reviews/product/{productId}/rating`

**Description**: Lấy thống kê rating của sản phẩm

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "productId": "product-uuid",
    "averageRating": 4.5,
    "totalReviews": 100,
    "ratingDistribution": {
      "5": 60,
      "4": 25,
      "3": 10,
      "2": 3,
      "1": 2
    }
  }
}
```

---

### 8. Lấy tất cả đánh giá (Admin)
**Endpoint**: `GET /api/v1/reviews/all`

**Description**: Lấy tất cả đánh giá trong hệ thống

**Headers**:
```
Authorization: Bearer <access_token>
```

**Authorization**: `ROLE_ADMIN`

**Response**:
```json
{
  "code": 1000,
  "message": "Success",
  "result": [...]
}
```

---

## 📊 Response Format

### Success Response
```json
{
  "code": 1000,
  "message": "Success" | "Custom message",
  "result": {
    // Data object or array
  }
}
```

### Error Response
```json
{
  "code": <error_code>,
  "message": "Error message",
  "result": null
}
```

### Common Error Codes
- `1000`: Success
- `1001`: Uncategorized error
- `1002`: Invalid key
- `1003`: User existed
- `1004`: User not found
- `1005`: Unauthenticated
- `1006`: Unauthorized
- `1007`: Invalid token
- `1008`: Product not found
- `1009`: Category not found
- `1010`: Review not found
- `9999`: Unknown error

---

## 🔒 Security & Authorization

### Authentication
- Sử dụng JWT (JSON Web Token)
- Access Token được gửi qua header: `Authorization: Bearer <token>`
- Refresh Token được lưu trong HTTP-only cookie

### Roles
1. **USER**: Người dùng thông thường
   - Xem sản phẩm, danh mục
   - Quản lý giỏ hàng
   - Tạo và quản lý đánh giá của mình
   - Xem và cập nhật thông tin cá nhân

2. **ADMIN**: Quản trị viên
   - Tất cả quyền của USER
   - Quản lý sản phẩm (CRUD)
   - Quản lý danh mục (CRUD)
   - Quản lý người dùng
   - Xóa bất kỳ đánh giá nào
   - Xem thống kê hệ thống

---

## 🚀 Getting Started

### Prerequisites
- Java 21
- MySQL 8.0+
- Maven 3.6+

### Configuration
File `application.yaml`:
```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shopclothing
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

api:
  prefix: /api/v1

jwt:
  signerKey: <your-secret-key>
  valid-duration: 7200        # 2 hours
  refreshable-duration: 432000 # 5 days
```

### Run Application
```bash
# Using Maven
mvn spring-boot:run

# Or using Maven Wrapper
./mvnw spring-boot:run

# On Windows
mvnw.cmd spring-boot:run
```

Application sẽ chạy tại: `http://localhost:8081`

---

## 📝 Notes

### File Upload
- Hình ảnh sản phẩm được lưu trong thư mục `uploads/`
- Format được accept: `.jpg`, `.jpeg`, `.png`, `.webp`
- Tên file được tự động tạo UUID để tránh trùng lặp

### Pagination
- Mặc định: `pageNo=1`, `pageSize=5`
- Page number bắt đầu từ 1 (không phải 0)

### Validation
- Tất cả request body đều được validate
- Error message sẽ trả về chi tiết lỗi validation

### CORS
- Cần cấu hình CORS nếu frontend chạy ở domain khác
- Hiện tại có thể cấu hình trong `SecurityConfig.java`

---

**Version**: 0.0.1-SNAPSHOT  
**Last Updated**: January 4, 2026

