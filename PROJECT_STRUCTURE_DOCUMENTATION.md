# TÀI LIỆU MÔ TẢ CẤU TRÚC DỰ ÁN MINI MART BACKEND

---

## 📌 THÔNG TIN TỔNG QUAN

### Tên dự án
**Mini Mart Backend System**

### Mô tả
Hệ thống backend RESTful API cho ứng dụng quản lý và bán hàng mini mart, được phát triển với công nghệ Spring Boot hiện đại.

### Thông tin kỹ thuật
- **Framework**: Spring Boot 3.5.4
- **Ngôn ngữ lập trình**: Java 17
- **Cơ sở dữ liệu**: MySQL
- **Build Tool**: Apache Maven
- **Port mặc định**: 8081
- **API Version**: v1
- **Base API URL**: `http://localhost:8081/api/v1`

---

## 🏗️ KIẾN TRÚC DỰ ÁN

### 1. Kiến trúc tổng thể
Dự án sử dụng kiến trúc **Layered Architecture** (Kiến trúc phân lớp) với các tầng:

```
┌─────────────────────────────────────┐
│   Controller Layer (REST APIs)      │ ← Xử lý HTTP requests/responses
├─────────────────────────────────────┤
│   Service Layer (Business Logic)    │ ← Logic nghiệp vụ
├─────────────────────────────────────┤
│   Repository Layer (Data Access)    │ ← Truy xuất dữ liệu
├─────────────────────────────────────┤
│   Database (MySQL)                   │ ← Lưu trữ dữ liệu
└─────────────────────────────────────┘
```

### 2. Design Patterns được áp dụng
- **MVC Pattern**: Tách biệt Model-View-Controller
- **Repository Pattern**: Quản lý truy xuất dữ liệu
- **Service Pattern**: Đóng gói business logic
- **DTO Pattern**: Transfer data giữa các layers
- **Dependency Injection**: Quản lý dependencies
- **Specification Pattern**: Tạo dynamic queries

---

## 📁 CẤU TRÚC THỨ MỤC CHI TIẾT

### Cấu trúc tổng quan
```
mini-mart-backend/
├── src/
│   ├── main/
│   │   ├── java/com/khangmoihocit/minimart/
│   │   │   ├── configuration/      # Cấu hình ứng dụng
│   │   │   ├── controller/         # REST Controllers
│   │   │   ├── dto/                # Data Transfer Objects
│   │   │   ├── entity/             # JPA Entities
│   │   │   ├── enums/              # Enum classes
│   │   │   ├── exception/          # Exception handling
│   │   │   ├── mapper/             # MapStruct mappers
│   │   │   ├── repository/         # JPA Repositories
│   │   │   ├── service/            # Business logic
│   │   │   ├── utils/              # Utility classes
│   │   │   └── MinimartApplication.java
│   │   └── resources/
│   │       └── application.yaml    # Application configuration
│   └── test/                       # Unit & Integration tests
├── target/                         # Compiled files
├── uploads/                        # User uploaded files
├── pom.xml                         # Maven configuration
├── API_DOCUMENTATION.md            # API documentation
├── API_CART_DOCUMENTATION.md       # Cart API docs
├── API_ORDER_DOCUMENTATION.md      # Order API docs
├── API_STATISTICS_DOCUMENTATION.md # Statistics API docs
└── database_reviews_table.sql      # Database schema
```

---

## 📦 CÁC PACKAGE VÀ CHỨC NĂNG

### 1. **configuration/** - Cấu hình ứng dụng
Chứa các class cấu hình cho Spring Boot application:
- **ApplicationInitConfig**: Khởi tạo dữ liệu ban đầu (roles, admin user)
- **JwtAuthenticationEntryPoint**: Xử lý lỗi authentication
- **SecurityConfig**: Cấu hình Spring Security
- **JwtTokenFilter**: Filter xử lý JWT token
- **WebConfig**: Cấu hình CORS, static resources

