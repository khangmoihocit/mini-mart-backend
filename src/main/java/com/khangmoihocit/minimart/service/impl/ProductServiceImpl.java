package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.request.ProductRequest;
import com.khangmoihocit.minimart.dto.response.ProductResponse;
import com.khangmoihocit.minimart.entity.Category;
import com.khangmoihocit.minimart.entity.Product;
import com.khangmoihocit.minimart.entity.ProductImage;
import com.khangmoihocit.minimart.enums.ErrorCode;
import com.khangmoihocit.minimart.exception.AppException;
import com.khangmoihocit.minimart.exception.OurException;
import com.khangmoihocit.minimart.mapper.ProductMapper;
import com.khangmoihocit.minimart.repository.CategoryRepository;
import com.khangmoihocit.minimart.repository.ProductImageRepository;
import com.khangmoihocit.minimart.repository.ProductRepository;
import com.khangmoihocit.minimart.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ProductMapper productMapper;
    ProductImageRepository productImageRepository;
    Path root = Paths.get("uploads");

    @Override
    @Transactional
    public ProductResponse createProductWithImages(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = productMapper.toProduct(request);
        product.setCategory(category);

        product = productRepository.save(product);

        ProductResponse productResponse = productMapper.toProductResponse(product);
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : request.getImages()) {
                if(file.isEmpty()){
                    continue; // Bỏ qua file rỗng
                }

                ProductImage productImage = saveImageForProduct(file, product);
                productImages.add(productImage);
            }
            productImageRepository.saveAll(productImages);
            List<String> imageUrls = productImages.stream().map(ProductImage::getImageUrl).toList();
            productResponse.setImages(imageUrls);
        }

        return productResponse;
    }

    private ProductImage saveImageForProduct(MultipartFile file, Product product) {
        try {
            // Tạo thư mục 'uploads' nếu chưa có
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            // Tạo tên file duy nhất để tránh trùng lặp
            String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;

            // Đường dẫn đầy đủ đến file
            Path destinationFile = this.root.resolve(uniqueFilename).normalize().toAbsolutePath();

            // Sao chép file vào thư mục đích
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            return ProductImage.builder()
                    .product(product)
                    .imageUrl(uniqueFilename)
                    .build();

        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    @Override
    public ProductResponse save(ProductRequest productRequest) {
        return null;
    }

    @Override
    public ProductResponse update(String id, ProductRequest productRequest) {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public ProductResponse findById(String id) {
        return null;
    }

    @Override
    public List<ProductResponse> findAll() {
        return List.of();
    }
}
