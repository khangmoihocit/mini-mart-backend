# API Cart Documentation

## API Giỏ Hàng (Shopping Cart)

API giỏ hàng cho phép người dùng quản lý các sản phẩm trong giỏ hàng của họ. Tất cả các API đều yêu cầu authentication.

---

## Endpoints

### 1. Lấy giỏ hàng hiện tại
**Endpoint:** `GET /api/v1/cart`

**Mô tả:** Lấy thông tin giỏ hàng của user hiện tại, bao gồm danh sách sản phẩm và tổng tiền.

**Headers:**
```
Authorization: Bearer {token}
```

**Response Success (200):**
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
        "productName": "Áo Thun Nam Basic",
        "price": 199000,
        "salePrice": 149000,
        "imageUrl": "https://example.com/images/product.jpg",
        "quantity": 2,
        "subtotal": 298000,
        "productSizeId": "size-uuid",
        "sizeName": "M",
        "availableQuantity": 50
      },
      {
        "id": "cart-detail-uuid-2",
        "productId": "product-uuid-2",
        "productName": "Quần Jean Nam",
        "price": 399000,
        "salePrice": null,
        "imageUrl": "https://example.com/images/product2.jpg",
        "quantity": 1,
        "subtotal": 399000,
        "productSizeId": null,
        "sizeName": null,
        "availableQuantity": 100
      }
    ],
    "totalItems": 2,
    "totalQuantity": 3,
    "totalAmount": 697000
  }
}
```

**Giải thích các trường:**
- `cartId`: ID của giỏ hàng
- `userId`: ID của user sở hữu giỏ hàng
- `items`: Danh sách sản phẩm trong giỏ
  - `id`: ID của cart item (dùng để update/delete)
  - `productId`: ID của sản phẩm
  - `productName`: Tên sản phẩm
  - `price`: Giá gốc
  - `salePrice`: Giá sale (nếu có), `null` nếu không có sale
  - `imageUrl`: URL hình ảnh đầu tiên của sản phẩm
  - `quantity`: Số lượng trong giỏ
  - `subtotal`: Tổng tiền của item này (tính theo giá sale nếu có)
  - `productSizeId`: ID của size (nếu sản phẩm có size), `null` nếu không
  - `sizeName`: Tên size (VD: "S", "M", "L")
  - `availableQuantity`: Số lượng còn lại trong kho (của size hoặc product)
- `totalItems`: Tổng số loại sản phẩm khác nhau
- `totalQuantity`: Tổng số lượng sản phẩm (cộng tất cả quantity)
- `totalAmount`: Tổng tiền của giỏ hàng

**Response khi giỏ hàng trống:**
```json
{
  "code": 1000,
  "message": "Lấy giỏ hàng thành công!",
  "result": {
    "cartId": "cart-uuid",
    "userId": "user-uuid",
    "items": [],
    "totalItems": 0,
    "totalQuantity": 0,
    "totalAmount": 0
  }
}
```

---

### 2. Thêm sản phẩm vào giỏ hàng
**Endpoint:** `POST /api/v1/cart/add`

**Mô tả:** Thêm sản phẩm vào giỏ hàng. Nếu sản phẩm đã có trong giỏ (cùng productId và productSizeId), số lượng sẽ được cộng dồn.

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "productId": "product-uuid",
  "quantity": 2,
  "productSizeId": "size-uuid"
}
```

**Request Body (sản phẩm không có size):**
```json
{
  "productId": "product-uuid",
  "quantity": 1
}
```