**Chức năng chính:**
- Cấu hình Spring Security và JWT authentication
- Khởi tạo dữ liệu mặc định khi start ứng dụng
- Cấu hình CORS cho phép frontend access
- Cấu hình file upload và static resource serving

---

### 2. **controller/** - REST API Controllers
Xử lý các HTTP requests và trả về responses:

#### **AuthenticationController.java**
- `POST /api/v1/auth/log-in` - Đăng nhập
- `POST /api/v1/auth/sign-up` - Đăng ký tài khoản
- `POST /api/v1/auth/refresh-token` - Làm mới token
- `POST /api/v1/auth/log-out` - Đăng xuất

#### **UserController.java**
- `GET /api/v1/users` - Lấy danh sách users
- `GET /api/v1/users/{id}` - Lấy thông tin user theo ID
- `GET /api/v1/users/my-info` - Lấy thông tin user hiện tại
- `PUT /api/v1/users/{id}` - Cập nhật thông tin user
- `DELETE /api/v1/users/{id}` - Xóa user

#### **ProductController.java**
- `GET /api/v1/products` - Lấy danh sách sản phẩm (có pagination, filter, search)
- `GET /api/v1/products/{id}` - Lấy chi tiết sản phẩm
- `POST /api/v1/products` - Tạo sản phẩm mới (ADMIN)
- `PUT /api/v1/products/{id}` - Cập nhật sản phẩm (ADMIN)
- `DELETE /api/v1/products/{id}` - Xóa sản phẩm (ADMIN)
- `POST /api/v1/products/uploads/{id}` - Upload hình ảnh sản phẩm

#### **CategoryController.java**
- `GET /api/v1/categories` - Lấy danh sách danh mục
- `GET /api/v1/categories/{id}` - Lấy chi tiết danh mục
- `POST /api/v1/categories` - Tạo danh mục (ADMIN)
- `PUT /api/v1/categories/{id}` - Cập nhật danh mục (ADMIN)
- `DELETE /api/v1/categories/{id}` - Xóa danh mục (ADMIN)

#### **CartController.java**
- `GET /api/v1/carts/user/{userId}` - Xem giỏ hàng
- `POST /api/v1/carts/add` - Thêm sản phẩm vào giỏ
- `PUT /api/v1/carts/update` - Cập nhật số lượng
- `DELETE /api/v1/carts/{cartDetailId}` - Xóa sản phẩm khỏi giỏ
- `DELETE /api/v1/carts/clear/{userId}` - Xóa toàn bộ giỏ hàng

#### **OrderController.java**
- `GET /api/v1/orders` - Lấy danh sách đơn hàng
- `GET /api/v1/orders/{id}` - Lấy chi tiết đơn hàng
- `POST /api/v1/orders` - Tạo đơn hàng mới
- `PUT /api/v1/orders/{id}` - Cập nhật trạng thái đơn hàng
- `DELETE /api/v1/orders/{id}` - Hủy đơn hàng

#### **ReviewController.java**
- `GET /api/v1/reviews/product/{productId}` - Lấy đánh giá của sản phẩm
- `POST /api/v1/reviews` - Tạo đánh giá mới
- `PUT /api/v1/reviews/{id}` - Cập nhật đánh giá
- `DELETE /api/v1/reviews/{id}` - Xóa đánh giá

#### **StatisticsController.java**
- `GET /api/v1/statistics/revenue` - Thống kê doanh thu
- `GET /api/v1/statistics/top-products` - Sản phẩm bán chạy
- `GET /api/v1/statistics/orders-status` - Thống kê đơn hàng theo trạng thái

#### **FakeDataController.java**
- `POST /api/v1/fake-data/generate` - Tạo dữ liệu giả để test

---

### 3. **entity/** - JPA Entities (Database Models)

