package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.entity.*;
import com.khangmoihocit.minimart.enums.OrderStatus;
import com.khangmoihocit.minimart.repository.*;
import com.khangmoihocit.minimart.service.FakeDataService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FakeDataServiceImpl implements FakeDataService {

    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ProductSizeRepository productSizeRepository;
    ProductImageRepository productImageRepository;
    UserRepository userRepository;
    RoleRepository roleRepository;
    OrderRepository orderRepository;
    OrderDetailRepository orderDetailRepository;

    // Danh sách tên sản phẩm mẫu
    private static final String[] PRODUCT_PREFIXES = {
        "Áo thun", "Áo polo", "Áo sơ mi", "Áo khoác", "Áo hoodie",
        "Quần jean", "Quần kaki", "Quần short", "Quần thể thao", "Quần jogger",
        "Váy", "Đầm", "Set đồ", "Áo vest", "Áo blazer"
    };

    private static final String[] PRODUCT_STYLES = {
        "Basic", "Premium", "Cao cấp", "Thời trang", "Hàn Quốc",
        "Nhật Bản", "Vintage", "Streetwear", "Casual", "Formal",
        "Sport", "Oversized", "Slim fit", "Regular fit", "Unisex"
    };

    private static final String[] MATERIALS = {
        "cotton 100%", "cotton pha", "kaki", "jean", "linen",
        "thun co giãn", "polyester", "vải thô", "da lộn", "nỉ"
    };

    private static final String[] COLORS = {
        "trắng", "đen", "xám", "navy", "be",
        "xanh dương", "xanh lá", "đỏ", "hồng", "vàng",
        "nâu", "tím", "cam", "xanh ngọc"
    };

    // Category names
    private static final String[] CATEGORIES = {
        "Áo thun", "Áo polo", "Áo sơ mi", "Áo khoác",
        "Quần jean", "Quần kaki", "Quần short", "Váy đầm"
    };

    // Sizes
    private static final String[] SIZES = {"S", "M", "L", "XL", "XXL"};

    // Image URLs
    private static final String[] IMAGE_URLS = {
        "Image-1.2-min.webp",
            "Image-2.4-min.webp",
            "Image-3.1-min.webp",
            "Image-4.1-min.webp",
            "Image-5.3-min.webp",
            "Image-6.1-min.webp",
            "Image-8.1-min.webp",
            "Image-9.1-min.webp",
            "Image-11.1-min.webp",
            "Image-13.1-min.webp",
            "Image-14.1-min.webp",
            "Image-15.1-min.webp",
            "Image-16.1-min.webp"
    };

    @Override
    @Transactional
    public String generateFakeProducts(int count) {
        log.info("Starting to generate {} fake products", count);

        try {
            // 1. Tạo categories nếu chưa có
            Map<String, Category> categoryMap = createCategories();
            log.info("Created/Found {} categories", categoryMap.size());

            // 2. Tạo products
            int created = 0;
            Random random = new Random();

            for (int i = 0; i < count; i++) {
                try {
                    // Tạo tên sản phẩm
                    String productName = generateProductName(random);

                    // Chọn category ngẫu nhiên
                    String categoryName = CATEGORIES[random.nextInt(CATEGORIES.length)];
                    Category category = categoryMap.get(categoryName);

                    // Tạo giá
                    BigDecimal price = generatePrice(random);
                    BigDecimal salePrice = random.nextBoolean() ? generateSalePrice(price, random) : null;

                    // Tạo description
                    String description = generateDescription(productName, random);

                    // Tạo stock quantity
                    int stockQuantity = random.nextInt(200) + 50; // 50-250

                    // Tạo Product
                    Product product = Product.builder()
                            .name(productName)
                            .price(price)
                            .salePrice(salePrice)
                            .description(description)
                            .stockQuantity(stockQuantity)
                            .category(category)
                            .build();

                    product = productRepository.save(product);

                    // Tạo sizes cho product
                    createProductSizes(product, random);

                    // Tạo images cho product
                    createProductImages(product, random);

                    created++;

                    if ((i + 1) % 10 == 0) {
                        log.info("Created {} products", i + 1);
                    }

                } catch (Exception e) {
                    log.error("Error creating product {}: {}", i, e.getMessage());
                }
            }

            String message = String.format("Successfully created %d products with categories, sizes, and images", created);
            log.info(message);
            return message;

        } catch (Exception e) {
            log.error("Error generating fake data", e);
            throw new RuntimeException("Failed to generate fake data: " + e.getMessage());
        }
    }

    @Override
    public void deleteAllFakeProducts() {

    }

    private Map<String, Category> createCategories() {
        Map<String, Category> categoryMap = new HashMap<>();

        for (String categoryName : CATEGORIES) {
            Category category = categoryRepository.findByName(categoryName)
                    .orElseGet(() -> {
                        Category newCategory = Category.builder()
                                .name(categoryName)
                                .build();
                        return categoryRepository.save(newCategory);
                    });
            categoryMap.put(categoryName, category);
        }

        return categoryMap;
    }

    private String generateProductName(Random random) {
        String prefix = PRODUCT_PREFIXES[random.nextInt(PRODUCT_PREFIXES.length)];
        String style = PRODUCT_STYLES[random.nextInt(PRODUCT_STYLES.length)];
        String color = COLORS[random.nextInt(COLORS.length)];

        return String.format("%s %s %s", prefix, style, color);
    }

    private BigDecimal generatePrice(Random random) {
        // Giá từ 99,000 đến 999,000
        int basePrice = (random.nextInt(90) + 10) * 10000 + 9000; // 99k, 109k, 119k, ... 999k
        return BigDecimal.valueOf(basePrice);
    }

    private BigDecimal generateSalePrice(BigDecimal price, Random random) {
        // Giảm giá 10-50%
        double discountPercent = 10 + random.nextDouble() * 40; // 10-50%
        double salePrice = price.doubleValue() * (100 - discountPercent) / 100;
        // Làm tròn đến 1000
        salePrice = Math.floor(salePrice / 1000) * 1000;
        return BigDecimal.valueOf(salePrice);
    }

    private String generateDescription(String productName, Random random) {
        String material = MATERIALS[random.nextInt(MATERIALS.length)];

        List<String> features = Arrays.asList(
            "Form dáng đẹp, vừa vặn",
            "Chất liệu " + material + " cao cấp",
            "Thoáng mát, thấm hút mồ hôi tốt",
            "Dễ dàng phối đồ",
            "Thiết kế hiện đại, trẻ trung",
            "Co giãn tốt, thoải mái khi vận động",
            "Không phai màu, không co rút sau khi giặt",
            "Phù hợp cho cả nam và nữ"
        );

        // Chọn 3-5 features ngẫu nhiên
        Collections.shuffle(features, random);
        int numFeatures = 3 + random.nextInt(3);

        StringBuilder description = new StringBuilder();
        description.append(productName).append("\n\n");
        description.append("✨ Đặc điểm nổi bật:\n");

        for (int i = 0; i < numFeatures && i < features.size(); i++) {
            description.append("• ").append(features.get(i)).append("\n");
        }

        description.append("\n📦 Thông tin sản phẩm:\n");
        description.append("• Chất liệu: ").append(material).append("\n");
        description.append("• Xuất xứ: Việt Nam\n");
        description.append("• Bảo hành: 30 ngày đổi trả\n");

        return description.toString();
    }

    private void createProductSizes(Product product, Random random) {
        // 80% sản phẩm có sizes, 20% không có
        if (random.nextDouble() < 0.8) {
            // Chọn số lượng sizes (3-5 sizes)
            int numSizes = 3 + random.nextInt(3);

            for (int i = 0; i < numSizes && i < SIZES.length; i++) {
                int quantity = random.nextInt(50) + 10; // 10-60 cho mỗi size

                ProductSize size = ProductSize.builder()
                        .sizeName(SIZES[i])
                        .quantity(quantity)
                        .product(product)
                        .build();

                productSizeRepository.save(size);
            }
        }
    }

    private void createProductImages(Product product, Random random) {
        // Mỗi product có 1-2 ảnh
        int numImages = 1 + random.nextInt(2);

        for (int i = 0; i < numImages; i++) {
            String imageUrl = IMAGE_URLS[random.nextInt(IMAGE_URLS.length)];

            ProductImage image = ProductImage.builder()
                    .imageUrl(imageUrl)
                    .product(product)
                    .build();

            productImageRepository.save(image);
        }
    }

    // ========================= FAKE USER GENERATION =========================

    private static final String[] FIRST_NAMES = {
        "Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ", "Đặng",
        "Bùi", "Đỗ", "Hồ", "Ngô", "Dương", "Lý"
    };

    private static final String[] MIDDLE_NAMES = {
        "Văn", "Thị", "Hữu", "Minh", "Quốc", "Thanh", "Hoàng", "Đức", "Anh", "Tuấn"
    };

    private static final String[] LAST_NAMES = {
        "An", "Bình", "Chi", "Dũng", "Hải", "Hùng", "Khoa", "Linh", "Long", "Mai",
        "Nam", "Phong", "Quân", "Sơn", "Tâm", "Tùng", "Việt", "Yến", "Hương", "Thảo"
    };

    private static final String[] CITIES = {
        "Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Hải Phòng", "Cần Thơ",
        "Biên Hòa", "Nha Trang", "Huế", "Vũng Tàu", "Buôn Ma Thuột"
    };

    private static final String[] DISTRICTS = {
        "Quận 1", "Quận 2", "Quận 3", "Quận 4", "Quận 5",
        "Quận Tân Bình", "Quận Bình Thạnh", "Quận Gò Vấp", "Quận Thủ Đức"
    };

    private static final String[] STREETS = {
        "Lê Lợi", "Nguyễn Huệ", "Trần Hưng Đạo", "Hai Bà Trưng", "Điện Biên Phủ",
        "Võ Văn Kiệt", "Lý Thường Kiệt", "Nguyễn Thái Học", "Pasteur", "Cách Mạng Tháng 8"
    };

    @Override
    @Transactional
    public String generateFakeUsers(int count) {
        log.info("Starting to generate {} fake users", count);

        try {
            // Lấy role USER
            Role userRole = roleRepository.findById("USER")
                    .orElseThrow(() -> new RuntimeException("Role USER not found"));

            int created = 0;
            Random random = new Random();

            for (int i = 0; i < count; i++) {
                try {
                    String fullName = generateFullName(random);
                    String email = generateEmail(fullName, i, random);
                    String phoneNumber = generatePhoneNumber(random);
                    String address = generateAddress(random);
                    LocalDate dateOfBirth = generateDateOfBirth(random);

                    // Check if email already exists
                    if (userRepository.findByEmail(email).isPresent()) {
                        email = "user" + System.currentTimeMillis() + random.nextInt(1000) + "@example.com";
                    }

                    User user = User.builder()
                            .fullName(fullName)
                            .email(email)
                            .phoneNumber(phoneNumber)
                            .address(address)
                            .password("$2a$10$vKhqhcKPqVb0.LCyxPnq0.MQVDt4xKx3zqVQe0YG8xKOl5kF6gXqS") // password: user123
                            .isActive(true)
                            .dateOfBirth(dateOfBirth)
                            .role(userRole)
                            .build();

                    userRepository.save(user);
                    created++;

                    if ((i + 1) % 10 == 0) {
                        log.info("Created {} users", i + 1);
                    }

                } catch (Exception e) {
                    log.error("Error creating user {}: {}", i, e.getMessage());
                }
            }

            String message = String.format("Successfully created %d fake users", created);
            log.info(message);
            return message;

        } catch (Exception e) {
            log.error("Error generating fake users", e);
            throw new RuntimeException("Failed to generate fake users: " + e.getMessage());
        }
    }

    private String generateFullName(Random random) {
        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String middleName = MIDDLE_NAMES[random.nextInt(MIDDLE_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        return firstName + " " + middleName + " " + lastName;
    }

    private String generateEmail(String fullName, int index, Random random) {
        String[] parts = fullName.toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .split(" ");

        String username = parts[parts.length - 1] + parts[0].charAt(0);
        return username + index + random.nextInt(100) + "@example.com";
    }

    private String generatePhoneNumber(Random random) {
        String[] prefixes = {"09", "08", "07", "03"};
        String prefix = prefixes[random.nextInt(prefixes.length)];
        int number = 10000000 + random.nextInt(90000000);
        return prefix + number;
    }

    private String generateAddress(Random random) {
        int houseNumber = random.nextInt(500) + 1;
        String street = STREETS[random.nextInt(STREETS.length)];
        String district = DISTRICTS[random.nextInt(DISTRICTS.length)];
        String city = CITIES[random.nextInt(CITIES.length)];
        return houseNumber + " " + street + ", " + district + ", " + city;
    }

    private LocalDate generateDateOfBirth(Random random) {
        int year = 1970 + random.nextInt(35); // 1970-2004
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);
        return LocalDate.of(year, month, day);
    }

    // ========================= FAKE ORDER GENERATION =========================

    private static final String[] PAYMENT_METHODS = {
        "COD", "Banking", "Momo", "ZaloPay", "VNPay"
    };

    private static final String[] SHIPPING_METHODS = {
        "Giao hàng tiêu chuẩn", "Giao hàng nhanh", "Giao hàng hỏa tốc"
    };

    private static final String[] ORDER_NOTES = {
        "Giao hàng giờ hành chính",
        "Gọi trước khi giao",
        "Để hàng ở bảo vệ",
        "Giao hàng buổi chiều",
        null, null, null // 30% không có note
    };

    @Override
    @Transactional
    public String generateFakeOrders(int count) {
        log.info("Starting to generate {} fake orders", count);

        try {
            // Lấy danh sách users
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                return "No users found. Please generate users first!";
            }

            // Lấy danh sách products
            List<Product> products = productRepository.findAll();
            if (products.isEmpty()) {
                return "No products found. Please generate products first!";
            }

            int created = 0;
            Random random = new Random();

            for (int i = 0; i < count; i++) {
                try {
                    // Chọn user ngẫu nhiên
                    User user = users.get(random.nextInt(users.size()));

                    // Tạo thông tin đơn hàng
                    String fullName = user.getFullName();
                    String email = user.getEmail();
                    String phoneNumber = user.getPhoneNumber();
                    String shippingAddress = user.getAddress() != null ? user.getAddress() : generateAddress(random);
                    String note = ORDER_NOTES[random.nextInt(ORDER_NOTES.length)];
                    String paymentMethod = PAYMENT_METHODS[random.nextInt(PAYMENT_METHODS.length)];
                    String shippingMethod = SHIPPING_METHODS[random.nextInt(SHIPPING_METHODS.length)];

                    // Chọn trạng thái ngẫu nhiên với phân bổ hợp lý
                    OrderStatus status = generateOrderStatus(random);

                    // Tạo order date trong 90 ngày qua
                    LocalDateTime orderDate = LocalDateTime.now().minusDays(random.nextInt(90));

                    // Tạo Order
                    Order order = Order.builder()
                            .user(user)
                            .fullName(fullName)
                            .email(email)
                            .phoneNumber(phoneNumber)
                            .shippingAddress(shippingAddress)
                            .note(note)
                            .status(status)
                            .totalMoney(BigDecimal.ZERO) // Sẽ cập nhật sau
                            .shippingMethod(shippingMethod)
                            .paymentMethod(paymentMethod)
                            .build();

                    order = orderRepository.save(order);

                    // Tạo order details (2-5 sản phẩm mỗi đơn)
                    int numProducts = 2 + random.nextInt(4);
                    BigDecimal totalMoney = BigDecimal.ZERO;

                    Set<String> usedProductIds = new HashSet<>();

                    for (int j = 0; j < numProducts; j++) {
                        Product product = products.get(random.nextInt(products.size()));

                        // Tránh trùng sản phẩm trong cùng đơn hàng
                        if (usedProductIds.contains(product.getId())) {
                            continue;
                        }
                        usedProductIds.add(product.getId());

                        int quantity = 1 + random.nextInt(3); // 1-3 sản phẩm
                        BigDecimal price = product.getSalePrice() != null ? product.getSalePrice() : product.getPrice();
                        BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(quantity));

                        // Lấy size ngẫu nhiên nếu có
                        String sizeName = null;
                        if (!product.getSizes().isEmpty()) {
                            List<ProductSize> sizes = new ArrayList<>(product.getSizes());
                            sizeName = sizes.get(random.nextInt(sizes.size())).getSizeName();
                        }

                        OrderDetail orderDetail = OrderDetail.builder()
                                .order(order)
                                .product(product)
                                .price(price)
                                .numberOfProducts(quantity)
                                .totalMoney(itemTotal)
                                .sizeName(sizeName)
                                .build();

                        orderDetailRepository.save(orderDetail);
                        totalMoney = totalMoney.add(itemTotal);
                    }

                    // Cập nhật total money cho order
                    order.setTotalMoney(totalMoney);
                    orderRepository.save(order);

                    created++;

                    if ((i + 1) % 10 == 0) {
                        log.info("Created {} orders", i + 1);
                    }

                } catch (Exception e) {
                    log.error("Error creating order {}: {}", i, e.getMessage());
                }
            }

            String message = String.format("Successfully created %d fake orders with details", created);
            log.info(message);
            return message;

        } catch (Exception e) {
            log.error("Error generating fake orders", e);
            throw new RuntimeException("Failed to generate fake orders: " + e.getMessage());
        }
    }

    private OrderStatus generateOrderStatus(Random random) {
        int rand = random.nextInt(100);
        if (rand < 50) return OrderStatus.DELIVERED; // 50%
        if (rand < 65) return OrderStatus.PENDING;   // 15%
        if (rand < 80) return OrderStatus.PROCESSING; // 15%
        if (rand < 90) return OrderStatus.SHIPPED;    // 10%
        return OrderStatus.CANCELLED;                 // 10%
    }
}

