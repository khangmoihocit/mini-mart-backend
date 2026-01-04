# Tóm tắt các thay đổi: Hỗ trợ Size cho Giỏ hàng và Sửa lỗi Sorting

## Ngày: 2026-01-04

## Vấn đề đã giải quyết

### 1. Lỗi Sorting khi tìm kiếm sản phẩm (price_asc/price_desc)
**Lỗi gốc:**
```
java.lang.UnsupportedOperationException: Applying Null Precedence using Criteria Queries is not yet supported.
```

**Nguyên nhân:**
- Spring Data JPA không hỗ trợ `nullsLast()` khi sử dụng Criteria Queries với Specification
- Xảy ra khi sort theo giá sản phẩm có thể có giá trị null (salePrice)

**Giải pháp:**
- Cập nhật `ProductServiceImpl.advancedSearch()` để xử lý sort trong memory thay vì dùng database sort khi gặp vấn đề
- Fetch tất cả records phù hợp điều kiện, sort trong Java, sau đó phân trang thủ công

### 2. Hỗ trợ Size sản phẩm trong Giỏ hàng
**Yêu cầu:**
- Cho phép người dùng chọn size khi thêm sản phẩm vào giỏ
- Kiểm tra số lượng tồn kho từ size cụ thể (thay vì chỉ từ product)
- Hiển thị thông tin size trong giỏ hàng

---

## Chi tiết các thay đổi

### 1. Entity Layer

#### `CartDetail.java`
**Thêm mới:**
```java
@ManyToOne
@JoinColumn(name = "product_size_id")
@OnDelete(action = OnDeleteAction.CASCADE)
ProductSize productSize;
```
- Thêm trường `productSize` để lưu size được chọn (nullable - sản phẩm có thể không có size)

---

### 2. DTO Layer

#### `AddToCartRequest.java`
**Thêm mới:**
```java
String productSizeId; // Optional: nếu sản phẩm có size
```
- Thêm trường để nhận productSizeId khi thêm vào giỏ (optional)

#### `CartItemResponse.java`
**Thêm mới:**
```java
String productSizeId;
String sizeName;
Integer availableQuantity; // Số lượng còn lại của size này
```
- Hiển thị thông tin size trong response
- `availableQuantity`: số lượng tồn kho của size (hoặc product nếu không có size)

---

### 3. Repository Layer

#### `CartDetailRepository.java`
**Thay đổi:**
1. Cập nhật query `findByCartId` để fetch ProductSize:
```java
@Query("SELECT cd FROM CartDetail cd JOIN FETCH cd.product p LEFT JOIN FETCH p.category LEFT JOIN FETCH cd.productSize WHERE cd.cart.id = :cartId")
```

2. Thay thế `findByCartIdAndProductId` bằng `findByCartIdAndProductIdAndSize`:
```java
@Query("SELECT cd FROM CartDetail cd WHERE cd.cart.id = :cartId AND cd.product.id = :productId AND " +
       "((:productSizeId IS NULL AND cd.productSize IS NULL) OR cd.productSize.id = :productSizeId)")
Optional<CartDetail> findByCartIdAndProductIdAndSize(
    @Param("cartId") String cartId, 
    @Param("productId") String productId,
    @Param("productSizeId") String productSizeId);
```
- Xử lý cả trường hợp có size và không có size

---

### 4. Service Layer

#### `CartServiceImpl.java`

**Thêm dependency:**
```java
ProductSizeRepository productSizeRepository;
```

**Cập nhật `addToCart()`:**
1. Kiểm tra và validate ProductSize nếu được cung cấp
2. Lấy số lượng tồn kho từ size (nếu có) hoặc product
3. Kiểm tra size có thuộc product không
4. Lưu ProductSize vào CartDetail

**Logic:**
```java
if (request.getProductSizeId() != null && !request.getProductSizeId().isEmpty()) {
    // Lấy thông tin size
    productSize = productSizeRepository.findById(request.getProductSizeId())
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_SIZE_NOT_FOUND));
    
    // Validate size thuộc product
    if (!productSize.getProduct().getId().equals(product.getId())) {
        throw new AppException(ErrorCode.INVALID_PRODUCT_SIZE);
    }
    
    availableQuantity = productSize.getQuantity();
} else {
    availableQuantity = product.getStockQuantity();
}
```

**Cập nhật `updateCartItem()`:**
- Kiểm tra số lượng từ size nếu cart item có size
- Validate với số lượng tồn kho của size

**Cập nhật `buildCartResponse()`:**
- Thêm thông tin size vào CartItemResponse
- Tính `availableQuantity` từ size hoặc product
```java
Integer availableQuantity = productSize != null 
    ? productSize.getQuantity() 
    : product.getStockQuantity();

CartItemResponse itemResponse = CartItemResponse.builder()
    // ... existing fields ...
    .productSizeId(productSize != null ? productSize.getId() : null)
    .sizeName(productSize != null ? productSize.getSizeName() : null)
    .availableQuantity(availableQuantity)
    .build();
```

