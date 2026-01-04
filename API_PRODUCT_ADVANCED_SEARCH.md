# API Product Advanced Search Documentation

## Tổng quan
API tìm kiếm nâng cao cho phép lọc, sắp xếp và phân trang sản phẩm theo nhiều tiêu chí khác nhau.

---

## Endpoint

```
GET /api/v1/products/advanced-search
```

### Authentication
❌ **Không yêu cầu** - API public, không cần token

---

## Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| keyword | String | ❌ No | null | Tìm kiếm theo tên hoặc mô tả sản phẩm |
| categoryId | String | ❌ No | null | Lọc theo danh mục sản phẩm |
| minPrice | BigDecimal | ❌ No | null | Giá tối thiểu (tính theo giá sale nếu có) |
| maxPrice | BigDecimal | ❌ No | null | Giá tối đa (tính theo giá sale nếu có) |
| sortBy | String | ❌ No | newest | Sắp xếp theo: `price_asc`, `price_desc`, `newest` |
| pageNo | Integer | ❌ No | 1 | Số trang (bắt đầu từ 1) |
| pageSize | Integer | ❌ No | 10 | Số sản phẩm mỗi trang |

---

## Sort Options

| Value | Description |
|-------|-------------|
| `price_asc` | Giá tăng dần (rẻ → đắt) |
| `price_desc` | Giá giảm dần (đắt → rẻ) |
| `newest` | Mới nhất (theo ngày tạo) |

**Lưu ý về giá:**
- Nếu sản phẩm có `salePrice` → sort theo `salePrice`
- Nếu không có `salePrice` → sort theo `price`

---

## Request Examples

### Example 1: Tìm kiếm cơ bản
```http
GET /api/v1/products/advanced-search?keyword=áo thun
```

### Example 2: Lọc theo danh mục
```http
GET /api/v1/products/advanced-search?categoryId=cat-123
```

### Example 3: Lọc theo khoảng giá
```http
GET /api/v1/products/advanced-search?minPrice=100000&maxPrice=500000
```

### Example 4: Tìm kiếm + lọc + sắp xếp
```http
GET /api/v1/products/advanced-search?keyword=áo&categoryId=cat-123&minPrice=100000&maxPrice=300000&sortBy=price_asc&pageNo=1&pageSize=20
```

### Example 5: Sắp xếp theo giá cao nhất
```http
GET /api/v1/products/advanced-search?sortBy=price_desc
```

### Example 6: Lấy tất cả sản phẩm (không phân trang)
```http
GET /api/v1/products/advanced-search?pageSize=0
```
hoặc không truyền `pageSize`

---

## Response Format

### Success Response (200 OK)

```json
{
  "code": 1000,
  "message": "Tìm kiếm sản phẩm thành công!",
  "result": {
    "content": [
      {
        "id": "prod-001",
        "name": "Áo thun cotton basic",
        "price": 199000,
        "salePrice": 149000,
        "description": "Áo thun cotton 100% thoáng mát, co giãn tốt",
        "stockQuantity": 100,
        "category": {
          "id": "cat-123",
          "name": "Áo thun"
        },
        "images": [
          {
            "id": "img-001",
            "imageUrl": "uuid_image1.jpg"
          },
          {
            "id": "img-002",
            "imageUrl": "uuid_image2.jpg"
          }
        ],
        "sizes": [
          {
            "id": "size-001",
            "sizeName": "S",
            "quantity": 20
          },
          {
            "id": "size-002",
            "sizeName": "M",
            "quantity": 30
          },
          {
            "id": "size-003",
            "sizeName": "L",
            "quantity": 30
          },
          {
            "id": "size-004",
            "sizeName": "XL",
            "quantity": 20
          }
        ],
        "createdAt": "2026-01-01T10:00:00",
        "updatedAt": "2026-01-04T15:30:00"
      },
      {
        "id": "prod-002",
        "name": "Áo thun polo",
        "price": 299000,
        "salePrice": null,
        "description": "Áo polo cao cấp, form dáng hiện đại",
        "stockQuantity": 80,
        "category": {
          "id": "cat-123",
          "name": "Áo thun"
        },
        "images": [
          {
            "id": "img-003",
            "imageUrl": "uuid_image3.jpg"
          }
        ],
        "sizes": [
          {
            "id": "size-005",
            "sizeName": "M",
            "quantity": 40
          },
          {
            "id": "size-006",
            "sizeName": "L",
            "quantity": 40
          }
        ],
        "createdAt": "2026-01-02T14:20:00",
        "updatedAt": "2026-01-02T14:20:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 25,
    "totalPages": 2,
    "last": false,
    "size": 20,
    "number": 0,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "numberOfElements": 20,
    "first": true,
    "empty": false
  }
}
```

