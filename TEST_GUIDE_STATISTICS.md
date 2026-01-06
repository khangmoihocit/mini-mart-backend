# Test Guide - Statistics API

## Hướng dẫn test các API thống kê

### Yêu cầu
1. Backend đang chạy
2. Có token JWT với quyền ADMIN
3. Database có dữ liệu mẫu

---

## 1. Lấy token ADMIN

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@gmail.com",
    "password": "admin123"
  }'
```

**Response:**
```json
{
  "code": 1000,
  "message": "Đăng nhập thành công!",
  "result": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "authenticated": true
  }
}
```

Lưu token để sử dụng cho các request tiếp theo.

---

## 2. Test thống kê sản phẩm

**Request:**
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/products" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**Expected Response:**
```json
{
  "code": 1000,
  "message": "Lấy thống kê sản phẩm thành công!",
  "result": {
    "totalProducts": 16,
    "outOfStockProducts": 0,
    "lowStockProducts": 5,
    "totalInventoryValue": 15000000.00,
    "averagePrice": 250000.00
  }
}
```

---

## 3. Test thống kê đơn hàng

**Request:**
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/orders" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**Expected Response:**
```json
{
  "code": 1000,
  "message": "Lấy thống kê đơn hàng thành công!",
  "result": {
    "totalOrders": 50,
    "pendingOrders": 5,
    "processingOrders": 10,
    "shippedOrders": 8,
    "deliveredOrders": 25,
    "cancelledOrders": 2,
    "totalRevenue": 12500000.00,
    "pendingRevenue": 2500000.00,
    "averageOrderValue": 250000.00
  }
}
```

---

## 4. Test top sản phẩm bán chạy

**Request:**
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/top-products?limit=5" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**Expected Response:**
```json
{
  "code": 1000,
  "message": "Lấy danh sách sản phẩm bán chạy thành công!",
  "result": [
    {
      "productId": "uuid-product-1",
      "productName": "Áo thun nam cổ tròn",
      "totalSold": 150,
      "totalRevenue": 2250000.00
    },
    {
      "productId": "uuid-product-2",
      "productName": "Quần jean nữ",
      "totalSold": 120,
      "totalRevenue": 3600000.00
    }
  ]
}
```

---

## 5. Test top sản phẩm theo thời gian

**Request:**
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/top-products/by-date?startDate=2024-01-01&endDate=2024-12-31&limit=10" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**Expected Response:**
```json
{
  "code": 1000,
  "message": "Lấy danh sách sản phẩm bán chạy theo thời gian thành công!",
  "result": [
    {
      "productId": "uuid-1",
      "productName": "Áo khoác bomber",
      "totalSold": 85,
      "totalRevenue": 4250000.00
    },
    {
      "productId": "uuid-2",
      "productName": "Váy midi",
      "totalSold": 70,
      "totalRevenue": 2800000.00
    }
  ]
}
```

---

## 6. Test doanh thu theo ngày

**Request:**
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/revenue/by-date?startDate=2024-01-01&endDate=2024-01-07" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**Expected Response:**
```json
{
  "code": 1000,
  "message": "Lấy thống kê doanh thu theo ngày thành công!",
  "result": [
    {
      "date": "2024-01-01",
      "orderCount": 5,
      "totalRevenue": 1250000.00
    },
    {
      "date": "2024-01-02",
      "orderCount": 8,
      "totalRevenue": 2000000.00
    },
    {
      "date": "2024-01-03",
      "orderCount": 6,
      "totalRevenue": 1500000.00
    }
  ]
}
```

---

## 7. Test thống kê theo danh mục

**Request:**
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/categories" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**Expected Response:**
```json
{
  "code": 1000,
  "message": "Lấy thống kê theo danh mục thành công!",
  "result": [
    {
      "categoryId": "uuid-cat-1",
      "categoryName": "Áo",
      "productCount": 8,
      "totalSold": 200,
      "totalRevenue": 5000000.00
    },
    {
      "categoryId": "uuid-cat-2",
      "categoryName": "Quần",
      "productCount": 5,
      "totalSold": 150,
      "totalRevenue": 4500000.00
    },
    {
      "categoryId": "uuid-cat-3",
      "categoryName": "Phụ kiện",
      "productCount": 3,
      "totalSold": 80,
      "totalRevenue": 1600000.00
    }
  ]
}
```

---

## Test với Postman

### Import Collection

1. Tạo Collection mới: "Statistics API"
2. Thêm Environment variable:
   - `base_url`: http://localhost:8080
   - `admin_token`: (lấy từ login)

### Các request:

#### 1. Product Statistics
- **Method**: GET
- **URL**: {{base_url}}/api/v1/statistics/products
- **Headers**: Authorization: Bearer {{admin_token}}

#### 2. Order Statistics
- **Method**: GET
- **URL**: {{base_url}}/api/v1/statistics/orders
- **Headers**: Authorization: Bearer {{admin_token}}

#### 3. Top Products
- **Method**: GET
- **URL**: {{base_url}}/api/v1/statistics/top-products?limit=10
- **Headers**: Authorization: Bearer {{admin_token}}

#### 4. Top Products by Date
- **Method**: GET
- **URL**: {{base_url}}/api/v1/statistics/top-products/by-date?startDate=2024-01-01&endDate=2024-12-31&limit=10
- **Headers**: Authorization: Bearer {{admin_token}}

#### 5. Revenue by Date
- **Method**: GET
- **URL**: {{base_url}}/api/v1/statistics/revenue/by-date?startDate=2024-01-01&endDate=2024-01-31
- **Headers**: Authorization: Bearer {{admin_token}}

#### 6. Category Statistics
- **Method**: GET
- **URL**: {{base_url}}/api/v1/statistics/categories
- **Headers**: Authorization: Bearer {{admin_token}}

---

## Test Error Cases

### 1. Test không có token
**Request:**
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/products"
```