**Validation:**
- `productId`: Bắt buộc, không được để trống
- `quantity`: Bắt buộc, phải >= 1
- `productSizeId`: Không bắt buộc (chỉ cần khi sản phẩm có size)

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Thêm sản phẩm vào giỏ hàng thành công!",
  "result": {
    "cartId": "cart-uuid",
    "userId": "user-uuid",
    "items": [
      {
        "id": "cart-detail-uuid",
        "productId": "product-uuid",
        "productName": "Áo Thun Nam Basic",
        "price": 199000,
        "salePrice": 149000,
        "imageUrl": "https://example.com/images/product.jpg",
        "quantity": 2,
        "subtotal": 298000,
        "productSizeId": "size-uuid",
        "sizeName": "M",
        "availableQuantity": 48
      }
    ],
    "totalItems": 1,
    "totalQuantity": 2,
    "totalAmount": 298000
  }
}
```

**Error Responses:**

**Product không tồn tại (404):**
```json
{
  "code": 3001,
  "message": "Không tìm thấy sản phẩm."
}
```

**Size không tồn tại (404):**
```json
{
  "code": 3004,
  "message": "Không tìm thấy size sản phẩm."
}
```

**Size không thuộc sản phẩm (400):**
```json
{
  "code": 3005,
  "message": "Size không thuộc sản phẩm này."
}
```

**Không đủ hàng trong kho (400):**
```json
{
  "code": 4004,
  "message": "Số lượng sản phẩm trong kho không đủ."
}
```

---

### 3. Cập nhật số lượng sản phẩm trong giỏ hàng
**Endpoint:** `PUT /api/v1/cart/items/{cartDetailId}`

**Mô tả:** Cập nhật số lượng của một sản phẩm đã có trong giỏ hàng.

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Path Parameters:**
- `cartDetailId`: ID của cart item (lấy từ response của GET /api/v1/cart)

**Request Body:**
```json
{
  "quantity": 5
}
```

**Validation:**
- `quantity`: Bắt buộc, phải >= 1

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Cập nhật giỏ hàng thành công!",
  "result": {
    "cartId": "cart-uuid",
    "userId": "user-uuid",
    "items": [
      {
        "id": "cart-detail-uuid",
        "productId": "product-uuid",
        "productName": "Áo Thun Nam Basic",
        "price": 199000,
        "salePrice": 149000,
        "imageUrl": "https://example.com/images/product.jpg",
        "quantity": 5,
        "subtotal": 745000,
        "productSizeId": "size-uuid",
        "sizeName": "M",
        "availableQuantity": 45
      }
    ],
    "totalItems": 1,
    "totalQuantity": 5,
    "totalAmount": 745000
  }
}
```

**Error Responses:**

**Cart item không tồn tại (404):**
```json
{
  "code": 4002,
  "message": "Không tìm thấy sản phẩm trong giỏ hàng."
}
```

**Không có quyền (403):**
```json
{
  "code": 2102,
  "message": "Bạn không có quyền truy cập tài nguyên này."
}
```

**Không đủ hàng trong kho (400):**
```json
{
  "code": 4004,
  "message": "Số lượng sản phẩm trong kho không đủ."
}
```

---

### 4. Xóa sản phẩm khỏi giỏ hàng
**Endpoint:** `DELETE /api/v1/cart/items/{cartDetailId}`

**Mô tả:** Xóa một sản phẩm khỏi giỏ hàng.

**Headers:**
```
Authorization: Bearer {token}
```

**Path Parameters:**
- `cartDetailId`: ID của cart item

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Xóa sản phẩm khỏi giỏ hàng thành công!",
  "result": {
    "cartId": "cart-uuid",
    "userId": "user-uuid",
    "items": [
      {
        "id": "cart-detail-uuid-2",
        "productId": "product-uuid-2",
        "productName": "Quần Jean Nam",
        "price": 399000,
        "salePrice": null,
        "imageUrl": "https://example.com/images/product2.jpg",
        "quantity": 1,
        "subtotal": 399000,
        "productSizeId": null,
        "sizeName": null,
        "availableQuantity": 100
      }
    ],
    "totalItems": 1,
    "totalQuantity": 1,
    "totalAmount": 399000
  }
}
```

**Error Responses:**

**Cart item không tồn tại (404):**
```json
{
  "code": 4002,
  "message": "Không tìm thấy sản phẩm trong giỏ hàng."
}
```

**Không có quyền (403):**
```json
{
  "code": 2102,
  "message": "Bạn không có quyền truy cập tài nguyên này."
}
```

---

### 5. Xóa toàn bộ giỏ hàng
**Endpoint:** `DELETE /api/v1/cart/clear`

**Mô tả:** Xóa tất cả sản phẩm trong giỏ hàng (clear cart).

**Headers:**
```
Authorization: Bearer {token}
```

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Đã xóa toàn bộ giỏ hàng!"
}
```