### Response khi không có kết quả

```json
{
  "code": 1000,
  "message": "Tìm kiếm sản phẩm thành công!",
  "result": {
    "content": [],
    "totalElements": 0,
    "totalPages": 0,
    "size": 20,
    "number": 0,
    "first": true,
    "last": true,
    "empty": true
  }
}
```

---

## Response Fields Explained

### Pagination Fields

| Field | Type | Description |
|-------|------|-------------|
| `content` | Array | Danh sách sản phẩm trong trang hiện tại |
| `totalElements` | Integer | Tổng số sản phẩm tìm được |
| `totalPages` | Integer | Tổng số trang |
| `size` | Integer | Số sản phẩm mỗi trang (pageSize) |
| `number` | Integer | Số trang hiện tại (bắt đầu từ 0) |
| `first` | Boolean | Có phải trang đầu tiên? |
| `last` | Boolean | Có phải trang cuối cùng? |
| `empty` | Boolean | Danh sách có rỗng không? |

### Product Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | String | ID sản phẩm |
| `name` | String | Tên sản phẩm |
| `price` | BigDecimal | Giá gốc |
| `salePrice` | BigDecimal | Giá khuyến mãi (null nếu không có) |
| `description` | String | Mô tả sản phẩm |
| `stockQuantity` | Integer | Tổng số lượng tồn kho |
| `category` | Object | Thông tin danh mục |
| `images` | Array | Danh sách ảnh sản phẩm |
| `sizes` | Array | Danh sách size và số lượng từng size |
| `createdAt` | DateTime | Thời gian tạo |
| `updatedAt` | DateTime | Thời gian cập nhật gần nhất |

---

## Search Logic

### 1. Keyword Search
Tìm kiếm trong:
- Tên sản phẩm (`name`)
- Mô tả sản phẩm (`description`)

**Case-insensitive** và sử dụng **LIKE %keyword%**

**Example:**
```
keyword = "áo"
→ Tìm: "Áo thun", "Áo polo", "Quần áo thể thao"
```

### 2. Category Filter
Lọc chính xác theo `categoryId`

**Example:**
```
categoryId = "cat-123"
→ Chỉ lấy sản phẩm có category.id = "cat-123"
```

### 3. Price Range Filter
So sánh với **effective price** (giá hiệu dụng):
- Nếu có `salePrice` → dùng `salePrice`
- Nếu không có `salePrice` → dùng `price`

**Example:**
```
minPrice = 100000
maxPrice = 300000
→ Lấy sản phẩm có giá hiệu dụng từ 100k đến 300k
```

### 4. Sorting
Sắp xếp được xử lý trong **memory** (không dùng database sort) để tránh lỗi với null values.

**Logic:**
```java
// Sort theo effective price
effectivePrice = salePrice != null ? salePrice : price

if (sortBy == "price_asc") {
  sort ASC by effectivePrice
} else if (sortBy == "price_desc") {
  sort DESC by effectivePrice
} else {
  sort DESC by createdAt (newest first)
}
```

---

## Use Cases

### Use Case 1: Trang chủ - Hiển thị sản phẩm mới nhất
```http
GET /api/v1/products/advanced-search?sortBy=newest&pageSize=12
```

### Use Case 2: Trang danh mục - Lọc theo category
```http
GET /api/v1/products/advanced-search?categoryId=cat-123&pageNo=1&pageSize=20
```

### Use Case 3: Tìm kiếm sản phẩm
```http
GET /api/v1/products/advanced-search?keyword=áo%20thun
```

### Use Case 4: Lọc theo khoảng giá
```http
GET /api/v1/products/advanced-search?minPrice=200000&maxPrice=500000&sortBy=price_asc
```

### Use Case 5: Trang "Sale" - Chỉ sản phẩm giảm giá
```http
GET /api/v1/products/advanced-search?maxPrice=200000&sortBy=price_asc
```
(Frontend filter thêm: chỉ hiển thị sản phẩm có `salePrice != null`)

### Use Case 6: Load More / Infinite Scroll
```javascript
// Page 1
GET /api/v1/products/advanced-search?pageNo=1&pageSize=10

// Page 2 (khi scroll xuống)
GET /api/v1/products/advanced-search?pageNo=2&pageSize=10

// Page 3
GET /api/v1/products/advanced-search?pageNo=3&pageSize=10
```