**Expected Response:**
```json
{
  "code": 1006,
  "message": "Unauthenticated"
}
```

### 2. Test với token không hợp lệ
**Request:**
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/products" \
  -H "Authorization: Bearer invalid_token"
```

**Expected Response:**
```json
{
  "code": 1006,
  "message": "Unauthenticated"
}
```

### 3. Test với user không phải ADMIN
**Request:**
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/products" \
  -H "Authorization: Bearer USER_TOKEN"
```

**Expected Response:**
```json
{
  "code": 1007,
  "message": "You do not have permission"
}
```

### 4. Test với format ngày sai
**Request:**
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/revenue/by-date?startDate=01-01-2024&endDate=31-01-2024" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**Expected Response:**
```json
{
  "code": 1003,
  "message": "Invalid date format"
}
```

---

## Checklist Test

### Functional Tests
- [ ] API trả về đúng structure
- [ ] Các số liệu thống kê chính xác
- [ ] Query performance tốt
- [ ] Xử lý null/empty data đúng

### Security Tests
- [ ] Yêu cầu authentication
- [ ] Chỉ ADMIN có quyền truy cập
- [ ] Token hết hạn được xử lý đúng
- [ ] Invalid token bị reject

### Edge Cases
- [ ] Database trống
- [ ] Không có đơn hàng DELIVERED
- [ ] Date range không hợp lệ
- [ ] Limit = 0 hoặc âm

### Performance Tests
- [ ] Response time < 1s với 1000 products
- [ ] Response time < 2s với 10000 orders
- [ ] Complex queries được tối ưu
- [ ] Database connection pool đủ

---

## Kết quả mong đợi

✅ Tất cả API trả về đúng format  
✅ Data chính xác theo business logic  
✅ Security hoạt động đúng  
✅ Performance đạt yêu cầu  
✅ Error handling đầy đủ  

---

## Troubleshooting

### Lỗi 403 Forbidden
- Kiểm tra token có quyền ADMIN không
- Kiểm tra token còn hạn không

### Lỗi 500 Internal Server Error
- Kiểm tra database connection
- Kiểm tra log server
- Kiểm tra data integrity

### Response rỗng
- Kiểm tra có dữ liệu trong database không
- Kiểm tra các điều kiện filter (status, date range)

### Slow response
- Kiểm tra database index
- Kiểm tra số lượng data
- Cân nhắc implement caching

---

**Updated**: 06/01/2026  
**Version**: 1.0.0

