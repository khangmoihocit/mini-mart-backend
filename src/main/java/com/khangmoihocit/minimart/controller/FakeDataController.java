package com.khangmoihocit.minimart.controller;

import com.khangmoihocit.minimart.dto.response.ApiResponse;
import com.khangmoihocit.minimart.service.FakeDataService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("${api.prefix}/fake-data")
public class FakeDataController {

    FakeDataService fakeDataService;

    /**
     * Generate fake products with categories, sizes, and images
     *
     * @param count Number of products to generate (default: 100)
     * @return Success message with count
     */
    @PostMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> generateFakeProducts(
            @RequestParam(defaultValue = "100") int count) {

        log.info("Received request to generate {} fake products", count);

        // Validate count
        if (count < 1 || count > 500) {
            return ApiResponse.<String>builder()
                    .code(1001)
                    .message("Số lượng phải từ 1 đến 500")
                    .build();
        }

        String result = fakeDataService.generateFakeProducts(count);

        return ApiResponse.<String>builder()
                .result(result)
                .message("Tạo fake data thành công!")
                .build();
    }

    /**
     * Quick endpoint to generate 100 products (default)
     */
    @PostMapping("/products/quick")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> generateQuick() {
        log.info("Quick generate 100 fake products");

        String result = fakeDataService.generateFakeProducts(100);

        return ApiResponse.<String>builder()
                .result(result)
                .message("Tạo 100 fake products thành công!")
                .build();
    }

    /**
     * Generate fake users
     *
     * @param count Number of users to generate (default: 50)
     * @return Success message with count
     */
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> generateFakeUsers(
            @RequestParam(defaultValue = "50") int count) {

        log.info("Received request to generate {} fake users", count);

        // Validate count
        if (count < 1 || count > 500) {
            return ApiResponse.<String>builder()
                    .code(1001)
                    .message("Số lượng phải từ 1 đến 500")
                    .build();
        }

        String result = fakeDataService.generateFakeUsers(count);

        return ApiResponse.<String>builder()
                .result(result)
                .message("Tạo fake users thành công!")
                .build();
    }

    /**
     * Generate fake orders
     *
     * @param count Number of orders to generate (default: 100)
     * @return Success message with count
     */
    @PostMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> generateFakeOrders(
            @RequestParam(defaultValue = "100") int count) {

        log.info("Received request to generate {} fake orders", count);

        // Validate count
        if (count < 1 || count > 1000) {
            return ApiResponse.<String>builder()
                    .code(1001)
                    .message("Số lượng phải từ 1 đến 1000")
                    .build();
        }

        String result = fakeDataService.generateFakeOrders(count);

        return ApiResponse.<String>builder()
                .result(result)
                .message("Tạo fake orders thành công!")
                .build();
    }

    /**
     * Generate complete fake data set (users, products, orders)
     */
    @PostMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, String>> generateAllFakeData(
            @RequestParam(defaultValue = "50") int userCount,
            @RequestParam(defaultValue = "100") int productCount,
            @RequestParam(defaultValue = "200") int orderCount) {

        log.info("Generating complete fake data set: {} users, {} products, {} orders",
                userCount, productCount, orderCount);

        Map<String, String> results = new HashMap<>();

        try {
            // 1. Generate Users first
            String userResult = fakeDataService.generateFakeUsers(userCount);
            results.put("users", userResult);

            // 2. Generate Products
            String productResult = fakeDataService.generateFakeProducts(productCount);
            results.put("products", productResult);

            // 3. Generate Orders (requires users and products)
            String orderResult = fakeDataService.generateFakeOrders(orderCount);
            results.put("orders", orderResult);

            return ApiResponse.<Map<String, String>>builder()
                    .result(results)
                    .message("Tạo toàn bộ fake data thành công!")
                    .build();

        } catch (Exception e) {
            log.error("Error generating complete fake data", e);
            return ApiResponse.<Map<String, String>>builder()
                    .code(1002)
                    .message("Lỗi khi tạo fake data: " + e.getMessage())
                    .result(results)
                    .build();
        }
    }


}