---

### 6. Lấy số lượng items trong giỏ hàng
**Endpoint:** `GET /api/v1/cart/count`

**Mô tả:** Lấy tổng số loại sản phẩm khác nhau trong giỏ hàng (không tính quantity). Thường dùng để hiển thị badge trên icon giỏ hàng.

**Headers:**
```
Authorization: Bearer {token}
```

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Lấy số lượng items thành công!",
  "result": 3
}
```

**Ví dụ:**
- Nếu giỏ hàng có: 2 áo (quantity=2), 1 quần (quantity=3), 1 mũ (quantity=1)
- Thì `count` sẽ trả về `3` (3 loại sản phẩm khác nhau)
- Nhưng `totalQuantity` trong CartResponse sẽ là `6` (2+3+1)

---

## Luồng hoạt động (Flow)

### 1. Thêm sản phẩm vào giỏ hàng
```
User -> POST /api/v1/cart/add
     -> Server kiểm tra:
        - Sản phẩm có tồn tại không?
        - Size có hợp lệ không? (nếu có)
        - Có đủ hàng trong kho không?
        - Sản phẩm đã có trong giỏ chưa?
          + Nếu có: Cộng thêm quantity
          + Nếu chưa: Tạo mới cart item
     -> Trả về giỏ hàng đã cập nhật
```

### 2. Cập nhật số lượng
```
User -> PUT /api/v1/cart/items/{id}
     -> Server kiểm tra:
        - Cart item có tồn tại không?
        - Cart item có thuộc về user này không?
        - Có đủ hàng trong kho không?
     -> Cập nhật quantity
     -> Trả về giỏ hàng đã cập nhật
```

### 3. Xóa sản phẩm
```
User -> DELETE /api/v1/cart/items/{id}
     -> Server kiểm tra:
        - Cart item có tồn tại không?
        - Cart item có thuộc về user này không?
     -> Xóa cart item
     -> Trả về giỏ hàng đã cập nhật
```

### 4. Đặt hàng (checkout)
```
User -> Xem giỏ hàng: GET /api/v1/cart
     -> Điền thông tin giao hàng
     -> POST /api/v1/orders (API Order)
     -> Server tạo đơn hàng và tự động xóa giỏ hàng
