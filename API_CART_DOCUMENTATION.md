# API Cart Documentation - Giỏ hàng với hỗ trợ Product Size

## Tổng quan
API giỏ hàng hỗ trợ đầy đủ chức năng quản lý giỏ hàng với khả năng chọn size cho sản phẩm. Mỗi sản phẩm có thể có nhiều size khác nhau, và cùng một sản phẩm với size khác nhau sẽ tạo thành các cart items riêng biệt.

---

## 1. Lấy giỏ hàng của user

### Endpoint
```
GET /api/v1/cart
```

### Authentication
✅ **Required** - Cần JWT token

### Headers
```
Authorization: Bearer {your_token}
```

### Request
Không có parameters

### Response Success (200 OK)

```json
{
  "code": 1000,
  "message": "Lấy giỏ hàng thành công!",
  "result": {
    "cartId": "cart-abc123",
    "userId": "user-xyz789",
    "items": [
      {
        "id": "cart-item-001",
        "productId": "prod-001",
        "productName": "Áo thun cotton basic",
        "price": 199000,
        "salePrice": 149000,
        "imageUrl": "uuid_image1.jpg",
        "quantity": 2,
        "subtotal": 298000,
        "productSizeId": "size-001",
        "sizeName": "L",
        "availableQuantity": 30
      },
      {
        "id": "cart-item-002",
        "productId": "prod-001",
        "productName": "Áo thun cotton basic",
        "price": 199000,
        "salePrice": 149000,
        "imageUrl": "uuid_image1.jpg",
        "quantity": 1,
        "subtotal": 149000,
        "productSizeId": "size-002",
        "sizeName": "XL",
        "availableQuantity": 20
      },
      {
        "id": "cart-item-003",
        "productId": "prod-002",
        "productName": "Quần jean slim fit",
        "price": 399000,
        "salePrice": null,
        "imageUrl": "uuid_image2.jpg",
        "quantity": 1,
        "subtotal": 399000,
        "productSizeId": null,
        "sizeName": null,
        "availableQuantity": 50
      }
    ],
    "totalItems": 3,
    "totalQuantity": 4,
    "totalAmount": 846000
  }
}
```

### Response khi giỏ hàng rỗng

```json
{
  "code": 1000,
  "message": "Lấy giỏ hàng thành công!",
  "result": {
    "cartId": "cart-abc123",
    "userId": "user-xyz789",
    "items": [],
    "totalItems": 0,
    "totalQuantity": 0,
    "totalAmount": 0
  }
}
```

### Response Fields Explained

| Field | Type | Description |
|-------|------|-------------|
| `cartId` | String | ID của giỏ hàng |
| `userId` | String | ID của user sở hữu giỏ hàng |
| `items` | Array | Danh sách sản phẩm trong giỏ |
| `totalItems` | Integer | Tổng số items (sản phẩm) trong giỏ |
| `totalQuantity` | Integer | Tổng số lượng sản phẩm |
| `totalAmount` | BigDecimal | Tổng giá trị giỏ hàng |

### Cart Item Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | String | ID của cart item (để update/delete) |
| `productId` | String | ID sản phẩm |
| `productName` | String | Tên sản phẩm |
| `price` | BigDecimal | Giá gốc |
| `salePrice` | BigDecimal | Giá khuyến mãi (null nếu không có) |
| `imageUrl` | String | URL ảnh đầu tiên của sản phẩm |
| `quantity` | Integer | Số lượng trong giỏ |
| `subtotal` | BigDecimal | Tổng tiền của item này (giá × số lượng) |
| `productSizeId` | String | ID của size (null nếu sản phẩm không có size) |
| `sizeName` | String | Tên size (VD: S, M, L, XL) |
| `availableQuantity` | Integer | Số lượng còn lại trong kho của size này |

---

## 2. Thêm sản phẩm vào giỏ hàng

### Endpoint
```
POST /api/v1/cart/add
```

### Authentication
✅ **Required** - Cần JWT token

### Headers
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

### Request Body

