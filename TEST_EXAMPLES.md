# Test Examples - Ví dụ test API Product với Postman

## 1. Test CREATE Product với Sizes

### Request
```
Method: POST
URL: http://localhost:8081/api/v1/products
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  Content-Type: multipart/form-data

Body (form-data):
  name: Áo thun nam cổ tròn Premium
  price: 299000
  salePrice: 199000
  description: Áo thun nam chất liệu cotton 100%, thoáng mát, co giãn tốt. Phù hợp mặc hàng ngày.
  stockQuantity: 100
  categoryId: {your-category-id}
  images: [chọn 2-3 file ảnh]
  
  sizes[0].sizeName: S
  sizes[0].quantity: 20
  
  sizes[1].sizeName: M
  sizes[1].quantity: 30
  
  sizes[2].sizeName: L
  sizes[2].quantity: 30
  
  sizes[3].sizeName: XL
  sizes[3].quantity: 20
```

### Expected Response
```json
{
  "code": 1000,
  "message": "Tạo sản phẩm thành công!",
  "result": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Áo thun nam cổ tròn Premium",
    "price": 299000,
    "salePrice": 199000,
    "description": "Áo thun nam chất liệu cotton 100%, thoáng mát, co giãn tốt. Phù hợp mặc hàng ngày.",
    "stockQuantity": 100,
    "categoryId": "category-id-here",
    "categoryName": "Áo thun",
    "images": [
      {
        "id": "img-uuid-1",
        "imageUrl": "uuid_filename1.jpg"
      },
      {
        "id": "img-uuid-2",
        "imageUrl": "uuid_filename2.jpg"
      }
    ],
    "sizes": [
      {
        "id": "size-uuid-1",
        "sizeName": "S",
        "quantity": 20
      },
      {
        "id": "size-uuid-2",
        "sizeName": "M",
        "quantity": 30
      },
      {
        "id": "size-uuid-3",
        "sizeName": "L",
        "quantity": 30
      },
      {
        "id": "size-uuid-4",
        "sizeName": "XL",
        "quantity": 20
      }
    ],
    "createdAt": "2026-01-04T12:30:00",
    "updatedAt": "2026-01-04T12:30:00"
  }
}
```

---

## 2. Test UPDATE Product - Chỉ cập nhật thông tin cơ bản (GIỮ NGUYÊN SIZES)

### Request
```
Method: PUT
URL: http://localhost:8081/api/v1/products/{product-id}
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  Content-Type: multipart/form-data

Body (form-data):
  name: Áo thun nam cổ tròn Premium V2
  price: 349000
  salePrice: 249000
  description: Áo thun nam chất liệu cotton 100%, thoáng mát, co giãn tốt. Phiên bản nâng cấp.
  
  (KHÔNG gửi sizes → giữ nguyên sizes cũ)
```

### Expected Response
```json
{
  "code": 1000,
  "message": "Cập nhật sản phẩm thành công!",
  "result": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Áo thun nam cổ tròn Premium V2",
    "price": 349000,
    "salePrice": 249000,
    "description": "Áo thun nam chất liệu cotton 100%, thoáng mát, co giãn tốt. Phiên bản nâng cấp.",
    "images": [
      {
        "id": "img-uuid-1",
        "imageUrl": "uuid_filename1.jpg"
      },
      {
        "id": "img-uuid-2",
        "imageUrl": "uuid_filename2.jpg"
      }
    ],
    "sizes": [
      {
        "id": "size-uuid-1",
        "sizeName": "S",
        "quantity": 20
      },
      {
        "id": "size-uuid-2",
        "sizeName": "M",
        "quantity": 30
      },
      {
        "id": "size-uuid-3",
        "sizeName": "L",
        "quantity": 30
      },
      {
        "id": "size-uuid-4",
        "sizeName": "XL",
        "quantity": 20
      }
    ]
  }
}
```

**✅ Verify:** Sizes vẫn giữ nguyên như lúc create (4 sizes: S, M, L, XL)