---

## Frontend Integration Example

### React/Vue/Angular Example

```javascript
async function searchProducts(filters) {
  const params = new URLSearchParams();
  
  if (filters.keyword) params.append('keyword', filters.keyword);
  if (filters.categoryId) params.append('categoryId', filters.categoryId);
  if (filters.minPrice) params.append('minPrice', filters.minPrice);
  if (filters.maxPrice) params.append('maxPrice', filters.maxPrice);
  if (filters.sortBy) params.append('sortBy', filters.sortBy);
  if (filters.pageNo) params.append('pageNo', filters.pageNo);
  if (filters.pageSize) params.append('pageSize', filters.pageSize);
  
  const response = await fetch(
    `http://localhost:8081/api/v1/products/advanced-search?${params}`
  );
  
  const data = await response.json();
  return data.result;
}

// Usage
const result = await searchProducts({
  keyword: 'áo thun',
  categoryId: 'cat-123',
  minPrice: 100000,
  maxPrice: 500000,
  sortBy: 'price_asc',
  pageNo: 1,
  pageSize: 20
});

console.log('Total products:', result.totalElements);
console.log('Products:', result.content);
```

---

## Performance Notes

### Optimization đã áp dụng:

1. **Eager Fetching**: Category được fetch cùng lúc với Product để tránh N+1 queries
2. **Batch Loading**: Images và Sizes được load theo batch (không phải từng product một)
3. **In-Memory Sorting**: Sort theo giá được xử lý trong memory để tránh lỗi database
4. **Distinct Results**: Sử dụng `distinct()` để tránh duplicate khi join

### Query Performance:

```sql
-- Main query (simplified)
SELECT DISTINCT p.* 
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
WHERE 
  (p.name LIKE '%keyword%' OR p.description LIKE '%keyword%')
  AND p.category_id = 'categoryId'
  AND COALESCE(p.sale_price, p.price) >= minPrice
  AND COALESCE(p.sale_price, p.price) <= maxPrice
ORDER BY p.created_at DESC
LIMIT pageSize OFFSET (pageNo - 1) * pageSize;

-- Then batch load images and sizes
SELECT * FROM product_images WHERE product_id IN (...)
SELECT * FROM product_sizes WHERE product_id IN (...)
```

---

## Error Handling

### 400 Bad Request - Invalid Parameters
```json
{
  "code": 1001,
  "message": "Invalid request parameters",
  "errors": {
    "minPrice": "Giá tối thiểu phải là số dương",
    "pageNo": "Số trang phải lớn hơn 0"
  }
}
```

### 500 Internal Server Error
```json
{
  "code": 9999,
  "message": "Internal server error"
}
```

---

## Testing với Postman/Thunder Client

### Test Case 1: Tìm kiếm cơ bản
```
GET http://localhost:8081/api/v1/products/advanced-search?keyword=áo
```

### Test Case 2: Lọc + sắp xếp
```
GET http://localhost:8081/api/v1/products/advanced-search
  ?categoryId=cat-123
  &minPrice=100000
  &maxPrice=300000
  &sortBy=price_asc
  &pageNo=1
  &pageSize=10
```

### Test Case 3: Pagination
```
// Page 1
GET http://localhost:8081/api/v1/products/advanced-search?pageNo=1&pageSize=5

// Page 2
GET http://localhost:8081/api/v1/products/advanced-search?pageNo=2&pageSize=5
```

---

## Best Practices

1. **Pagination**: Luôn sử dụng pagination cho performance tốt hơn
2. **Cache Results**: Frontend nên cache kết quả tìm kiếm để giảm số request
3. **Debounce Search**: Đợi user ngừng gõ 300-500ms trước khi search
4. **Loading State**: Hiển thị loading indicator khi đang search
5. **Empty State**: Hiển thị UI thân thiện khi không có kết quả
6. **URL Params**: Lưu filter vào URL để user có thể share link

---

## Changelog

### Version 2.0 (2026-01-04)
- ✅ Fix lỗi `UnsupportedOperationException` khi sort theo giá
- ✅ Cải thiện performance với batch loading
- ✅ Thêm hỗ trợ filter theo khoảng giá
- ✅ Sort theo effective price (salePrice nếu có)
- ✅ Trả về đầy đủ images và sizes cho mỗi sản phẩm

---

**Note**: API này được tối ưu cho việc hiển thị danh sách sản phẩm trên frontend. Tất cả các filter có thể kết hợp với nhau để tạo ra các trải nghiệm tìm kiếm phong phú.

