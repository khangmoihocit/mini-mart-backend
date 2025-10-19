package com.khangmoihocit.minimart.service;

import com.khangmoihocit.minimart.dto.request.ProductRequest;
import com.khangmoihocit.minimart.dto.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService extends BaseCRUDService<ProductResponse, ProductRequest> {
    ProductResponse createProductWithImages(ProductRequest request);

    ProductResponse updateProductImages(String id, List<MultipartFile> files);
}
