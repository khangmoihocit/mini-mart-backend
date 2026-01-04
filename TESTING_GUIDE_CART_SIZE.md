# Hướng dẫn Test API Cart với Size Support

## Chuẩn bị

### 1. Chạy database migration
```sql
-- Chạy file: database_migration_add_size_to_cart.sql
```

### 2. Start application
```bash
.\mvnw.cmd spring-boot:run
```

---

## Test Cases

### Test 1: Thêm sản phẩm KHÔNG CÓ size vào giỏ

**Request:**
```http
POST http://localhost:8081/api/v1/cart/add
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "productId": "product-uuid-without-sizes",
  "quantity": 2
}
```

**Expected Response:**
```json
{
  "code": 1000,
  "message": "Thêm sản phẩm vào giỏ hàng thành công!",
  "result": {
    "cartId": "...",
    "userId": "...",
    "items": [
      {
        "id": "...",
        "productId": "...",
        "productName": "Sản phẩm A",
        "price": 100000,
        "salePrice": null,
        "imageUrl": "...",
        "quantity": 2,
        "subtotal": 200000,
        "productSizeId": null,
        "sizeName": null,
        "availableQuantity": 100
      }
    ],
    "totalItems": 1,
    "totalQuantity": 2,
    "totalAmount": 200000
  }
}
```

---

### Test 2: Thêm sản phẩm CÓ size vào giỏ

**Request:**
```http
POST http://localhost:8081/api/v1/cart/add
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "productId": "product-uuid-with-sizes",
  "productSizeId": "size-uuid-L",
  "quantity": 1
}
```

**Expected Response:**
```json
{
  "code": 1000,
  "message": "Thêm sản phẩm vào giỏ hàng thành công!",
  "result": {
    "items": [
      {
        "id": "...",
        "productId": "...",
        "productName": "Áo thun",
        "price": 200000,
        "salePrice": 150000,
        "imageUrl": "...",
        "quantity": 1,
        "subtotal": 150000,
        "productSizeId": "size-uuid-L",
        "sizeName": "L",
        "availableQuantity": 50
      }
    ],
    "totalItems": 1,
    "totalQuantity": 1,
    "totalAmount": 150000
  }
}
```

---

### Test 3: Thêm cùng sản phẩm nhưng khác size

**Request 1 - Size M:**
```http
POST http://localhost:8081/api/v1/cart/add
Content-Type: application/json

{
  "productId": "product-uuid",
  "productSizeId": "size-uuid-M",
  "quantity": 2
}
```

**Request 2 - Size L (cùng product):**
```http
POST http://localhost:8081/api/v1/cart/add
Content-Type: application/json

{
  "productId": "product-uuid",
  "productSizeId": "size-uuid-L",
  "quantity": 1
}
```

**Expected Result:**
- Giỏ hàng có **2 items riêng biệt** (cùng product nhưng khác size)

```json
{
  "items": [
    {
      "productId": "product-uuid",
      "sizeName": "M",
      "quantity": 2
    },
    {
      "productId": "product-uuid",
      "sizeName": "L",
      "quantity": 1
    }
  ],
  "totalItems": 2,
  "totalQuantity": 3
}
```

---

### Test 4: Thêm lại cùng sản phẩm + cùng size (tăng số lượng)

**Request 1:**
```http
POST http://localhost:8081/api/v1/cart/add
{
  "productId": "product-uuid",
  "productSizeId": "size-uuid-M",
  "quantity": 2
}
```

**Request 2 (thêm lại cùng product + size):**
```http
POST http://localhost:8081/api/v1/cart/add
{
  "productId": "product-uuid",
  "productSizeId": "size-uuid-M",
  "quantity": 3
}
```

**Expected Result:**
- Giỏ hàng chỉ có **1 item**
- Số lượng = 2 + 3 = **5**

```json
{
  "items": [
    {
      "productId": "product-uuid",
      "productSizeId": "size-uuid-M",
      "sizeName": "M",
      "quantity": 5
    }
  ]
}
```

---

### Test 5: Kiểm tra số lượng tồn kho không đủ (từ size)

**Giả sử:** Size M chỉ còn 3 cái

**Request:**
```http
POST http://localhost:8081/api/v1/cart/add
{
  "productId": "product-uuid",
  "productSizeId": "size-uuid-M",
  "quantity": 5
}
```

**Expected Response:**
```json
{
  "code": 4004,
  "message": "Số lượng sản phẩm trong kho không đủ."
}
```

---

### Test 6: Size không thuộc product

**Request:**
```http
POST http://localhost:8081/api/v1/cart/add
{
  "productId": "product-A-uuid",
  "productSizeId": "size-of-product-B-uuid",
  "quantity": 1
}
```

**Expected Response:**
```json
{
  "code": 3005,
  "message": "Size không thuộc sản phẩm này."
}
```

---

### Test 7: Size không tồn tại

**Request:**
```http
POST http://localhost:8081/api/v1/cart/add
{
  "productId": "product-uuid",
  "productSizeId": "invalid-size-uuid",
  "quantity": 1
}
```

**Expected Response:**
```json
{
  "code": 3004,
  "message": "Không tìm thấy size sản phẩm."
}
```