#### **User.java**
```
- id: Long (Primary Key)
- username: String
- password: String
- email: String
- fullName: String
- phoneNumber: String
- address: String
- dateOfBirth: LocalDate
- isActive: Boolean
- role: Role (ManyToOne)
- cart: Cart (OneToOne)
- orders: List<Order> (OneToMany)
- reviews: List<Review> (OneToMany)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

#### **Role.java**
```
- id: Long
- name: String (ADMIN, USER)
- description: String
- users: List<User>
```

#### **Product.java**
```
- id: Long
- name: String
- description: String
- price: BigDecimal
- quantity: Integer
- category: Category (ManyToOne)
- images: List<ProductImage> (OneToMany)
- sizes: List<ProductSize> (OneToMany)
- reviews: List<Review> (OneToMany)
- averageRating: Double
- reviewCount: Integer
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

#### **Category.java**
```
- id: Long
- name: String
- description: String
- products: List<Product> (OneToMany)
```

#### **ProductImage.java**
```
- id: Long
- imageUrl: String
- product: Product (ManyToOne)
```

#### **ProductSize.java**
```
- id: Long
- size: String
- additionalPrice: BigDecimal
- product: Product (ManyToOne)
```

#### **Cart.java**
```
- id: Long
- user: User (OneToOne)
- cartDetails: List<CartDetail> (OneToMany)
```

#### **CartDetail.java**
```
- id: Long
- cart: Cart (ManyToOne)
- product: Product (ManyToOne)
- quantity: Integer
- size: String
- price: BigDecimal
```

#### **Order.java**
```
- id: Long
- orderCode: String
- user: User (ManyToOne)
- fullName: String
- email: String
- phoneNumber: String
- address: String
- totalMoney: BigDecimal
- shippingMethod: String
- shippingAddress: String
- paymentMethod: String
- status: OrderStatus (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
- orderDetails: List<OrderDetail> (OneToMany)
- orderDate: LocalDateTime
```

#### **OrderDetail.java**
```
- id: Long
- order: Order (ManyToOne)
- product: Product (ManyToOne)
- quantity: Integer
- price: BigDecimal
- size: String
- totalMoney: BigDecimal
```