```

---

## Business Rules

### 1. Quản lý giỏ hàng
- Mỗi user chỉ có 1 giỏ hàng duy nhất
- Giỏ hàng được tự động tạo khi user thêm sản phẩm lần đầu
- Giỏ hàng được tự động xóa sau khi đặt hàng thành công

### 2. Thêm sản phẩm
- Phải kiểm tra tồn kho trước khi thêm
- Nếu sản phẩm đã có trong giỏ (cùng product và size), quantity sẽ được cộng dồn
- Sản phẩm có size: phải chọn size, kiểm tra quantity của size đó
- Sản phẩm không có size: kiểm tra quantity của product

### 3. Tính giá
- Nếu sản phẩm có `salePrice`, dùng `salePrice`
- Nếu không có `salePrice`, dùng `price`
- `subtotal = (salePrice hoặc price) × quantity`
- `totalAmount = sum(subtotal của tất cả items)`

### 4. Tồn kho
- `availableQuantity` luôn được cập nhật realtime
- Không cho phép thêm/cập nhật vượt quá `availableQuantity`
- Khi đặt hàng thành công, tồn kho sẽ bị trừ đi

### 5. Security
- User chỉ có thể xem và thao tác giỏ hàng của chính mình
- Tất cả API đều yêu cầu authentication
- Cart item ID phải thuộc về giỏ hàng của user hiện tại

---

## Ví dụ sử dụng

### Ví dụ 1: Thêm sản phẩm có size vào giỏ hàng

**Request:**
```bash
POST /api/v1/cart/add
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "productId": "550e8400-e29b-41d4-a716-446655440000",
  "quantity": 2,
  "productSizeId": "660e8400-e29b-41d4-a716-446655440000"
}
```

**Response:**
```json
{
  "code": 1000,
  "message": "Thêm sản phẩm vào giỏ hàng thành công!",
  "result": {
    "cartId": "770e8400-e29b-41d4-a716-446655440000",
    "userId": "880e8400-e29b-41d4-a716-446655440000",
    "items": [
      {
        "id": "990e8400-e29b-41d4-a716-446655440000",
        "productId": "550e8400-e29b-41d4-a716-446655440000",
        "productName": "Áo Polo Nam Cao Cấp",
        "price": 299000,
        "salePrice": 249000,
        "imageUrl": "/uploads/polo-shirt.jpg",
        "quantity": 2,
        "subtotal": 498000,
        "productSizeId": "660e8400-e29b-41d4-a716-446655440000",
        "sizeName": "L",
        "availableQuantity": 48
      }
    ],
    "totalItems": 1,
    "totalQuantity": 2,
    "totalAmount": 498000
  }
}
```

---

### Ví dụ 2: Thêm cùng sản phẩm lần 2 (cộng dồn quantity)

**Request:**
```bash
POST /api/v1/cart/add
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "productId": "550e8400-e29b-41d4-a716-446655440000",
  "quantity": 3,
  "productSizeId": "660e8400-e29b-41d4-a716-446655440000"
}
```

**Response:**
```json
{
  "code": 1000,
  "message": "Thêm sản phẩm vào giỏ hàng thành công!",
  "result": {
    "cartId": "770e8400-e29b-41d4-a716-446655440000",
    "userId": "880e8400-e29b-41d4-a716-446655440000",
    "items": [
      {
        "id": "990e8400-e29b-41d4-a716-446655440000",
        "productId": "550e8400-e29b-41d4-a716-446655440000",
        "productName": "Áo Polo Nam Cao Cấp",
        "price": 299000,
        "salePrice": 249000,
        "imageUrl": "/uploads/polo-shirt.jpg",
        "quantity": 5,
        "subtotal": 1245000,
        "productSizeId": "660e8400-e29b-41d4-a716-446655440000",
        "sizeName": "L",
        "availableQuantity": 45
      }
    ],
    "totalItems": 1,
    "totalQuantity": 5,
    "totalAmount": 1245000
  }
}
```

**Chú ý:** Quantity đã tăng từ 2 lên 5 (2 + 3), và availableQuantity giảm từ 48 xuống 45.

---

### Ví dụ 3: Cập nhật số lượng

**Request:**
```bash
PUT /api/v1/cart/items/990e8400-e29b-41d4-a716-446655440000
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "quantity": 3
}
```

**Response:**
```json
{
  "code": 1000,
  "message": "Cập nhật giỏ hàng thành công!",
  "result": {
    "cartId": "770e8400-e29b-41d4-a716-446655440000",
    "userId": "880e8400-e29b-41d4-a716-446655440000",
    "items": [
      {
        "id": "990e8400-e29b-41d4-a716-446655440000",
        "productId": "550e8400-e29b-41d4-a716-446655440000",
        "productName": "Áo Polo Nam Cao Cấp",
        "price": 299000,
        "salePrice": 249000,
        "imageUrl": "/uploads/polo-shirt.jpg",
        "quantity": 3,
        "subtotal": 747000,
        "productSizeId": "660e8400-e29b-41d4-a716-446655440000",
        "sizeName": "L",
        "availableQuantity": 47
      }
    ],
    "totalItems": 1,
    "totalQuantity": 3,
    "totalAmount": 747000
  }
}
```

---

### Ví dụ 4: Thêm sản phẩm không có size

**Request:**
```bash
POST /api/v1/cart/add
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "productId": "111e8400-e29b-41d4-a716-446655440000",
  "quantity": 1
}
```

**Response:**
```json
{
  "code": 1000,
  "message": "Thêm sản phẩm vào giỏ hàng thành công!",
  "result": {
    "cartId": "770e8400-e29b-41d4-a716-446655440000",
    "userId": "880e8400-e29b-41d4-a716-446655440000",
    "items": [
      {
        "id": "990e8400-e29b-41d4-a716-446655440000",
        "productId": "550e8400-e29b-41d4-a716-446655440000",
        "productName": "Áo Polo Nam Cao Cấp",
        "price": 299000,
        "salePrice": 249000,
        "imageUrl": "/uploads/polo-shirt.jpg",
        "quantity": 3,
        "subtotal": 747000,
        "productSizeId": "660e8400-e29b-41d4-a716-446655440000",
        "sizeName": "L",
        "availableQuantity": 47
      },
      {
        "id": "222e8400-e29b-41d4-a716-446655440000",
        "productId": "111e8400-e29b-41d4-a716-446655440000",
        "productName": "Mũ Snapback",
        "price": 150000,
        "salePrice": null,
        "imageUrl": "/uploads/cap.jpg",
        "quantity": 1,
        "subtotal": 150000,
        "productSizeId": null,
        "sizeName": null,
        "availableQuantity": 100
      }
    ],
    "totalItems": 2,
    "totalQuantity": 4,
    "totalAmount": 897000
  }
}
```

**Chú ý:** Sản phẩm không có size thì `productSizeId` và `sizeName` sẽ là `null`.

---

## Error Codes

| Code | Message | Mô tả |
|------|---------|-------|
| 1001 | Truyền thiếu request param rồi má | Request thiếu tham số bắt buộc |
| 1099 | Dữ liệu không hợp lệ | Validation failed |
| 2101 | Please login! | Chưa đăng nhập |
| 2102 | Bạn không có quyền truy cập tài nguyên này | Không có quyền truy cập |
| 3001 | Không tìm thấy sản phẩm | Product không tồn tại |
| 3004 | Không tìm thấy size sản phẩm | ProductSize không tồn tại |
| 3005 | Size không thuộc sản phẩm này | Size không match với product |
| 4001 | Giỏ hàng của bạn đang trống | Cart rỗng |
| 4002 | Không tìm thấy sản phẩm trong giỏ hàng | CartItem không tồn tại |
| 4003 | Số lượng không hợp lệ | Quantity < 1 |
| 4004 | Số lượng sản phẩm trong kho không đủ | Insufficient stock |

---

## Best Practices

### 1. Frontend Implementation
```javascript
// Lấy giỏ hàng khi load trang
async function loadCart() {
  const response = await fetch('/api/v1/cart', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  const data = await response.json();
  displayCart(data.result);
}

// Thêm sản phẩm vào giỏ
async function addToCart(productId, quantity, sizeId = null) {
  const body = {
    productId,
    quantity
  };
  if (sizeId) {
    body.productSizeId = sizeId;
  }
  
  const response = await fetch('/api/v1/cart/add', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(body)
  });
  
  const data = await response.json();
  if (data.code === 1000) {
    showSuccessMessage(data.message);
    updateCartBadge(data.result.totalItems);
  } else {
    showErrorMessage(data.message);
  }
}

