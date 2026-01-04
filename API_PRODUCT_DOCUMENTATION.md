# API Product Documentation - Hướng dẫn sử dụng API sản phẩm

## Tổng quan thay đổi
API thêm và sửa sản phẩm đã được cập nhật để xử lý đầy đủ thông tin về **sizes** (kích cỡ). Mỗi sản phẩm có thể có nhiều size khác nhau.

---

## 1. API Thêm sản phẩm mới (CREATE)

### Endpoint
```
POST /api/v1/products
```

### Headers
```
Authorization: Bearer {your_token}
Content-Type: multipart/form-data
```

### Request Body (Form-Data)
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | String | ✅ Yes | Tên sản phẩm |
| price | BigDecimal | ✅ Yes | Giá gốc sản phẩm (≥ 0) |
| salePrice | BigDecimal | ❌ No | Giá khuyến mãi |
| description | String | ✅ Yes | Mô tả sản phẩm |
| stockQuantity | Integer | ❌ No | Số lượng tồn kho (mặc định: 0) |
| categoryId | String | ✅ Yes | ID của danh mục |
| images | MultipartFile[] | ❌ No | Danh sách file ảnh |
| sizes[0].sizeName | String | ✅ Yes | Tên size (ví dụ: S, M, L, XL) |
| sizes[0].quantity | Integer | ✅ Yes | Số lượng của size đó (≥ 0) |
| sizes[1].sizeName | String | ❌ No | Size thứ 2 |
| sizes[1].quantity | Integer | ❌ No | Số lượng size thứ 2 |

### Ví dụ Request với Postman/Thunder Client:
```
Form-Data:
- name: Áo thun basic
- price: 199000
- salePrice: 149000
- description: Áo thun cotton 100% thoáng mát
- stockQuantity: 100
- categoryId: cat-123
- images: [file1.jpg, file2.jpg]
- sizes[0].sizeName: S
- sizes[0].quantity: 20
- sizes[1].sizeName: M
- sizes[1].quantity: 30
- sizes[2].sizeName: L
- sizes[2].quantity: 30
- sizes[3].sizeName: XL
- sizes[3].quantity: 20
```

### Response Success (200)
```json
{
  "code": 1000,
  "message": "Tạo sản phẩm thành công!",
  "result": {
    "id": "prod-abc123",
    "name": "Áo thun basic",
    "price": 199000,
    "salePrice": 149000,
    "description": "Áo thun cotton 100% thoáng mát",
    "stockQuantity": 100,
    "categoryId": "cat-123",
    "categoryName": "Áo thun",
    "images": [
      {
        "id": "img-1",
        "imageUrl": "uuid_file1.jpg"
      },
      {
        "id": "img-2",
        "imageUrl": "uuid_file2.jpg"
      }
    ],
    "sizes": [
      {
        "id": "size-1",
        "sizeName": "S",
        "quantity": 20
      },
      {
        "id": "size-2",
        "sizeName": "M",
        "quantity": 30
      },
      {
        "id": "size-3",
        "sizeName": "L",
        "quantity": 30
      },
      {
        "id": "size-4",
        "sizeName": "XL",
        "quantity": 20
      }
    ],
    "createdAt": "2026-01-04T10:30:00",
    "updatedAt": "2026-01-04T10:30:00"
  }
}
```

---

## 2. API Cập nhật sản phẩm (UPDATE)

### Endpoint
```
PUT /api/v1/products/{id}
```

### Headers
```
Authorization: Bearer {your_token}
Content-Type: multipart/form-data
```

### Path Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| id | String | ID của sản phẩm cần cập nhật |

### Request Body (Form-Data)
Giống như API CREATE, nhưng tất cả các trường đều optional (chỉ gửi những gì muốn cập nhật).

**Lưu ý quan trọng về SIZES:**
- Nếu gửi `sizes` trong request → Tất cả sizes cũ sẽ bị XÓA và thay thế bằng sizes mới
- Nếu KHÔNG gửi `sizes` → Giữ nguyên sizes hiện tại
- Không có khái niệm "update từng size", chỉ có thể thay thế toàn bộ

### Ví dụ Request:
```
Form-Data (chỉ update name, price và sizes):
- name: Áo thun basic Premium
- price: 249000
- sizes[0].sizeName: M
- sizes[0].quantity: 50
- sizes[1].sizeName: L
- sizes[1].quantity: 50
```

### Response Success (200)
```json
{
  "code": 1000,
  "message": "Cập nhật sản phẩm thành công!",
  "result": {
    "id": "prod-abc123",
    "name": "Áo thun basic Premium",
    "price": 249000,
    "salePrice": 149000,
    "description": "Áo thun cotton 100% thoáng mát",
    "stockQuantity": 100,
    "categoryId": "cat-123",
    "categoryName": "Áo thun",
    "images": [
      {
        "id": "img-1",
        "imageUrl": "uuid_file1.jpg"
      },
      {
        "id": "img-2",
        "imageUrl": "uuid_file2.jpg"
      }
    ],
    "sizes": [
      {
        "id": "size-5",
        "sizeName": "M",
        "quantity": 50
      },
      {
        "id": "size-6",
        "sizeName": "L",
        "quantity": 50
      }
    ],
    "createdAt": "2026-01-04T10:30:00",
    "updatedAt": "2026-01-04T11:45:00"
  }
}
```

---

## 3. API Cập nhật ảnh sản phẩm (UPDATE IMAGES)

### Endpoint
```
POST /api/v1/products/update-images/{id}
```

### Headers
```
Authorization: Bearer {your_token}
Content-Type: multipart/form-data
```