---

### 5. Error Codes

#### `ErrorCode.java`
**Thêm mới:**
```java
PRODUCT_SIZE_NOT_FOUND(3004, "Không tìm thấy size sản phẩm.", HttpStatus.NOT_FOUND),
INVALID_PRODUCT_SIZE(3005, "Size không thuộc sản phẩm này.", HttpStatus.BAD_REQUEST),
```

---

### 6. ProductServiceImpl - Fix Sorting Issue

#### `advancedSearch()`
**Thay đổi:**
- Đã có sẵn logic xử lý sort theo giá trong memory (đúng)
- Cập nhật phần sort không phải giá để tránh lỗi với pageSize lớn:

```java
if (searchRequest.getPageSize() == null || searchRequest.getPageSize() <= 0) {
    // Fetch all và sort trong memory
    pageable = PageRequest.of(0, Integer.MAX_VALUE);
    List<Product> allProducts = productRepository.findAll(spec);
    allProducts.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
    products = new PageImpl<>(allProducts, pageable, allProducts.size());
}
```

---

## API Usage Examples

### 1. Thêm sản phẩm VÀO giỏ hàng (không có size)
```json
POST /api/v1/cart/add
{
  "productId": "product-uuid",
  "quantity": 2
}
```

### 2. Thêm sản phẩm VÀO giỏ hàng (có size)
```json
POST /api/v1/cart/add
{
  "productId": "product-uuid",
  "productSizeId": "size-uuid",
  "quantity": 2
}
```

### 3. Response của giỏ hàng
```json
{
  "code": 1000,
  "message": "Lấy giỏ hàng thành công!",
  "result": {
    "cartId": "cart-uuid",
    "userId": "user-uuid",
    "items": [
      {
        "id": "cart-detail-uuid",
        "productId": "product-uuid",
        "productName": "Áo thun",
        "price": 200000,
        "salePrice": 150000,
        "imageUrl": "https://...",
        "quantity": 2,
        "subtotal": 300000,
        "productSizeId": "size-uuid",
        "sizeName": "L",
        "availableQuantity": 50
      }
    ],
    "totalItems": 1,
    "totalQuantity": 2,
    "totalAmount": 300000
  }
}
```

---

## Database Migration Required

### Thêm cột vào bảng `cart_details`
```sql
ALTER TABLE cart_details 
ADD COLUMN product_size_id VARCHAR(36) NULL,
ADD CONSTRAINT fk_cart_detail_product_size 
    FOREIGN KEY (product_size_id) 
    REFERENCES product_sizes(id) 
    ON DELETE CASCADE;
```

---

## Testing Checklist

### Cart với Size
- [x] Thêm sản phẩm không có size vào giỏ
- [x] Thêm sản phẩm có size vào giỏ
- [x] Thêm cùng sản phẩm nhưng khác size (phải tạo 2 cart items riêng)
- [x] Cập nhật số lượng cart item có size
- [x] Kiểm tra số lượng tồn kho từ size
- [x] Validate size có thuộc product không
- [x] Xóa cart item có size

### Product Sorting
- [x] Sort theo price_asc
- [x] Sort theo price_desc
- [x] Sort theo newest
- [x] Không có sort (default)
- [x] Pagination với sort

---

## Notes

1. **Backward Compatible:** Code vẫn hoạt động với sản phẩm không có size (productSizeId = null)
2. **Validation:** Size phải thuộc về product được chọn
3. **Stock Check:** Luôn kiểm tra từ size nếu có, nếu không thì từ product
4. **UI Consideration:** Frontend nên:
   - Hiển thị dropdown chọn size nếu product có sizes
   - Hiển thị size name và available quantity trong giỏ hàng
   - Disable nút "Thêm vào giỏ" nếu size đã hết hàng

---

## Các file đã thay đổi

1. `src/main/java/com/khangmoihocit/minimart/entity/CartDetail.java`
2. `src/main/java/com/khangmoihocit/minimart/dto/request/AddToCartRequest.java`
3. `src/main/java/com/khangmoihocit/minimart/dto/response/CartItemResponse.java`
4. `src/main/java/com/khangmoihocit/minimart/repository/CartDetailRepository.java`
5. `src/main/java/com/khangmoihocit/minimart/service/impl/CartServiceImpl.java`
6. `src/main/java/com/khangmoihocit/minimart/enums/ErrorCode.java`
7. `src/main/java/com/khangmoihocit/minimart/service/impl/ProductServiceImpl.java`

---

## Compilation Status

✅ **BUILD SUCCESS** - Tất cả các file đã compile thành công không có lỗi.

