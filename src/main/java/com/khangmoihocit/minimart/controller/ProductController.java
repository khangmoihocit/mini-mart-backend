package com.khangmoihocit.minimart.controller;

import com.khangmoihocit.minimart.dto.request.ProductRequest;
import com.khangmoihocit.minimart.dto.request.ProductSearchRequest;
import com.khangmoihocit.minimart.dto.request.UpdateImageRequest;
import com.khangmoihocit.minimart.dto.response.ApiResponse;
import com.khangmoihocit.minimart.dto.response.ProductResponse;
import com.khangmoihocit.minimart.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @PostMapping(value = "/update-images/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ProductResponse> updateProductImages(@PathVariable String id, @ModelAttribute UpdateImageRequest request) {
        ProductResponse result = productService.updateProductImages(id, request.getFiles(), request.getKeepImageIds());
        return ApiResponse.<ProductResponse>builder()
                .result(result)
                .message("Cập nhật hình ảnh sản phẩm thành công!")
                .build();
    }

    @GetMapping("/search")
//    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Page<ProductResponse>> searchProduct(@RequestParam(name="pageNo", defaultValue = "1") int pageNo,
                                                   @RequestParam(name="pageSize", defaultValue = "5") int pageSize,
                                                   @RequestParam(name = "keyword", required = false) String keyword) {
        Page<ProductResponse> products = productService.searchProduct(pageNo, pageSize, keyword);
        return ApiResponse.<Page<ProductResponse>>builder()
                .result(products)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteById(@PathVariable String id){
        productService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Xóa sản phẩm thành công!")
                .build();
    }

    @GetMapping("/advanced-search")
    ApiResponse<Page<ProductResponse>> advancedSearch(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) String categoryId,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "pageNo", required = false) Integer pageNo,
            @RequestParam(name = "pageSize", required = false) Integer pageSize) {

        ProductSearchRequest searchRequest = ProductSearchRequest.builder()
                .keyword(keyword)
                .categoryId(categoryId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .sortBy(sortBy)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .build();

        Page<ProductResponse> products = productService.advancedSearch(searchRequest);
        return ApiResponse.<Page<ProductResponse>>builder()
                .result(products)
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<ProductResponse> getProductById(@PathVariable String id) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.findById(id))
                .build();
    }
}