#### **Review.java**
```
- id: Long
- product: Product (ManyToOne)
- user: User (ManyToOne)
- rating: Integer (1-5)
- comment: String
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

#### **Token.java**
```
- id: Long
- token: String
- tokenType: String
- expiryDate: LocalDateTime
- revoked: Boolean
- user: User (ManyToOne)
```

---

### 4. **repository/** - Data Access Layer

Các interface JPA Repository extend từ `JpaRepository`:

- **UserRepository**: Quản lý dữ liệu User
  - `findByUsername(String username)`
  - `existsByUsername(String username)`
  - `existsByEmail(String email)`

- **ProductRepository**: Quản lý dữ liệu Product
  - `findByCategory(Category category)`
  - `findByNameContaining(String keyword)`
  - Support Specification cho dynamic queries

- **CategoryRepository**: Quản lý dữ liệu Category
  - `findByName(String name)`

- **CartRepository**: Quản lý dữ liệu Cart
  - `findByUserId(Long userId)`

- **CartDetailRepository**: Quản lý dữ liệu CartDetail
  - `findByCartId(Long cartId)`

- **OrderRepository**: Quản lý dữ liệu Order
  - `findByUserId(Long userId)`
  - `findByStatus(OrderStatus status)`
  - `findByOrderCode(String orderCode)`

- **OrderDetailRepository**: Quản lý dữ liệu OrderDetail

- **ReviewRepository**: Quản lý dữ liệu Review
  - `findByProductId(Long productId)`
  - `findByUserId(Long userId)`

- **RoleRepository**: Quản lý dữ liệu Role
  - `findByName(String name)`

- **TokenRepository**: Quản lý dữ liệu Token
  - `findByToken(String token)`
  - `findAllValidTokensByUser(Long userId)`

- **ProductImageRepository**: Quản lý dữ liệu ProductImage

- **ProductSizeRepository**: Quản lý dữ liệu ProductSize

- **ProductSpecification**: Class hỗ trợ tạo dynamic queries với Specification Pattern

---

### 5. **service/** - Business Logic Layer

#### Cấu trúc Service Layer:
```
service/
├── AuthenticationService.java
├── UserService.java
├── ProductService.java
├── CategoryService.java
├── CartService.java
├── OrderService.java
├── ReviewService.java
├── StatisticsService.java
├── FakeDataService.java
├── BaseCRUDService.java          # Base service với CRUD operations
└── impl/                         # Service implementations
```

#### Chức năng các Service:

**AuthenticationService**
- Xác thực đăng nhập (username/password)
- Tạo JWT token (access token & refresh token)
- Refresh token
- Đăng xuất (revoke token)
- Đăng ký tài khoản mới

**UserService**
- CRUD operations cho User
- Tìm kiếm user theo username, email
- Cập nhật profile
- Quản lý roles

**ProductService**
- CRUD operations cho Product
- Tìm kiếm, filter, sort sản phẩm
- Pagination
- Upload và quản lý hình ảnh sản phẩm
- Quản lý sizes và giá
- Tính toán average rating từ reviews

**CategoryService**
- CRUD operations cho Category
- Lấy danh sách sản phẩm theo category

**CartService**
- Thêm sản phẩm vào giỏ hàng
- Cập nhật số lượng sản phẩm
- Xóa sản phẩm khỏi giỏ
- Tính tổng tiền giỏ hàng
- Xóa toàn bộ giỏ hàng

**OrderService**
- Tạo đơn hàng từ giỏ hàng
- Cập nhật trạng thái đơn hàng
- Hủy đơn hàng
- Xem lịch sử đơn hàng
- Tính toán tổng tiền đơn hàng

**ReviewService**
- Tạo đánh giá sản phẩm
- Cập nhật đánh giá
- Xóa đánh giá
- Lấy danh sách đánh giá theo sản phẩm
- Validate: User chỉ có thể review sản phẩm đã mua

**StatisticsService**
- Thống kê doanh thu theo thời gian
- Top sản phẩm bán chạy
- Thống kê đơn hàng theo trạng thái
- Thống kê theo category
- Tính toán growth rate

**FakeDataService**
- Tạo dữ liệu giả để test
- Generate users, products, categories, orders

---

### 6. **dto/** - Data Transfer Objects

#### Cấu trúc DTO:
```
dto/
├── request/          # DTOs cho request body
│   ├── LoginRequest
│   ├── SignUpRequest
│   ├── RefreshTokenRequest
│   ├── ProductRequest
│   ├── CategoryRequest
│   ├── CartRequest
│   ├── OrderRequest
│   ├── ReviewRequest
│   └── ...
├── response/         # DTOs cho response body
│   ├── AuthResponse
│   ├── UserResponse
│   ├── ProductResponse
│   ├── CategoryResponse
│   ├── CartResponse
│   ├── OrderResponse
│   ├── ReviewResponse
│   ├── ApiResponse (Generic response wrapper)
│   └── ...
└── UserDetailsCustom.java    # Custom UserDetails cho Spring Security
```

**Mục đích sử dụng DTO:**
- Tách biệt Entity và API response/request
- Bảo mật: Không expose trực tiếp entity structure
- Validation: Sử dụng Bean Validation annotations
- Flexibility: Dễ dàng thay đổi API structure mà không ảnh hưởng database

---

### 7. **mapper/** - MapStruct Mappers

Sử dụng **MapStruct** để mapping giữa Entity và DTO:

```java
@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toResponse(Product product);
    Product toEntity(ProductRequest request);
    List<ProductResponse> toResponseList(List<Product> products);
}
```

**Các Mapper:**
- UserMapper
- ProductMapper
- CategoryMapper
- CartMapper
- OrderMapper
- ReviewMapper

**Ưu điểm của MapStruct:**
- Type-safe mapping tại compile time
- Performance cao (không dùng reflection)
- Tích hợp tốt với Lombok
- Code generation tự động

---

### 8. **exception/** - Exception Handling

#### Cấu trúc Exception Package:
- **GlobalExceptionHandler**: Xử lý exceptions toàn cục bằng `@ControllerAdvice`
- **Custom Exceptions**:
  - `ResourceNotFoundException`: Không tìm thấy resource
  - `BadRequestException`: Invalid request
  - `UnauthorizedException`: Không có quyền truy cập
  - `DuplicateException`: Trùng lặp dữ liệu
  - `InvalidTokenException`: Token không hợp lệ

**Response format khi có lỗi:**
```json
{
  "status": "ERROR",
  "message": "Error message",
  "data": null,
  "timestamp": "2026-01-09T10:30:00"
}
```

---

### 9. **enums/** - Enumeration Classes

Các enum được sử dụng trong project:

- **OrderStatus**: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
- **PaymentMethod**: COD (Cash on Delivery), BANK_TRANSFER, CREDIT_CARD
- **TokenType**: BEARER
- **RoleName**: ADMIN, USER

---

### 10. **utils/** - Utility Classes

Các class tiện ích:
- **JwtTokenUtil**: Generate và validate JWT tokens
- **FileUploadUtil**: Xử lý upload và lưu file
- **DateTimeUtil**: Xử lý date/time operations
- **StringUtil**: String manipulation utilities

---

## 🔐 SECURITY & AUTHENTICATION

### JWT Authentication Flow

```
1. User gửi username/password → POST /api/v1/auth/log-in
2. Server verify credentials
3. Server tạo Access Token (2 hours) và Refresh Token (5 days)
4. Client lưu tokens và gửi Access Token trong header cho các requests tiếp theo
   Header: Authorization: Bearer {access_token}