#### Trường hợp 1: Sản phẩm KHÔNG CÓ size
```json
{
  "productId": "prod-001",
  "quantity": 2
}
```

#### Trường hợp 2: Sản phẩm CÓ size
```json
{
  "productId": "prod-001",
  "productSizeId": "size-001",
  "quantity": 2
}
```

### Request Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `productId` | String | ✅ Yes | ID sản phẩm cần thêm |
| `productSizeId` | String | ❌ No | ID size (bắt buộc nếu sản phẩm có size) |
| `quantity` | Integer | ✅ Yes | Số lượng cần thêm (≥ 1) |

### Logic xử lý

1. **Nếu sản phẩm + size ĐÃ CÓ trong giỏ:**
   - Cộng thêm số lượng vào item hiện có
   - Kiểm tra tổng số lượng không vượt quá tồn kho

2. **Nếu sản phẩm + size CHƯA CÓ trong giỏ:**
   - Tạo cart item mới
   - Kiểm tra số lượng không vượt quá tồn kho

3. **Validation:**
   - Kiểm tra sản phẩm tồn tại
   - Kiểm tra size tồn tại (nếu có)
   - Kiểm tra size có thuộc sản phẩm không
   - Kiểm tra số lượng tồn kho đủ không

### Response Success (200 OK)

```json
{
  "code": 1000,
  "message": "Thêm sản phẩm vào giỏ hàng thành công!",
  "result": {
    "cartId": "cart-abc123",
    "userId": "user-xyz789",
    "items": [
      {
        "id": "cart-item-001",
        "productId": "prod-001",
        "productName": "Áo thun cotton basic",
        "price": 199000,
        "salePrice": 149000,
        "imageUrl": "uuid_image1.jpg",
        "quantity": 2,
        "subtotal": 298000,
        "productSizeId": "size-001",
        "sizeName": "L",
        "availableQuantity": 28
      }
    ],
    "totalItems": 1,
    "totalQuantity": 2,
    "totalAmount": 298000
  }
}
```

### Error Responses

#### 3004 - Size không tồn tại
```json
{
  "code": 3004,
  "message": "Không tìm thấy size sản phẩm."
}
```

#### 3005 - Size không thuộc sản phẩm
```json
{
  "code": 3005,
  "message": "Size không thuộc sản phẩm này."
}
```

#### 4004 - Không đủ số lượng
```json
{
  "code": 4004,
  "message": "Số lượng sản phẩm trong kho không đủ."
}
```

#### 3001 - Sản phẩm không tồn tại
```json
{
  "code": 3001,
  "message": "Không tìm thấy sản phẩm."
}
```

---

## 3. Cập nhật số lượng sản phẩm trong giỏ

### Endpoint
```
PUT /api/v1/cart/items/{cartItemId}
```

### Authentication
✅ **Required** - Cần JWT token

### Headers
```
Authorization: Bearer {your_token}
Content-Type: application/json
```

### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| cartItemId | String | ID của cart item cần cập nhật |

### Request Body

```json
{
  "quantity": 5
}
```

### Request Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `quantity` | Integer | ✅ Yes | Số lượng mới (≥ 1) |

### Logic xử lý

1. Tìm cart item theo ID
2. Kiểm tra cart item có thuộc user không
3. Kiểm tra số lượng mới không vượt quá tồn kho (từ size nếu có)
4. Cập nhật số lượng

### Response Success (200 OK)

```json
{
  "code": 1000,
  "message": "Cập nhật giỏ hàng thành công!",
  "result": {
    "cartId": "cart-abc123",
    "userId": "user-xyz789",
    "items": [
      {
        "id": "cart-item-001",
        "productId": "prod-001",
        "productName": "Áo thun cotton basic",
        "quantity": 5,
        "subtotal": 745000,
        "availableQuantity": 25
      }
    ],
    "totalItems": 1,
    "totalQuantity": 5,
    "totalAmount": 745000
  }
}
```

### Error Responses

#### 4002 - Cart item không tồn tại
```json
{
  "code": 4002,
  "message": "Không tìm thấy sản phẩm trong giỏ hàng."
}
```

