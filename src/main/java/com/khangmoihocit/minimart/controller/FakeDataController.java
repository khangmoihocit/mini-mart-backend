package com.khangmoihocit.minimart.controller;

import com.khangmoihocit.minimart.dto.response.ApiResponse;
import com.khangmoihocit.minimart.repository.CategoryRepository;
import com.khangmoihocit.minimart.repository.ProductImageRepository;
import com.khangmoihocit.minimart.repository.ProductRepository;
import com.khangmoihocit.minimart.repository.ProductSizeRepository;
import com.khangmoihocit.minimart.service.FakeDataService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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


}