// Cập nhật số lượng
async function updateCartItem(cartItemId, quantity) {
  const response = await fetch(`/api/v1/cart/items/${cartItemId}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ quantity })
  });
  
  const data = await response.json();
  if (data.code === 1000) {
    displayCart(data.result);
  } else {
    showErrorMessage(data.message);
  }
}

// Xóa sản phẩm
async function removeCartItem(cartItemId) {
  const response = await fetch(`/api/v1/cart/items/${cartItemId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  const data = await response.json();
  if (data.code === 1000) {
    displayCart(data.result);
    showSuccessMessage(data.message);
  }
}

// Hiển thị badge số lượng items
async function updateCartBadge() {
  const response = await fetch('/api/v1/cart/count', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  const data = await response.json();
  document.getElementById('cart-badge').textContent = data.result;
}
```

### 2. Xử lý lỗi
- Luôn kiểm tra `code` trong response
- Hiển thị `message` từ server cho user
- Xử lý đặc biệt cho lỗi 2101 (chưa login) -> redirect đến trang login
- Xử lý lỗi 4004 (không đủ hàng) -> hiển thị số lượng available và cho phép chỉnh lại

### 3. UX Tips
- Hiển thị loading khi gọi API
- Debounce khi user thay đổi quantity (không gọi API liên tục)
- Confirm trước khi xóa sản phẩm hoặc clear cart
- Hiển thị badge số lượng items trên icon giỏ hàng
- Disable nút "Thêm vào giỏ" khi quantity > availableQuantity
- Hiển thị thông báo "Chỉ còn X sản phẩm" khi gần hết hàng

---

## Testing

### Test Case 1: Thêm sản phẩm mới vào giỏ hàng trống
```bash
# Giỏ hàng ban đầu trống
GET /api/v1/cart
-> totalItems: 0

# Thêm sản phẩm
POST /api/v1/cart/add
Body: { "productId": "abc", "quantity": 2, "productSizeId": "xyz" }

# Kiểm tra giỏ hàng
GET /api/v1/cart
-> totalItems: 1
-> items[0].quantity: 2
```

### Test Case 2: Thêm cùng sản phẩm lần 2
```bash
# Thêm lần 1
POST /api/v1/cart/add
Body: { "productId": "abc", "quantity": 2, "productSizeId": "xyz" }
-> items[0].quantity: 2

# Thêm lần 2 cùng sản phẩm và size
POST /api/v1/cart/add
Body: { "productId": "abc", "quantity": 3, "productSizeId": "xyz" }
-> items[0].quantity: 5 (2+3)
-> totalItems vẫn là 1
```

### Test Case 3: Thêm cùng sản phẩm nhưng khác size
```bash
# Thêm size M
POST /api/v1/cart/add
Body: { "productId": "abc", "quantity": 2, "productSizeId": "size-M" }
-> totalItems: 1

# Thêm size L
POST /api/v1/cart/add
Body: { "productId": "abc", "quantity": 1, "productSizeId": "size-L" }
-> totalItems: 2 (2 items khác nhau vì khác size)
```

### Test Case 4: Cập nhật quantity vượt quá tồn kho
```bash
# Sản phẩm có availableQuantity = 10
PUT /api/v1/cart/items/{id}
Body: { "quantity": 15 }
-> Error 4004: Số lượng sản phẩm trong kho không đủ
```

### Test Case 5: Xóa sản phẩm không thuộc về user
```bash
# User A tạo cart item có id = "item-123"
# User B cố xóa cart item của User A
DELETE /api/v1/cart/items/item-123
-> Error 2102: Bạn không có quyền truy cập tài nguyên này
```

---

## Tóm tắt

API Cart cung cấp đầy đủ chức năng quản lý giỏ hàng:
- ✅ Xem giỏ hàng với đầy đủ thông tin (giá, số lượng, tổng tiền)
- ✅ Thêm sản phẩm (hỗ trợ cả có size và không có size)
- ✅ Cập nhật số lượng
- ✅ Xóa sản phẩm
- ✅ Xóa toàn bộ giỏ hàng
- ✅ Lấy số lượng items (cho badge)
- ✅ Kiểm tra tồn kho tự động
- ✅ Tính giá tự động (support sale price)
- ✅ Security đầy đủ (authentication & authorization)

**Lưu ý quan trọng:**
- Giỏ hàng sẽ tự động bị xóa sau khi đặt hàng thành công
- Luôn kiểm tra `availableQuantity` trước khi cho phép thêm/cập nhật
- Sử dụng `salePrice` nếu có, nếu không thì dùng `price`