---

## 3. Test UPDATE Product - Cập nhật cả SIZES (REPLACE toàn bộ)

### Request
```
Method: PUT
URL: http://localhost:8081/api/v1/products/{product-id}
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  Content-Type: multipart/form-data

Body (form-data):
  sizes[0].sizeName: M
  sizes[0].quantity: 50
  
  sizes[1].sizeName: L
  sizes[1].quantity: 50
  
  (Chỉ gửi 2 sizes → sizes cũ sẽ bị xóa và thay bằng 2 sizes mới)
```

### Expected Response
```json
{
  "code": 1000,
  "message": "Cập nhật sản phẩm thành công!",
  "result": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Áo thun nam cổ tròn Premium V2",
    "images": [...],
    "sizes": [
      {
        "id": "size-uuid-5",
        "sizeName": "M",
        "quantity": 50
      },
      {
        "id": "size-uuid-6",
        "sizeName": "L",
        "quantity": 50
      }
    ]
  }
}
```

**✅ Verify:** 
- Chỉ còn 2 sizes: M và L
- Sizes cũ (S, M, L, XL) đã bị xóa hoàn toàn
- IDs của sizes mới khác với IDs cũ

---

## 4. Test UPDATE IMAGES - Thay thế hoàn toàn

### Request
```
Method: POST
URL: http://localhost:8081/api/v1/products/update-images/{product-id}
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  Content-Type: multipart/form-data

Body (form-data):
  files: [chọn 3 file ảnh mới: new1.jpg, new2.jpg, new3.jpg]
  
  (KHÔNG gửi keepImageIds → xóa hết ảnh cũ)
```

### Expected Response
```json
{
  "code": 1000,
  "message": "Cập nhật hình ảnh sản phẩm thành công!",
  "result": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "images": [
      {
        "id": "img-uuid-7",
        "imageUrl": "uuid_new1.jpg"
      },
      {
        "id": "img-uuid-8",
        "imageUrl": "uuid_new2.jpg"
      },
      {
        "id": "img-uuid-9",
        "imageUrl": "uuid_new3.jpg"
      }
    ],
    "sizes": [
      {
        "id": "size-uuid-5",
        "sizeName": "M",
        "quantity": 50
      },
      {
        "id": "size-uuid-6",
        "sizeName": "L",
        "quantity": 50
      }
    ]
  }
}
```

**✅ Verify:** 
- Tất cả ảnh cũ bị xóa
- 3 ảnh mới được thêm vào
- **QUAN TRỌNG:** Response có trả về `sizes` ✅

---

## 5. Test UPDATE IMAGES - Giữ một số ảnh cũ + Thêm ảnh mới

### Request
```
Method: POST
URL: http://localhost:8081/api/v1/products/update-images/{product-id}
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  Content-Type: multipart/form-data

Body (form-data):
  files: [chọn 1 file ảnh mới: extra.jpg]
  keepImageIds: img-uuid-7
  keepImageIds: img-uuid-8
  
  (Giữ 2 ảnh: img-uuid-7 và img-uuid-8, xóa ảnh img-uuid-9, thêm extra.jpg)
```

### Expected Response
```json
{
  "code": 1000,
  "message": "Cập nhật hình ảnh sản phẩm thành công!",
  "result": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "images": [
      {
        "id": "img-uuid-10",
        "imageUrl": "uuid_extra.jpg"
      },
      {
        "id": "img-uuid-7",
        "imageUrl": "uuid_new1.jpg"
      },
      {
        "id": "img-uuid-8",
        "imageUrl": "uuid_new2.jpg"
      }
    ],
    "sizes": [
      {
        "id": "size-uuid-5",
        "sizeName": "M",
        "quantity": 50
      },
      {
        "id": "size-uuid-6",
        "sizeName": "L",
        "quantity": 50
      }
    ]
  }
}
```