#### 4004 - Không đủ số lượng
```json
{
  "code": 4004,
  "message": "Số lượng sản phẩm trong kho không đủ."
}
```

#### 2102 - Không có quyền
```json
{
  "code": 2102,
  "message": "Bạn không có quyền truy cập tài nguyên này."
}
```

---

## 4. Xóa sản phẩm khỏi giỏ hàng

### Endpoint
```
DELETE /api/v1/cart/items/{cartItemId}
```

### Authentication
✅ **Required** - Cần JWT token

### Headers
```
Authorization: Bearer {your_token}
```

### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| cartItemId | String | ID của cart item cần xóa |

### Response Success (200 OK)

```json
{
  "code": 1000,
  "message": "Xóa sản phẩm khỏi giỏ hàng thành công!",
  "result": {
    "cartId": "cart-abc123",
    "userId": "user-xyz789",
    "items": [],
    "totalItems": 0,
    "totalQuantity": 0,
    "totalAmount": 0
  }
}
```

### Error Responses

#### 4002 - Cart item không tồn tại
```json
{
  "code": 4002,
  "message": "Không tìm thấy sản phẩm trong giỏ hàng."
}
```

#### 2102 - Không có quyền
```json
{
  "code": 2102,
  "message": "Bạn không có quyền truy cập tài nguyên này."
}
```

---

## 5. Xóa toàn bộ giỏ hàng

### Endpoint
```
DELETE /api/v1/cart/clear
```

### Authentication
✅ **Required** - Cần JWT token

### Headers
```
Authorization: Bearer {your_token}
```

### Response Success (200 OK)

```json
{
  "code": 1000,
  "message": "Xóa toàn bộ giỏ hàng thành công!"
}
```

---

## 6. Lấy số lượng items trong giỏ hàng

### Endpoint
```
GET /api/v1/cart/count
```

### Authentication
✅ **Required** - Cần JWT token

### Headers
```
Authorization: Bearer {your_token}
```

### Response Success (200 OK)

```json
{
  "code": 1000,
  "message": "Lấy số lượng items thành công!",
  "result": 5
}
```

**Note:** Trả về tổng số items (không phải tổng số lượng). Ví dụ:
- 2 items với quantity 2 mỗi item → result = 2
- 3 items với quantity 1, 2, 3 → result = 3

---

## Use Cases & Examples

### Use Case 1: User thêm áo size L vào giỏ

**Step 1: Thêm vào giỏ lần đầu**
```http
POST /api/v1/cart/add
{
  "productId": "prod-001",
  "productSizeId": "size-L",
  "quantity": 1
}
```

**Result:** Giỏ hàng có 1 item (Áo - Size L - Qty: 1)

**Step 2: Thêm vào giỏ lần 2 (cùng sản phẩm, cùng size)**
```http
POST /api/v1/cart/add
{
  "productId": "prod-001",
  "productSizeId": "size-L",
  "quantity": 2
}
```

**Result:** Giỏ hàng vẫn 1 item (Áo - Size L - Qty: 3)

### Use Case 2: User thêm áo size M vào giỏ (đã có size L)

```http
POST /api/v1/cart/add
{
  "productId": "prod-001",
  "productSizeId": "size-M",
  "quantity": 1
}
```

**Result:** Giỏ hàng có 2 items:
- Item 1: Áo - Size L - Qty: 3
- Item 2: Áo - Size M - Qty: 1

### Use Case 3: User cập nhật số lượng

```http
PUT /api/v1/cart/items/cart-item-001
{
  "quantity": 5
}
```

**Result:** Item có ID `cart-item-001` được update quantity = 5

### Use Case 4: User xóa một item

```http
DELETE /api/v1/cart/items/cart-item-001
```

**Result:** Item có ID `cart-item-001` bị xóa khỏi giỏ

### Use Case 5: User xóa toàn bộ giỏ hàng

```http
DELETE /api/v1/cart/clear
```

**Result:** Tất cả items trong giỏ bị xóa

---

## Frontend Integration Examples

### Example 1: Hiển thị giỏ hàng

