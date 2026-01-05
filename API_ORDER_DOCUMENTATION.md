 # API Order Documentation

## API Đặt Hàng và Quản Lý Đơn Hàng

### 1. API cho User

#### 1.1. Tạo đơn hàng từ giỏ hàng
**Endpoint:** `POST /api/v1/orders`

**Mô tả:** Tạo đơn hàng mới từ các sản phẩm trong giỏ hàng hiện tại. Sau khi đặt hàng thành công, giỏ hàng sẽ được xóa.

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "fullName": "Nguyễn Văn A",
  "email": "nguyenvana@example.com",
  "phoneNumber": "0123456789",
  "shippingAddress": "123 Đường ABC, Quận 1, TP.HCM",
  "note": "Giao hàng buổi sáng",
  "shippingMethod": "Giao hàng tiêu chuẩn",
  "paymentMethod": "Thanh toán khi nhận hàng"
}
```

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Đặt hàng thành công!",
  "result": {
    "id": "order-uuid",
    "userId": "user-uuid",
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "phoneNumber": "0123456789",
    "shippingAddress": "123 Đường ABC, Quận 1, TP.HCM",
    "note": "Giao hàng buổi sáng",
    "status": "PENDING",
    "totalMoney": 500000,
    "shippingMethod": "Giao hàng tiêu chuẩn",
    "paymentMethod": "Thanh toán khi nhận hàng",
    "orderDate": "2026-01-05T15:30:00",
    "orderDetails": [
      {
        "id": "detail-uuid",
        "productId": "product-uuid",
        "productName": "Sản phẩm A",
        "productImage": "url-to-image",
        "productSize": "M",
        "price": 250000,
        "numberOfProducts": 2,
        "totalMoney": 500000
      }
    ]
  }
}
```

---

#### 1.2. Lấy danh sách đơn hàng của tôi
**Endpoint:** `GET /api/v1/orders/my-orders?page=1&size=10`

**Mô tả:** Lấy danh sách tất cả đơn hàng của user hiện tại (có phân trang).

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `page` (integer, default=1): Số trang
- `size` (integer, default=10): Số items mỗi trang

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Lấy danh sách đơn hàng thành công!",
  "result": {
    "content": [
      {
        "id": "order-uuid",
        "userId": "user-uuid",
        "fullName": "Nguyễn Văn A",
        "email": "nguyenvana@example.com",
        "phoneNumber": "0123456789",
        "shippingAddress": "123 Đường ABC, Quận 1, TP.HCM",
        "note": "Giao hàng buổi sáng",
        "status": "PENDING",
        "totalMoney": 500000,
        "shippingMethod": "Giao hàng tiêu chuẩn",
        "paymentMethod": "Thanh toán khi nhận hàng",
        "orderDate": "2026-01-05T15:30:00",
        "orderDetails": [...]
      }
    ],
    "pageNo": 1,
    "pageSize": 10,
    "totalElements": 25,
    "totalPages": 3,
    "first": true,
    "last": false
  }
}
```

---

#### 1.3. Lấy chi tiết đơn hàng
**Endpoint:** `GET /api/v1/orders/{orderId}`

**Mô tả:** Lấy chi tiết một đơn hàng cụ thể. Chỉ lấy được đơn hàng của chính mình.

**Headers:**
```
Authorization: Bearer {token}
```

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Lấy chi tiết đơn hàng thành công!",
  "result": {
    "id": "order-uuid",
    "userId": "user-uuid",
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "phoneNumber": "0123456789",
    "shippingAddress": "123 Đường ABC, Quận 1, TP.HCM",
    "note": "Giao hàng buổi sáng",
    "status": "PENDING",
    "totalMoney": 500000,
    "shippingMethod": "Giao hàng tiêu chuẩn",
    "paymentMethod": "Thanh toán khi nhận hàng",
    "orderDate": "2026-01-05T15:30:00",
    "orderDetails": [
      {
        "id": "detail-uuid",
        "productId": "product-uuid",
        "productName": "Sản phẩm A",
        "productImage": "url-to-image",
        "productSize": "M",
        "price": 250000,
        "numberOfProducts": 2,
        "totalMoney": 500000
      }
    ]
  }
}
```

---

#### 1.4. Hủy đơn hàng
**Endpoint:** `PUT /api/v1/orders/{orderId}/cancel`

**Mô tả:** Hủy đơn hàng. Chỉ có thể hủy đơn hàng ở trạng thái `PENDING`. Khi hủy, số lượng sản phẩm sẽ được hoàn lại vào kho.