5. Khi Access Token hết hạn → POST /api/v1/auth/refresh-token với Refresh Token
6. Server tạo cặp tokens mới
7. Khi logout → POST /api/v1/auth/log-out (revoke tokens)
```

### Security Configuration

**Public endpoints (không cần authentication):**
- `POST /api/v1/auth/log-in`
- `POST /api/v1/auth/sign-up`
- `POST /api/v1/auth/refresh-token`
- `GET /api/v1/products/**`
- `GET /api/v1/categories/**`
- `/uploads/**` (static resources)

**Protected endpoints:**
- Tất cả endpoints còn lại cần JWT token
- Một số endpoints yêu cầu role ADMIN

**Password Encoding:**
- Sử dụng BCryptPasswordEncoder
- Strength: 10 rounds

---

## 🗄️ DATABASE SCHEMA

### Database Name: `shopclothing`

### Tables:

#### **users**
```sql
- id (PK, AUTO_INCREMENT)
- username (UNIQUE, NOT NULL)
- password (NOT NULL)
- email (UNIQUE)
- full_name
- phone_number
- address
- date_of_birth
- is_active (DEFAULT TRUE)
- role_id (FK → roles.id)
- created_at
- updated_at
```

#### **roles**
```sql
- id (PK)
- name (UNIQUE)
- description
```

#### **products**
```sql
- id (PK)
- name
- description
- price
- quantity
- category_id (FK → categories.id)
- average_rating
- review_count
- created_at
- updated_at
```

#### **categories**
```sql
- id (PK)
- name
- description
```

#### **product_images**
```sql
- id (PK)
- image_url
- product_id (FK → products.id)
```

#### **product_sizes**
```sql
- id (PK)
- size
- additional_price
- product_id (FK → products.id)
```

#### **carts**
```sql
- id (PK)
- user_id (FK → users.id, UNIQUE)
```

#### **cart_details**
```sql
- id (PK)
- cart_id (FK → carts.id)
- product_id (FK → products.id)
- quantity
- size
- price
```

#### **orders**
```sql
- id (PK)
- order_code (UNIQUE)
- user_id (FK → users.id)
- full_name
- email
- phone_number
- address
- total_money
- shipping_method
- shipping_address
- payment_method
- status
- order_date
```

#### **order_details**
```sql
- id (PK)
- order_id (FK → orders.id)
- product_id (FK → products.id)
- quantity
- price
- size
- total_money
```

#### **reviews**
```sql
- id (PK)
- product_id (FK → products.id)
- user_id (FK → users.id)
- rating (1-5)
- comment
- created_at
- updated_at
```

#### **tokens**
```sql
- id (PK)
- token (UNIQUE)
- token_type
- expiry_date
- revoked
- user_id (FK → users.id)
```

### Relationships:
- User **1-1** Cart
- User **1-N** Order
- User **1-N** Review
- User **N-1** Role
- Cart **1-N** CartDetail
- Product **N-1** Category
- Product **1-N** ProductImage
- Product **1-N** ProductSize
- Product **1-N** Review
- Order **1-N** OrderDetail
- CartDetail **N-1** Product
- OrderDetail **N-1** Product

---

## 🔧 CONFIGURATION FILES

### pom.xml - Maven Dependencies

**Major Dependencies:**
- Spring Boot Starter Web (3.5.4)
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Validation
- MySQL Connector
- Lombok (1.18.30)
- MapStruct (1.5.5.Final)
- JJWT (0.12.6) - JWT implementation
- P6Spy (1.12.0) - SQL logging

### application.yaml

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shopclothing
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

api:
  prefix: /api/v1

jwt:
  signerKey: {SECRET_KEY}
  valid-duration: 7200          # 2 hours
  refreshable-duration: 432000  # 5 days
```

---

## 📋 API DOCUMENTATION FILES

Dự án có các file documentation riêng cho từng module:

1. **API_DOCUMENTATION.md**: 
   - Authentication APIs
   - User Management APIs
   - Product Management APIs
   - Category Management APIs

2. **API_CART_DOCUMENTATION.md**:
   - Cart APIs
   - Add/Update/Remove items
   - View cart details

3. **API_ORDER_DOCUMENTATION.md**:
   - Order APIs
   - Create order
   - Update order status
   - Order history

4. **API_STATISTICS_DOCUMENTATION.md**:
   - Revenue statistics
   - Top selling products
   - Order status statistics

---

## 🚀 DEPLOYMENT & RUNNING

### Prerequisites
- Java 17 or higher
- MySQL 8.0+
- Maven 3.6+

### Setup Database
```sql
CREATE DATABASE shopclothing;
```

### Run Application
```bash
# Using Maven wrapper (Windows)
mvnw.cmd spring-boot:run

# Using Maven wrapper (Linux/Mac)
./mvnw spring-boot:run

# Using Maven
mvn spring-boot:run
```

### Build for Production
```bash
mvn clean package
java -jar target/minimart-0.0.1-SNAPSHOT.jar
```

### Default Admin Account
- Username: `admin`
- Password: `admin123`
(Được tạo tự động khi application start lần đầu)

---

## 📂 FILE UPLOAD

### Upload Directory
- Location: `{project-root}/uploads/`
- Allowed extensions: `.jpg`, `.jpeg`, `.png`, `.webp`
- Max file size: Configurable in code
- Access URL: `http://localhost:8081/uploads/{filename}`

### Naming Convention
- Format: `{UUID}_{original-filename}`
- Example: `0cae1a07-d3f2-42fe-9601-cf3ea4c9d7cc_Image-6.1-min.webp`

---

## 🧪 TESTING

### Test Structure
```
src/test/java/
└── com/khangmoihocit/minimart/
    └── MinimartApplicationTests.java
```

### Test Guide
- **TEST_GUIDE_STATISTICS.md**: Hướng dẫn test Statistics APIs

### Run Tests
```bash
mvn test
```

---

## 📊 LOGGING & MONITORING

### P6Spy - SQL Logging
Dự án sử dụng P6Spy để log tất cả SQL queries được execute:
- Giúp debug và optimize queries
- Log format: Readable SQL with parameters
- Configuration: Automatic với spring-boot-starter

### Application Logs
- Spring Boot default logging
- Log level có thể config trong application.yaml

---

## 🔄 API RESPONSE FORMAT

### Success Response
```json
{
  "status": "SUCCESS",
  "message": "Operation successful",
  "data": {
    // actual data object
  }
}
```

### Error Response
```json
{
  "status": "ERROR",
  "message": "Error description",
  "data": null,
  "timestamp": "2026-01-09T10:30:00"
}
```

### Pagination Response
```json
{
  "status": "SUCCESS",
  "message": "Get products successfully",
  "data": {
    "content": [...],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 100,
    "totalPages": 10
  }
}
```

---

## 🎯 FEATURES SUMMARY

### ✅ Implemented Features:
1. **Authentication & Authorization**
   - JWT-based authentication
   - Role-based access control (ADMIN, USER)
   - Secure password encryption
   - Token refresh mechanism

2. **User Management**
   - User registration
   - Profile management
   - User list with pagination
   - Role assignment

3. **Product Management**
   - CRUD operations
   - Multiple images per product
   - Product sizes with additional pricing
   - Search, filter, sort
   - Pagination

4. **Category Management**
   - CRUD operations
   - Product categorization

5. **Shopping Cart**
   - Add to cart
   - Update quantities
   - Remove items
   - Clear cart

6. **Order Management**
   - Create order from cart
   - Order tracking
   - Status management
   - Order history

7. **Review System**
   - Rate products (1-5 stars)
   - Write reviews
   - Edit/Delete own reviews
   - Calculate average ratings

8. **Statistics & Analytics**
   - Revenue reports
   - Top selling products
   - Order status distribution

9. **File Upload**
   - Product image upload
   - Static file serving

10. **Fake Data Generation**
    - Test data generator
    - Useful for development/testing

---

## 🛠️ BEST PRACTICES APPLIED

### 1. Code Organization
- Clear separation of concerns
- Layered architecture
- Package by feature

### 2. Security
- JWT authentication
- Password encryption
- Input validation
- SQL injection prevention (JPA)

### 3. Data Validation
- Bean Validation annotations
- Custom validators
- Global exception handling

### 4. Code Quality
- Lombok for boilerplate reduction
- MapStruct for safe mapping
- Meaningful naming conventions
- Comments where necessary

### 5. API Design
- RESTful conventions
- Consistent response format
- Proper HTTP status codes
- API versioning (`/api/v1`)

### 6. Database
- JPA/Hibernate for ORM
- Proper relationships
- Indexes on foreign keys
- Timestamp tracking

---

## 📝 NOTES

### Development Tips:
1. Sử dụng file `.sql` để tạo database schema nếu cần
2. Check API documentation files để biết chi tiết về các endpoints
3. Sử dụng FakeDataController để generate test data
4. P6Spy sẽ show SQL queries trong console, useful cho debugging

### Common Issues:
1. **Database connection error**: Check MySQL service và credentials trong application.yaml
2. **Port already in use**: Change port trong application.yaml
3. **JWT signature error**: Check signerKey trong application.yaml
4. **File upload fails**: Check uploads directory permissions

---

## 📞 CONTACT & SUPPORT

**Developer**: khangmoihocit  
**Project**: Mini Mart Backend  
**Version**: 0.0.1-SNAPSHOT

---

## 📄 LICENSE

[Thông tin license sẽ được thêm sau]

---

**Ngày tạo tài liệu**: 09/01/2026  
**Phiên bản tài liệu**: 1.0

---

*Tài liệu này mô tả chi tiết cấu trúc và kiến trúc của dự án Mini Mart Backend. Để biết thêm chi tiết về API endpoints, vui lòng tham khảo các file API documentation riêng.*

