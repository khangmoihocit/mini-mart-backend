package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.request.ProductRequest;
import com.khangmoihocit.minimart.dto.response.ProductImageResponse;
import com.khangmoihocit.minimart.dto.response.ProductResponse;
import com.khangmoihocit.minimart.entity.Category;
import com.khangmoihocit.minimart.entity.Product;
import com.khangmoihocit.minimart.entity.ProductImage;
import com.khangmoihocit.minimart.enums.ErrorCode;
import com.khangmoihocit.minimart.exception.AppException;
import com.khangmoihocit.minimart.exception.OurException;
import com.khangmoihocit.minimart.mapper.ProductImageMapper;
import com.khangmoihocit.minimart.mapper.ProductMapper;
import com.khangmoihocit.minimart.repository.CategoryRepository;
import com.khangmoihocit.minimart.repository.ProductImageRepository;
import com.khangmoihocit.minimart.repository.ProductRepository;
import com.khangmoihocit.minimart.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ProductImageRepository productImageRepository;
    ProductMapper productMapper;
    ProductImageMapper productImageMapper;
    Path root = Paths.get("uploads");

    @Override
    @Transactional
    public ProductResponse createProductWithImages(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = productMapper.toProduct(request);
        product.setCategory(category);
        product = productRepository.save(product);

        List<ProductImage> savedImages = processAndSaveImages(request.getImages(), product);

        ProductResponse productResponse = productMapper.toProductResponse(product);
        List<ProductImageResponse> imageResponses = savedImages.stream()
                .map(productImageMapper::toProductImageResponse)
                .toList();
        productResponse.setImages(imageResponses);

        return productResponse;
    }


    @Override
    @Transactional
    public ProductResponse update(String id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        productMapper.updateProduct(productRequest, product);

        if (!productRequest.getCategoryId().equals(product.getCategory().getId())) {
            Category newCategory = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            product.setCategory(newCategory);
        }
        return productMapper.toProductResponse(product);
    }


    @Override
    @Transactional
    public ProductResponse updateProductImages(String id, List<MultipartFile> files) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        //lấy tất cả ảnh sản phẩm và xóa file ảnh trong thư mục
        List<ProductImage> existingImages = productImageRepository.findByProductId(id);
        existingImages.forEach(image -> deleteImageFile(image.getImageUrl()));
        productImageRepository.deleteAll(existingImages);


        List<ProductImage> savedImages = processAndSaveImages(files, product);

        ProductResponse productResponse = productMapper.toProductResponse(product);
        List<ProductImageResponse> imageResponses = savedImages.stream()
                .map(productImageMapper::toProductImageResponse)
                .toList();
        productResponse.setImages(imageResponses);

        return productResponse;
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new AppException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    //lưu nhiều ảnh vào thư mục và db
    private List<ProductImage> processAndSaveImages(List<MultipartFile> files, Product product) {
        if (files == null || files.isEmpty()) {
            return new ArrayList<>();
        }

        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile file : files) {
            validateImageFile(file);
            if (!file.isEmpty()) {
                ProductImage productImage = saveImageForProduct(file, product);
                productImages.add(productImage);
            }
        }

        return productImageRepository.saveAll(productImages);
    }

    //lưu ảnh vào thư mục và trả về entity để lưu vào db
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

    private void deleteImageFile(String filename) {
        if (filename == null || filename.isEmpty()) return;
        try {
            Path file = root.resolve(filename);
            Files.deleteIfExists(file);
            log.info("Đã xóa file ảnh: {}", filename);
        } catch (IOException e) {
            log.error("Không thể xóa file ảnh: {}", filename, e);
        }
    }

    @Override
    public void delete(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        productRepository.deleteById(id);
    }

    @Override
    public ProductResponse findById(String id) {
        return productRepository.findById(id)
                .map(productMapper::toProductResponse)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override //đã tối ưu còn 2 query
    public List<ProductResponse> findAll() {
        List<Product> products = productRepository.findAllWithCategory(); //query 1
        List<String> productIds = products.stream().map(Product::getId).toList();
        List<ProductImage> productImages = productImageRepository.findByProductIdIn(productIds); //query 2

        // Nhóm hình ảnh theo productId
        Map<String, List<ProductImage>> imagesByProductId = new HashMap<>();
        for (ProductImage image : productImages){
            String productId = image.getProduct().getId();

            if (!imagesByProductId.containsKey(productId)) {
                imagesByProductId.put(productId, new ArrayList<>());
            }

            imagesByProductId.get(productId).add(image);
        }

        return products.stream().map(product -> {
            ProductResponse response = productMapper.toProductResponse(product);

            List<ProductImage> images = imagesByProductId.getOrDefault(product.getId(), new ArrayList<>());

            List<ProductImageResponse> imageResponses = images.stream()
                    .map(productImageMapper::toProductImageResponse)
                    .toList();

            response.setImages(imageResponses);
            return response;
        }).toList();
    }

    @Override
    public ProductResponse save(ProductRequest productRequest) {
        return null;
    }
}
