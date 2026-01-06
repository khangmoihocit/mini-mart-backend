# API STATISTICS DOCUMENTATION

## Tổng quan
API thống kê cung cấp các endpoint để xem thống kê về sản phẩm, đơn hàng, doanh thu và danh mục sản phẩm.

**Base URL**: `/api/v1/statistics`

**Yêu cầu xác thực**: Tất cả các endpoint yêu cầu quyền ADMIN

---

## 1. Thống kê sản phẩm

### Lấy thống kê tổng quan về sản phẩm

**Endpoint**: `GET /api/v1/statistics/products`

**Mô tả**: Lấy thông tin thống kê tổng quan về sản phẩm trong hệ thống

**Headers**:
```
Authorization: Bearer {token}
```

**Response Success** (200 OK):
```json
{
  "code": 1000,
  "message": "Lấy thống kê sản phẩm thành công!",
  "result": {
    "totalProducts": 150,
    "outOfStockProducts": 5,
    "lowStockProducts": 12,
    "totalInventoryValue": 45000000.00,
    "averagePrice": 150000.00
  }
}
```

**Response Fields**:
- `totalProducts`: Tổng số sản phẩm
- `outOfStockProducts`: Số sản phẩm hết hàng (stock = 0)
- `lowStockProducts`: Số sản phẩm sắp hết hàng (stock < 10)
- `totalInventoryValue`: Tổng giá trị hàng tồn kho
- `averagePrice`: Giá trung bình của sản phẩm

---

## 2. Thống kê đơn hàng

### Lấy thống kê tổng quan về đơn hàng

**Endpoint**: `GET /api/v1/statistics/orders`

**Mô tả**: Lấy thông tin thống kê tổng quan về đơn hàng trong hệ thống

**Headers**:
```
Authorization: Bearer {token}
```

**Response Success** (200 OK):
```json
{
  "code": 1000,
  "message": "Lấy thống kê đơn hàng thành công!",
  "result": {
    "totalOrders": 500,
    "pendingOrders": 25,
    "processingOrders": 30,
    "shippedOrders": 20,
    "deliveredOrders": 400,
    "cancelledOrders": 25,
    "totalRevenue": 125000000.00,
    "pendingRevenue": 5000000.00,
    "averageOrderValue": 250000.00
  }
}
```

**Response Fields**:
- `totalOrders`: Tổng số đơn hàng
- `pendingOrders`: Số đơn hàng đang chờ xử lý
- `processingOrders`: Số đơn hàng đang xử lý
- `shippedOrders`: Số đơn hàng đang giao
- `deliveredOrders`: Số đơn hàng đã giao
- `cancelledOrders`: Số đơn hàng đã hủy
- `totalRevenue`: Tổng doanh thu (chỉ tính đơn hàng đã giao)
- `pendingRevenue`: Doanh thu đang chờ (đơn hàng chưa xử lý)
- `averageOrderValue`: Giá trị trung bình của đơn hàng

---

## 3. Sản phẩm bán chạy

### Lấy danh sách sản phẩm bán chạy nhất

**Endpoint**: `GET /api/v1/statistics/top-products`

**Mô tả**: Lấy danh sách các sản phẩm bán chạy nhất (tính tất cả thời gian)

**Headers**:
```
Authorization: Bearer {token}
```

**Query Parameters**:
| Tên | Kiểu | Mặc định | Mô tả |
|-----|------|----------|-------|
| limit | int | 10 | Số lượng sản phẩm muốn lấy |

**Example Request**:
```
GET /api/v1/statistics/top-products?limit=5
```

**Response Success** (200 OK):
```json
{
  "code": 1000,
  "message": "Lấy danh sách sản phẩm bán chạy thành công!",
  "result": [
    {
      "productId": "uuid-1",
      "productName": "Coca Cola 330ml",
      "totalSold": 1500,
      "totalRevenue": 22500000.00
    },
    {
      "productId": "uuid-2",
      "productName": "Pepsi 330ml",
      "totalSold": 1200,
      "totalRevenue": 18000000.00
    }
  ]
}
```

---

### Lấy sản phẩm bán chạy theo khoảng thời gian

**Endpoint**: `GET /api/v1/statistics/top-products/by-date`

**Mô tả**: Lấy danh sách các sản phẩm bán chạy nhất trong khoảng thời gian cụ thể

**Headers**:
```
Authorization: Bearer {token}
```

**Query Parameters**:
| Tên | Kiểu | Bắt buộc | Mô tả |
|-----|------|----------|-------|
| startDate | date | Yes | Ngày bắt đầu (yyyy-MM-dd) |
| endDate | date | Yes | Ngày kết thúc (yyyy-MM-dd) |
| limit | int | No (default: 10) | Số lượng sản phẩm muốn lấy |

**Example Request**:
```
GET /api/v1/statistics/top-products/by-date?startDate=2024-01-01&endDate=2024-12-31&limit=5
```