**Headers:**
```
Authorization: Bearer {token}
```

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Hủy đơn hàng thành công!",
  "result": {
    "id": "order-uuid",
    "status": "CANCELLED",
    ...
  }
}
```

**Error Response (400):**
```json
{
  "code": 4006,
  "message": "Không thể hủy đơn hàng ở trạng thái này."
}
```

---

### 2. API cho Admin

#### 2.1. Lấy tất cả đơn hàng (Admin)
**Endpoint:** `GET /api/v1/orders/admin/all?page=1&size=10`

**Mô tả:** Lấy danh sách tất cả đơn hàng trong hệ thống (có phân trang).

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Query Parameters:**
- `page` (integer, default=1): Số trang
- `size` (integer, default=10): Số items mỗi trang

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Lấy danh sách tất cả đơn hàng thành công!",
  "result": {
    "content": [
      {
        "id": "order-uuid",
        "userId": "user-uuid",
        "fullName": "Nguyễn Văn A",
        "email": "nguyenvana@example.com",
        "phoneNumber": "0123456789",
        "shippingAddress": "123 Đường ABC, Quận 1, TP.HCM",
        "note": "Giao hàng buổi sáng",
        "status": "PENDING",
        "totalMoney": 500000,
        "shippingMethod": "Giao hàng tiêu chuẩn",
        "paymentMethod": "Thanh toán khi nhận hàng",
        "orderDate": "2026-01-05T15:30:00",
        "orderDetails": [...]
      }
    ],
    "pageNo": 1,
    "pageSize": 10,
    "totalElements": 100,
    "totalPages": 10,
    "first": true,
    "last": false
  }
}
```

---

#### 2.2. Lấy đơn hàng theo trạng thái (Admin)
**Endpoint:** `GET /api/v1/orders/admin/status/{status}?page=1&size=10`

**Mô tả:** Lấy danh sách đơn hàng theo trạng thái cụ thể.

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Path Parameters:**
- `status`: Trạng thái đơn hàng
  - `PENDING`: Chờ xử lý
  - `PROCESSING`: Đang xử lý
  - `SHIPPED`: Đã giao cho vận chuyển
  - `DELIVERED`: Đã giao hàng
  - `CANCELLED`: Đã hủy

**Query Parameters:**
- `page` (integer, default=1): Số trang
- `size` (integer, default=10): Số items mỗi trang

**Example:** `GET /api/v1/orders/admin/status/PENDING?page=1&size=10`

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Lấy danh sách đơn hàng theo trạng thái thành công!",
  "result": {
    "content": [...],
    "pageNo": 1,
    "pageSize": 10,
    "totalElements": 15,
    "totalPages": 2,
    "first": true,
    "last": false
  }
}
```

---

#### 2.3. Cập nhật trạng thái đơn hàng (Admin)
**Endpoint:** `PUT /api/v1/orders/admin/{orderId}/status`

**Mô tả:** Cập nhật trạng thái của đơn hàng. Nếu chuyển sang trạng thái `CANCELLED`, số lượng sản phẩm sẽ được hoàn lại vào kho.

**Headers:**
```
Authorization: Bearer {admin-token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "status": "PROCESSING"
}
```

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Cập nhật trạng thái đơn hàng thành công!",
  "result": {
    "id": "order-uuid",
    "status": "PROCESSING",
    ...
  }
}
```

---

#### 2.4. Xóa đơn hàng (Admin)
**Endpoint:** `DELETE /api/v1/orders/admin/{orderId}`

**Mô tả:** Xóa đơn hàng khỏi hệ thống. Nếu đơn hàng chưa ở trạng thái `CANCELLED`, số lượng sản phẩm sẽ được hoàn lại vào kho trước khi xóa.

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Response Success (200):**
```json
{
  "code": 1000,
  "message": "Xóa đơn hàng thành công!"
}
```

---

## Trạng thái đơn hàng (OrderStatus)

1. **PENDING**: Chờ xử lý - Đơn hàng mới được tạo
2. **PROCESSING**: Đang xử lý - Admin đang chuẩn bị đơn hàng
3. **SHIPPED**: Đã giao cho vận chuyển - Đơn hàng đang trên đường giao
4. **DELIVERED**: Đã giao hàng - Khách hàng đã nhận được hàng
5. **CANCELLED**: Đã hủy - Đơn hàng bị hủy bởi user hoặc admin

---

## Error Codes

- **4001**: Giỏ hàng trống
- **4004**: Số lượng sản phẩm trong kho không đủ
- **4005**: Không tìm thấy đơn hàng
- **4006**: Không thể hủy đơn hàng ở trạng thái này
- **2102**: Không có quyền truy cập (Access denied)

---

## Lưu ý

1. Tất cả API đều yêu cầu authentication (trừ login/register)
2. API Admin yêu cầu role ADMIN
3. Khi tạo đơn hàng, giỏ hàng phải có ít nhất 1 sản phẩm
4. Khi hủy hoặc xóa đơn hàng, số lượng sản phẩm sẽ được hoàn lại vào kho
5. User chỉ có thể hủy đơn hàng ở trạng thái PENDING
6. Admin có thể cập nhật trạng thái đơn hàng sang bất kỳ trạng thái nào