---

### Test 8: Cập nhật số lượng cart item có size

**Request:**
```http
PUT http://localhost:8081/api/v1/cart/items/{cartDetailId}
Content-Type: application/json

{
  "quantity": 5
}
```

**Expected Response:**
- Kiểm tra số lượng từ size
- Nếu đủ → cập nhật thành công
- Nếu không đủ → trả về lỗi 4004

---

### Test 9: Lấy giỏ hàng

**Request:**
```http
GET http://localhost:8081/api/v1/cart
Authorization: Bearer <your-jwt-token>
```

**Expected Response:**
- Tất cả items trong giỏ
- Mỗi item có đầy đủ thông tin size (nếu có)
- `availableQuantity` hiển thị số lượng còn lại

---

### Test 10: Xóa cart item có size

**Request:**
```http
DELETE http://localhost:8081/api/v1/cart/items/{cartDetailId}
```

**Expected Response:**
```json
{
  "code": 1000,
  "message": "Xóa sản phẩm khỏi giỏ hàng thành công!",
  "result": {
    // Giỏ hàng sau khi xóa
  }
}
```

---

## Test Product Search Sorting

### Test 11: Sort theo giá tăng dần (price_asc)

**Request:**
```http
GET http://localhost:8081/api/v1/products/search?sortBy=price_asc&pageNo=1&pageSize=10
```

**Expected Result:**
- ✅ Không có lỗi UnsupportedOperationException
- Sản phẩm được sort theo giá tăng dần (salePrice nếu có, nếu không lấy price)

---

### Test 12: Sort theo giá giảm dần (price_desc)

**Request:**
```http
GET http://localhost:8081/api/v1/products/search?sortBy=price_desc&pageNo=1&pageSize=10
```

**Expected Result:**
- ✅ Không có lỗi
- Sản phẩm được sort theo giá giảm dần

---

### Test 13: Sort theo mới nhất (newest)

**Request:**
```http
GET http://localhost:8081/api/v1/products/search?sortBy=newest&pageNo=1&pageSize=10
```

**Expected Result:**
- ✅ Không có lỗi
- Sản phẩm được sort theo createdAt giảm dần

---

## Postman Collection

Bạn có thể import collection này vào Postman:

```json
{
  "info": {
    "name": "Cart API with Size Support",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Add to cart - no size",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          },
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"productId\": \"{{productId}}\",\n  \"quantity\": 2\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/v1/cart/add",
          "host": ["{{baseUrl}}"],
          "path": ["api", "v1", "cart", "add"]
        }
      }
    },
    {
      "name": "Add to cart - with size",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          },
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"productId\": \"{{productId}}\",\n  \"productSizeId\": \"{{sizeId}}\",\n  \"quantity\": 1\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/v1/cart/add",
          "host": ["{{baseUrl}}"],
          "path": ["api", "v1", "cart", "add"]
        }
      }
    },
    {
      "name": "Get cart",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/api/v1/cart",
          "host": ["{{baseUrl}}"],
          "path": ["api", "v1", "cart"]
        }
      }
    },
    {
      "name": "Update cart item",
      "request": {
        "method": "PUT",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          },
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"quantity\": 5\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/v1/cart/items/{{cartDetailId}}",
          "host": ["{{baseUrl}}"],
          "path": ["api", "v1", "cart", "items", "{{cartDetailId}}"]
        }
      }
    },
    {
      "name": "Remove cart item",
      "request": {
        "method": "DELETE",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/api/v1/cart/items/{{cartDetailId}}",
          "host": ["{{baseUrl}}"],
          "path": ["api", "v1", "cart", "items", "{{cartDetailId}}"]
        }
      }
    },
    {
      "name": "Search products - sort by price asc",
      "request": {
        "method": "GET",
        "url": {
          "raw": "{{baseUrl}}/api/v1/products/search?sortBy=price_asc&pageNo=1&pageSize=10",
          "host": ["{{baseUrl}}"],
          "path": ["api", "v1", "products", "search"],
          "query": [
            {
              "key": "sortBy",
              "value": "price_asc"
            },
            {
              "key": "pageNo",
              "value": "1"
            },
            {
              "key": "pageSize",
              "value": "10"
            }
          ]
        }
      }
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8081"
    },
    {
      "key": "token",
      "value": "your-jwt-token-here"
    },
    {
      "key": "productId",
      "value": ""
    },
    {
      "key": "sizeId",
      "value": ""
    },
    {
      "key": "cartDetailId",
      "value": ""
    }
  ]
}
```

---

## Lưu ý khi Test

1. **Database Migration:** Nhớ chạy SQL migration trước khi test
2. **Authentication:** Cần có JWT token hợp lệ
3. **Product Setup:** Tạo products với sizes trước khi test
4. **Stock Quantity:** Đảm bảo product và size có số lượng tồn kho để test

---

## Kết quả mong đợi

✅ Tất cả 13 test cases phải pass
✅ Không có lỗi UnsupportedOperationException khi sort
✅ Giỏ hàng hoạt động đúng với cả sản phẩm có và không có size
✅ Validation số lượng tồn kho chính xác từ size