**✅ Verify:** 
- 2 ảnh cũ được giữ lại (img-uuid-7, img-uuid-8)
- 1 ảnh cũ bị xóa (img-uuid-9)
- 1 ảnh mới được thêm (extra.jpg)
- Response có trả về `sizes` ✅

---

## 6. Test GET Product By ID

### Request
```
Method: GET
URL: http://localhost:8081/api/v1/products/{product-id}
```

### Expected Response
```json
{
  "code": 1000,
  "result": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Áo thun nam cổ tròn Premium V2",
    "price": 349000,
    "salePrice": 249000,
    "description": "...",
    "images": [
      {
        "id": "img-uuid-10",
        "imageUrl": "uuid_extra.jpg"
      },
      {
        "id": "img-uuid-7",
        "imageUrl": "uuid_new1.jpg"
      },
      {
        "id": "img-uuid-8",
        "imageUrl": "uuid_new2.jpg"
      }
    ],
    "sizes": [
      {
        "id": "size-uuid-5",
        "sizeName": "M",
        "quantity": 50
      },
      {
        "id": "size-uuid-6",
        "sizeName": "L",
        "quantity": 50
      }
    ],
    "createdAt": "2026-01-04T12:30:00",
    "updatedAt": "2026-01-04T13:45:00"
  }
}
```

**✅ Verify:** Dữ liệu khớp với test case trước

---

## 7. Test ADVANCED SEARCH với sortBy=price_asc

### Request
```
Method: GET
URL: http://localhost:8081/api/v1/products/advanced-search?sortBy=price_asc&pageNo=1&pageSize=10
```

### Expected Response
```json
{
  "code": 1000,
  "result": {
    "content": [
      {
        "id": "prod-1",
        "name": "Product A",
        "price": 99000,
        "salePrice": 79000,
        "images": [...],
        "sizes": [...]
      },
      {
        "id": "prod-2",
        "name": "Product B",
        "price": 199000,
        "salePrice": null,
        "images": [...],
        "sizes": [...]
      }
    ],
    "pageNo": 1,
    "pageSize": 10,
    "totalElements": 50,
    "totalPages": 5
  }
}
```

**✅ Verify:** 
- Không có lỗi `UnsupportedOperationException`
- Sản phẩm được sắp xếp theo giá tăng dần
- Response có đầy đủ images và sizes

---

## Test Checklist

### API CREATE
- [ ] Tạo product với 1 size
- [ ] Tạo product với nhiều sizes (S, M, L, XL)
- [ ] Tạo product với images và sizes
- [ ] Response có đầy đủ images và sizes

### API UPDATE
- [ ] Update không gửi sizes → giữ nguyên sizes cũ
- [ ] Update có gửi sizes → replace toàn bộ sizes
- [ ] Response có đầy đủ images và sizes

### API UPDATE IMAGES
- [ ] Update images không gửi keepImageIds → xóa hết ảnh cũ
- [ ] Update images có gửi keepImageIds → giữ một số ảnh cũ
- [ ] Response có đầy đủ images và sizes ⭐

### API GET
- [ ] Get by ID trả về đầy đủ thông tin
- [ ] Advanced search với sortBy=price_asc không lỗi
- [ ] Search trả về đầy đủ sizes và images

---

## Lưu ý khi test

1. **Postman Collection Variables:**
   ```json
   {
     "base_url": "http://localhost:8081/api/v1",
     "token": "Bearer your-jwt-token",
     "product_id": "550e8400-e29b-41d4-a716-446655440000",
     "category_id": "your-category-id"
   }
   ```

2. **Form-data trong Postman:**
   - Chọn Body → form-data
   - Với files: chọn type là "File"
   - Với sizes: key là `sizes[0].sizeName`, `sizes[0].quantity`, etc.
   - Với keepImageIds: thêm nhiều row cùng key `keepImageIds`

3. **Authorization:**
   - Sử dụng token của user có role ADMIN
   - Set trong Headers: `Authorization: Bearer {token}`

---

**Happy Testing! 🚀**