**Response Success** (200 OK):
```json
{
  "code": 1000,
  "message": "Lấy danh sách sản phẩm bán chạy theo thời gian thành công!",
  "result": [
    {
      "productId": "uuid-1",
      "productName": "Coca Cola 330ml",
      "totalSold": 450,
      "totalRevenue": 6750000.00
    },
    {
      "productId": "uuid-2",
      "productName": "Pepsi 330ml",
      "totalSold": 380,
      "totalRevenue": 5700000.00
    }
  ]
}
```

---

## 4. Thống kê doanh thu

### Lấy doanh thu theo ngày

**Endpoint**: `GET /api/v1/statistics/revenue/by-date`

**Mô tả**: Lấy thống kê doanh thu theo từng ngày trong khoảng thời gian cụ thể

**Headers**:
```
Authorization: Bearer {token}
```

**Query Parameters**:
| Tên | Kiểu | Bắt buộc | Mô tả |
|-----|------|----------|-------|
| startDate | date | Yes | Ngày bắt đầu (yyyy-MM-dd) |
| endDate | date | Yes | Ngày kết thúc (yyyy-MM-dd) |

**Example Request**:
```
GET /api/v1/statistics/revenue/by-date?startDate=2024-01-01&endDate=2024-01-07
```

**Response Success** (200 OK):
```json
{
  "code": 1000,
  "message": "Lấy thống kê doanh thu theo ngày thành công!",
  "result": [
    {
      "date": "2024-01-01",
      "orderCount": 15,
      "totalRevenue": 3750000.00
    },
    {
      "date": "2024-01-02",
      "orderCount": 20,
      "totalRevenue": 5000000.00
    },
    {
      "date": "2024-01-03",
      "orderCount": 18,
      "totalRevenue": 4500000.00
    }
  ]
}
```

**Response Fields**:
- `date`: Ngày
- `orderCount`: Số lượng đơn hàng trong ngày
- `totalRevenue`: Tổng doanh thu trong ngày (chỉ tính đơn hàng đã giao)

---

## 5. Thống kê theo danh mục

### Lấy thống kê theo danh mục sản phẩm

**Endpoint**: `GET /api/v1/statistics/categories`

**Mô tả**: Lấy thống kê hiệu suất bán hàng theo từng danh mục sản phẩm

**Headers**:
```
Authorization: Bearer {token}
```

**Response Success** (200 OK):
```json
{
  "code": 1000,
  "message": "Lấy thống kê theo danh mục thành công!",
  "result": [
    {
      "categoryId": "uuid-1",
      "categoryName": "Đồ uống",
      "productCount": 50,
      "totalSold": 5000,
      "totalRevenue": 75000000.00
    },
    {
      "categoryId": "uuid-2",
      "categoryName": "Bánh kẹo",
      "productCount": 30,
      "totalSold": 3000,
      "totalRevenue": 45000000.00
    },
    {
      "categoryId": "uuid-3",
      "categoryName": "Mỹ phẩm",
      "productCount": 25,
      "totalSold": 1000,
      "totalRevenue": 50000000.00
    }
  ]
}
```

**Response Fields**:
- `categoryId`: ID danh mục
- `categoryName`: Tên danh mục
- `productCount`: Số lượng sản phẩm trong danh mục
- `totalSold`: Tổng số lượng sản phẩm đã bán trong danh mục
- `totalRevenue`: Tổng doanh thu từ danh mục (chỉ tính đơn hàng đã giao)

---

## Error Responses

### 401 Unauthorized
```json
{
  "code": 1006,
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

### 400 Bad Request (Invalid Date Format)
```json
{
  "code": 1003,
  "message": "Invalid date format. Use yyyy-MM-dd"
}
```

---

## Ghi chú

1. **Quyền truy cập**: Tất cả các endpoint thống kê yêu cầu quyền ADMIN
2. **Định dạng ngày**: Sử dụng định dạng ISO 8601 (yyyy-MM-dd)
3. **Doanh thu**: Chỉ tính các đơn hàng có trạng thái DELIVERED
4. **Sản phẩm bán chạy**: Dựa trên tổng số lượng đã bán
5. **Performance**: Các query thống kê có thể mất thời gian với dữ liệu lớn, nên sử dụng cache khi cần thiết

---

## Use Cases

### 1. Dashboard Admin
```javascript
// Lấy thống kê tổng quan
const productStats = await fetch('/api/v1/statistics/products');
const orderStats = await fetch('/api/v1/statistics/orders');
const topProducts = await fetch('/api/v1/statistics/top-products?limit=5');
```

### 2. Báo cáo doanh thu tháng
```javascript
// Lấy doanh thu theo ngày trong tháng 1/2024
const revenue = await fetch('/api/v1/statistics/revenue/by-date?startDate=2024-01-01&endDate=2024-01-31');
```

### 3. Phân tích danh mục
```javascript
// Xem danh mục nào bán chạy nhất
const categoryStats = await fetch('/api/v1/statistics/categories');
```

### 4. Sản phẩm hot trong tuần
```javascript
// Top 10 sản phẩm bán chạy tuần này
const today = new Date();
const weekAgo = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);
const hotProducts = await fetch(`/api/v1/statistics/top-products/by-date?startDate=${weekAgo.toISOString().split('T')[0]}&endDate=${today.toISOString().split('T')[0]}&limit=10`);
```

