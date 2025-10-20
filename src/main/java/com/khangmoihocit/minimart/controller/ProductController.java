package com.khangmoihocit.minimart.controller;

import com.khangmoihocit.minimart.dto.request.ProductRequest;
import com.khangmoihocit.minimart.dto.response.ApiResponse;
import com.khangmoihocit.minimart.dto.response.ProductResponse;
import com.khangmoihocit.minimart.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("${api.prefix}/products")
public class ProductController {
    ProductService productService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ProductResponse> createProduct(@Valid @ModelAttribute ProductRequest request) {
        ProductResponse result = productService.createProductWithImages(request);
        return ApiResponse.<ProductResponse>builder()
                .result(result)
                .message("Tạo sản phẩm thành công!")
                .build();
    }

    @GetMapping
    ApiResponse<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> result = productService.findAll();
        return ApiResponse.<List<ProductResponse>>builder()
                .result(result)
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ProductResponse> updateProduct(@PathVariable String id, @Valid @ModelAttribute ProductRequest request) {
        ProductResponse result = productService.update(id, request);
        return ApiResponse.<ProductResponse>builder()
                .result(result)
                .message("Cập nhật sản phẩm thành công!")
                .build();
    }

    @PostMapping("/update-images/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ProductResponse> updateProductImages(@PathVariable String id, @ModelAttribute List<MultipartFile> files) {
        ProductResponse result = productService.updateProductImages(id, files);
        return ApiResponse.<ProductResponse>builder()
                .result(result)
                .message("Cập nhật hình ảnh sản phẩm thành công!")
                .build();
    }
}