### Path Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| id | String | ID của sản phẩm |

### Request Body (Form-Data)
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| files | MultipartFile[] | ❌ No | Danh sách ảnh mới cần thêm |
| keepImageIds | String[] | ❌ No | Danh sách ID của ảnh cần giữ lại |

### Logic xử lý:
- Nếu **KHÔNG gửi** `keepImageIds` → Xóa TẤT CẢ ảnh cũ
- Nếu **gửi** `keepImageIds` → Chỉ xóa những ảnh KHÔNG có trong danh sách
- `files` → Thêm ảnh mới vào sản phẩm

### Ví dụ Request:
```
Scenario 1: Thay thế toàn bộ ảnh
Form-Data:
- files: [new1.jpg, new2.jpg, new3.jpg]
(Không gửi keepImageIds → xóa hết ảnh cũ)

Scenario 2: Giữ một số ảnh cũ + thêm ảnh mới
Form-Data:
- files: [new1.jpg]
- keepImageIds: [img-1, img-2]
(Giữ lại img-1 và img-2, xóa các ảnh khác, thêm new1.jpg)

Scenario 3: Chỉ thêm ảnh mới, giữ tất cả ảnh cũ
Form-Data:
- files: [new1.jpg]
- keepImageIds: [img-1, img-2, img-3]
```

### Response Success (200)
```json
{
  "code": 1000,
  "message": "Cập nhật hình ảnh sản phẩm thành công!",
  "result": {
    "id": "prod-abc123",
    "name": "Áo thun basic Premium",
    "images": [
      {
        "id": "img-1",
        "imageUrl": "uuid_file1.jpg"
      },
      {
        "id": "img-2",
        "imageUrl": "uuid_file2.jpg"
      },
      {
        "id": "img-7",
        "imageUrl": "uuid_new1.jpg"
      }
    ],
    "sizes": [
      {
        "id": "size-5",
        "sizeName": "M",
        "quantity": 50
      },
      {
        "id": "size-6",
        "sizeName": "L",
        "quantity": 50
      }
    ],
    ...
  }
}
```

---

## 4. Các API khác (không thay đổi)

### GET /api/v1/products
Lấy danh sách tất cả sản phẩm

### GET /api/v1/products/{id}
Lấy chi tiết một sản phẩm

### GET /api/v1/products/search
Tìm kiếm sản phẩm theo keyword

### GET /api/v1/products/advanced-search
Tìm kiếm nâng cao với filters và sorting

### DELETE /api/v1/products/{id}
Xóa sản phẩm

---

## Validation Rules

### ProductRequest
- `name`: Không được để trống
- `price`: Không được để trống, phải ≥ 0
- `description`: Không được để trống
- `categoryId`: Không được để trống
- `stockQuantity`: Nếu có, phải ≥ 0 (mặc định: 0)

### ProductSizeRequest
- `sizeName`: Không được để trống
- `quantity`: Không được để trống, phải ≥ 0

### File Upload
- File size: Tối đa 10MB
- File type: Chỉ chấp nhận image/* (jpg, png, webp, etc.)

---

## Error Responses

### 400 Bad Request - Validation Error
```json
{
  "code": 1003,
  "message": "Validation failed",
  "errors": {
    "name": "Tên sản phẩm không được để trống",
    "price": "Giá sản phẩm phải lớn hơn hoặc bằng 0",
    "sizes[0].quantity": "Số lượng phải lớn hơn hoặc bằng 0"
  }
}
```

### 404 Not Found
```json
{
  "code": 1005,
  "message": "Product not found"
}
```

### 401 Unauthorized
```json
{
  "code": 1004,
  "message": "Unauthenticated"
}
```

### 403 Forbidden
```json
{
  "code": 1007,
  "message": "You do not have permission"
}
```

---

## Testing với Postman

### Tạo sản phẩm với nhiều sizes:

1. Chọn method: `POST`
2. URL: `http://localhost:8081/api/v1/products`
3. Headers: 
   - Authorization: Bearer YOUR_TOKEN
4. Body → form-data:
   ```
   name: Áo thun
   price: 199000
   salePrice: 149000
   description: Mô tả sản phẩm
   categoryId: {your-category-id}
   images: [chọn file]
   sizes[0].sizeName: S
   sizes[0].quantity: 10
   sizes[1].sizeName: M
   sizes[1].quantity: 20
   sizes[2].sizeName: L
   sizes[2].quantity: 15
   ```

---

## Best Practices

1. **Luôn gửi đầy đủ sizes khi update**: Vì sizes sẽ bị replace hoàn toàn
2. **Kiểm tra response**: Response luôn trả về đầy đủ images và sizes
3. **Xử lý ảnh cẩn thận**: Sử dụng `keepImageIds` để tránh mất ảnh quan trọng
4. **Validate trước khi gửi**: Đảm bảo giá trị quantity của mỗi size là hợp lệ

---

## Changelog

### Version 2.0 (2026-01-04)
- ✅ Fix API `PUT /products/{id}` để trả về đầy đủ sizes và images
- ✅ Fix API `POST /products/update-images/{id}` để trả về đầy đủ sizes
- ✅ Thêm method `findByProductIdAndIdIn` vào ProductImageRepository
- ✅ Cải thiện logic xử lý sizes khi update sản phẩm
- ✅ Fix lỗi UnsupportedOperationException khi sort theo giá

---

**Lưu ý**: Tất cả API thêm/sửa/xóa sản phẩm yêu cầu role ADMIN. Các API đọc dữ liệu không yêu cầu authentication (có thể thay đổi tùy theo yêu cầu nghiệp vụ).