```javascript
async function getCart() {
  const response = await fetch('http://localhost:8081/api/v1/cart', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  const data = await response.json();
  const cart = data.result;
  
  console.log('Total items:', cart.totalItems);
  console.log('Total amount:', cart.totalAmount);
  
  cart.items.forEach(item => {
    console.log(`${item.productName} - ${item.sizeName || 'No size'} x${item.quantity}`);
  });
}
```

### Example 2: Thêm sản phẩm vào giỏ

```javascript
async function addToCart(productId, sizeId, quantity) {
  const body = { productId, quantity };
  if (sizeId) body.productSizeId = sizeId;
  
  const response = await fetch('http://localhost:8081/api/v1/cart/add', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(body)
  });
  
  const data = await response.json();
  
  if (data.code === 1000) {
    console.log('Added to cart successfully!');
    return data.result;
  } else {
    console.error('Error:', data.message);
    throw new Error(data.message);
  }
}

// Usage
try {
  await addToCart('prod-001', 'size-L', 2);
  alert('Đã thêm vào giỏ hàng!');
} catch (error) {
  alert(error.message);
}
```

### Example 3: Cập nhật số lượng

```javascript
async function updateCartItem(cartItemId, quantity) {
  const response = await fetch(
    `http://localhost:8081/api/v1/cart/items/${cartItemId}`,
    {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ quantity })
    }
  );
  
  const data = await response.json();
  return data.result;
}
```

### Example 4: Xóa cart item

```javascript
async function removeCartItem(cartItemId) {
  const response = await fetch(
    `http://localhost:8081/api/v1/cart/items/${cartItemId}`,
    {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );
  
  const data = await response.json();
  return data.result;
}
```

### Example 5: Badge hiển thị số items

```javascript
async function getCartCount() {
  const response = await fetch('http://localhost:8081/api/v1/cart/count', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  const data = await response.json();
  const count = data.result;
  
  // Update badge
  document.getElementById('cart-badge').textContent = count;
}

// Call every time cart changes
getCartCount();
```

---

## UI/UX Recommendations

### 1. Product Detail Page

**Khi sản phẩm CÓ sizes:**
```html
<select id="size-selector" required>
  <option value="">Chọn size</option>
  <option value="size-S">S (còn 10)</option>
  <option value="size-M">M (còn 20)</option>
  <option value="size-L">L (còn 5)</option>
  <option value="size-XL" disabled>XL (hết hàng)</option>
</select>
<button onclick="addToCart()">Thêm vào giỏ</button>
```

**Validation:**
- Disable nút "Thêm vào giỏ" nếu chưa chọn size
- Disable size đã hết hàng (quantity = 0)
- Hiển thị số lượng còn lại

### 2. Cart Page

**Display cart item:**
```html
<div class="cart-item">
  <img src="{imageUrl}" />
  <div class="info">
    <h3>{productName}</h3>
    <p>Size: <strong>{sizeName}</strong></p>
    <p>Giá: <strong>{salePrice || price}</strong></p>
    <p class="stock-warning">Còn {availableQuantity} sản phẩm</p>
  </div>
  <div class="quantity">
    <button onclick="decrease()">-</button>
    <input type="number" value="{quantity}" max="{availableQuantity}" />
    <button onclick="increase()">+</button>
  </div>
  <div class="subtotal">{subtotal}</div>
  <button onclick="remove()">Xóa</button>
</div>
```

**Validation:**
- Hiển thị cảnh báo nếu `quantity > availableQuantity`
- Disable nút "+" nếu đạt max quantity
- Auto update subtotal khi thay đổi quantity
- Debounce update request (500ms)

### 3. Cart Badge (Navigation)

```html
<a href="/cart">
  <i class="icon-cart"></i>
  <span class="badge">{count}</span>
</a>
```

Update badge sau khi:
- Thêm vào giỏ
- Xóa khỏi giỏ
- Clear giỏ hàng

### 4. Error Handling

```javascript
try {
  await addToCart(productId, sizeId, quantity);
} catch (error) {
  if (error.code === 4004) {
    alert('Xin lỗi, sản phẩm không đủ số lượng trong kho!');
  } else if (error.code === 3005) {
    alert('Size không hợp lệ!');
  } else {
    alert('Có lỗi xảy ra, vui lòng thử lại!');
  }
}
```

---

## Business Rules

### 1. Số lượng tồn kho

- **Sản phẩm có size:** Kiểm tra từ `ProductSize.quantity`
- **Sản phẩm không có size:** Kiểm tra từ `Product.stockQuantity`
- Không cho phép thêm/update nếu vượt quá số lượng

### 2. Tính subtotal

```
subtotal = (salePrice || price) × quantity
```

Luôn ưu tiên `salePrice` nếu có.

### 3. Tính totalAmount

```
totalAmount = sum(all item.subtotal)
```

### 4. Cùng sản phẩm, khác size

Tạo thành **2 cart items riêng biệt**:
- Áo - Size M → Cart Item 1
- Áo - Size L → Cart Item 2

### 5. Validation

- `quantity` phải ≥ 1
- `quantity` ≤ `availableQuantity`
- `productSizeId` phải thuộc `productId`

---

## Database Schema

### Table: carts
```sql
CREATE TABLE carts (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL UNIQUE,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Table: cart_details
```sql
CREATE TABLE cart_details (
  id VARCHAR(36) PRIMARY KEY,
  cart_id VARCHAR(36) NOT NULL,
  product_id VARCHAR(36) NOT NULL,
  product_size_id VARCHAR(36) NULL,
  quantity INT NOT NULL,
  FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  FOREIGN KEY (product_size_id) REFERENCES product_sizes(id) ON DELETE CASCADE
);
```

**Cascade Delete:**
- Xóa Cart → Tự động xóa CartDetails
- Xóa Product → Tự động xóa CartDetails
- Xóa ProductSize → Tự động xóa CartDetails liên quan

---

## Testing Checklist

### Test Cart APIs

- [ ] GET /cart - Lấy giỏ hàng rỗng
- [ ] GET /cart - Lấy giỏ hàng có items
- [ ] POST /cart/add - Thêm sản phẩm không có size
- [ ] POST /cart/add - Thêm sản phẩm có size
- [ ] POST /cart/add - Thêm cùng product + cùng size (tăng quantity)
- [ ] POST /cart/add - Thêm cùng product + khác size (tạo item mới)
- [ ] POST /cart/add - Thêm vượt quá tồn kho (error 4004)
- [ ] POST /cart/add - Size không tồn tại (error 3004)
- [ ] POST /cart/add - Size không thuộc product (error 3005)
- [ ] PUT /cart/items/{id} - Update số lượng hợp lệ
- [ ] PUT /cart/items/{id} - Update vượt quá tồn kho (error)
- [ ] DELETE /cart/items/{id} - Xóa item
- [ ] DELETE /cart/clear - Xóa toàn bộ giỏ
- [ ] GET /cart/count - Đếm số items

---

## Changelog

### Version 2.0 (2026-01-04)
- ✅ Thêm hỗ trợ ProductSize trong giỏ hàng
- ✅ Kiểm tra số lượng tồn kho từ size
- ✅ Validate size có thuộc product
- ✅ Hiển thị `availableQuantity` trong response
- ✅ Thêm error codes: 3004, 3005
- ✅ Cải thiện transaction handling

### Version 1.0
- ✅ Basic cart CRUD operations
- ✅ Add/Update/Remove cart items
- ✅ Stock quantity validation

---

## Best Practices

1. **Luôn validate số lượng** trước khi thêm/update
2. **Hiển thị availableQuantity** để user biết còn bao nhiêu
3. **Debounce update requests** khi user thay đổi quantity liên tục
4. **Cache cart data** trong frontend để giảm số request
5. **Update cart badge** real-time sau mọi thao tác
6. **Handle errors gracefully** với thông báo rõ ràng
7. **Confirm trước khi xóa** toàn bộ giỏ hàng

---

**Note:** Tất cả cart APIs yêu cầu authentication. User chỉ có thể thao tác với giỏ hàng của chính mình.

