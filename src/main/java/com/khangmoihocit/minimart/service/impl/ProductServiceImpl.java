package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.request.ProductRequest;
import com.khangmoihocit.minimart.dto.request.ProductSearchRequest;
import com.khangmoihocit.minimart.dto.response.ProductImageResponse;
import com.khangmoihocit.minimart.dto.response.ProductResponse;
import com.khangmoihocit.minimart.dto.response.ProductSizeResponse;
import com.khangmoihocit.minimart.entity.Category;
import com.khangmoihocit.minimart.entity.Product;
import com.khangmoihocit.minimart.entity.ProductImage;
import com.khangmoihocit.minimart.entity.ProductSize;
import com.khangmoihocit.minimart.enums.ErrorCode;
import com.khangmoihocit.minimart.exception.AppException;
import com.khangmoihocit.minimart.mapper.ProductImageMapper;
import com.khangmoihocit.minimart.mapper.ProductMapper;
import com.khangmoihocit.minimart.mapper.ProductSizeMapper;
import com.khangmoihocit.minimart.repository.CategoryRepository;
import com.khangmoihocit.minimart.repository.ProductImageRepository;
import com.khangmoihocit.minimart.repository.ProductRepository;
import com.khangmoihocit.minimart.repository.ProductSizeRepository;
import com.khangmoihocit.minimart.repository.ProductSpecification;
import com.khangmoihocit.minimart.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
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
    ProductSizeRepository productSizeRepository;
    ProductMapper productMapper;
    ProductImageMapper productImageMapper;
    ProductSizeMapper productSizeMapper;
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
        List<ProductSize> savedSizes = processAndSaveSizes(request.getSizes(), product);

        ProductResponse productResponse = productMapper.toProductResponse(product);
        List<ProductImageResponse> imageResponses = savedImages.stream()
                .map(productImageMapper::toProductImageResponse)
                .toList();
        List<ProductSizeResponse> sizeResponses = savedSizes.stream()
                .map(productSizeMapper::toProductSizeResponse)
                .toList();
        productResponse.setImages(imageResponses);
        productResponse.setSizes(sizeResponses);

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

        // Update sizes if provided
        List<ProductSize> savedSizes = new ArrayList<>();
        if (productRequest.getSizes() != null && !productRequest.getSizes().isEmpty()) {
            productSizeRepository.deleteByProductId(id);
            savedSizes = processAndSaveSizes(productRequest.getSizes(), product);
        } else {
            savedSizes = productSizeRepository.findByProductId(id);
        }

        // Get existing images
        List<ProductImage> existingImages = productImageRepository.findByProductId(id);

        // Build response with complete information
        ProductResponse productResponse = productMapper.toProductResponse(product);

        List<ProductImageResponse> imageResponses = existingImages.stream()
                .map(productImageMapper::toProductImageResponse)
                .toList();

        List<ProductSizeResponse> sizeResponses = savedSizes.stream()
                .map(productSizeMapper::toProductSizeResponse)
                .toList();

        productResponse.setImages(imageResponses);
        productResponse.setSizes(sizeResponses);

        return productResponse;
    }


    @Override
    @Transactional
    public ProductResponse updateProductImages(String id, List<MultipartFile> files, List<String> keepImageIds) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        //lấy tất cả ảnh sản phẩm và xóa file ảnh trong thư mục
        List<ProductImage> existingImages = new ArrayList<>();
        if(keepImageIds != null){
            existingImages = productImageRepository.findByProductIdAndIdNotIn(id, keepImageIds);
        }else {
            existingImages = productImageRepository.findByProductId(id);
        }

        existingImages.forEach(image -> deleteImageFile(image.getImageUrl()));
        productImageRepository.deleteAll(existingImages);

        List<ProductImage> savedImages = processAndSaveImages(files, product);

        // Get kept images if any
        List<ProductImage> allImages = new ArrayList<>(savedImages);
        if(keepImageIds != null && !keepImageIds.isEmpty()) {
            List<ProductImage> keptImages = productImageRepository.findByProductIdAndIdIn(id, keepImageIds);
            allImages.addAll(keptImages);
        }

        // Get sizes
        List<ProductSize> productSizes = productSizeRepository.findByProductId(id);

        ProductResponse productResponse = productMapper.toProductResponse(product);

        List<ProductImageResponse> imageResponses = allImages.stream()
                .map(productImageMapper::toProductImageResponse)
                .toList();

        List<ProductSizeResponse> sizeResponses = productSizes.stream()
                .map(productSizeMapper::toProductSizeResponse)
                .toList();

        productResponse.setImages(imageResponses);
        productResponse.setSizes(sizeResponses);

        return productResponse;
    }

    @Override
    public Page<ProductResponse> searchProduct(int pageNo, int pageSize, String keyword) {
        Pageable pageable = PageRequest.of(pageNo-1, pageSize);
        Page<Product> products = productRepository.searchByKeyword(keyword, pageable);

        List<String> productIds = products.getContent().stream().map(Product::getId).toList();
        List<ProductImage> productImages = productImageRepository.findByProductIdIn(productIds); //query 2
        List<ProductSize> productSizes = productSizeRepository.findByProductIdIn(productIds); //query 3

        // Nhóm hình ảnh theo productId
        Map<String, List<ProductImage>> imagesByProductId = new HashMap<>();
        for (ProductImage image : productImages){
            String productId = image.getProduct().getId();

            if (!imagesByProductId.containsKey(productId)) {
                imagesByProductId.put(productId, new ArrayList<>());
            }

            imagesByProductId.get(productId).add(image);
        }

        // Nhóm sizes theo productId
        Map<String, List<ProductSize>> sizesByProductId = new HashMap<>();
        for (ProductSize size : productSizes){
            String productId = size.getProduct().getId();

            if (!sizesByProductId.containsKey(productId)) {
                sizesByProductId.put(productId, new ArrayList<>());
            }

            sizesByProductId.get(productId).add(size);
        }


        if (!products.isEmpty()) {
            return products.map(product -> {
                ProductResponse response = productMapper.toProductResponse(product);

                List<ProductImage> images = imagesByProductId.getOrDefault(product.getId(), new ArrayList<>());

                List<ProductImageResponse> imageResponses = images.stream()
                        .map(productImageMapper::toProductImageResponse)
                        .toList();

                List<ProductSize> sizes = sizesByProductId.getOrDefault(product.getId(), new ArrayList<>());
                List<ProductSizeResponse> sizeResponses = sizes.stream()
                        .map(productSizeMapper::toProductSizeResponse)
                        .toList();

                response.setImages(imageResponses);
                response.setSizes(sizeResponses);
                return response;
            });
        }
        return Page.empty();
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

    //lưu nhiều size vào db
    private List<ProductSize> processAndSaveSizes(List<com.khangmoihocit.minimart.dto.request.ProductSizeRequest> sizeRequests, Product product) {
        if (sizeRequests == null || sizeRequests.isEmpty()) {
            return new ArrayList<>();
        }

        List<ProductSize> productSizes = new ArrayList<>();
        for (com.khangmoihocit.minimart.dto.request.ProductSizeRequest sizeRequest : sizeRequests) {
            ProductSize productSize = productSizeMapper.toProductSize(sizeRequest);
            productSize.setProduct(product);
            productSizes.add(productSize);
        }

        return productSizeRepository.saveAll(productSizes);
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
    @Transactional
    public void delete(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Xóa tất cả ảnh sản phẩm và file ảnh trong thư mục
        List<ProductImage> existingImages = productImageRepository.findByProductId(id);
        existingImages.forEach(image -> deleteImageFile(image.getImageUrl()));
        productImageRepository.deleteAll(existingImages);

        // Xóa tất cả sizes của sản phẩm
        productSizeRepository.deleteByProductId(id);

        // Xóa sản phẩm
        productRepository.deleteById(id);
    }

    @Override
    public ProductResponse findById(String id) {
        return productRepository.findById(id)
                .map(product -> {
                    ProductResponse productResponse = productMapper.toProductResponse(product);
                    List<ProductImage> productImages = productImageRepository.findByProductId(product.getId());
                    productResponse.setImages(productImages.stream().map(productImageMapper::toProductImageResponse).toList());

                    List<ProductSize> productSizes = productSizeRepository.findByProductId(product.getId());
                    productResponse.setSizes(productSizes.stream().map(productSizeMapper::toProductSizeResponse).toList());

                    return productResponse;
                })
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override //đã tối ưu còn 3 query
    public List<ProductResponse> findAll() {
        List<Product> products = productRepository.findAllWithCategory(); //query 1
        List<String> productIds = products.stream().map(Product::getId).toList();
        List<ProductImage> productImages = productImageRepository.findByProductIdIn(productIds); //query 2
        List<ProductSize> productSizes = productSizeRepository.findByProductIdIn(productIds); //query 3

        // Nhóm hình ảnh theo productId
        Map<String, List<ProductImage>> imagesByProductId = new HashMap<>();
        for (ProductImage image : productImages){
            String productId = image.getProduct().getId();

            if (!imagesByProductId.containsKey(productId)) {
                imagesByProductId.put(productId, new ArrayList<>());
            }

            imagesByProductId.get(productId).add(image);
        }

        // Nhóm sizes theo productId
        Map<String, List<ProductSize>> sizesByProductId = new HashMap<>();
        for (ProductSize size : productSizes){
            String productId = size.getProduct().getId();

            if (!sizesByProductId.containsKey(productId)) {
                sizesByProductId.put(productId, new ArrayList<>());
            }

            sizesByProductId.get(productId).add(size);
        }

        return products.stream().map(product -> {
            ProductResponse response = productMapper.toProductResponse(product);

            List<ProductImage> images = imagesByProductId.getOrDefault(product.getId(), new ArrayList<>());

            List<ProductImageResponse> imageResponses = images.stream()
                    .map(productImageMapper::toProductImageResponse)
                    .toList();

            List<ProductSize> sizes = sizesByProductId.getOrDefault(product.getId(), new ArrayList<>());
            List<ProductSizeResponse> sizeResponses = sizes.stream()
                    .map(productSizeMapper::toProductSizeResponse)
                    .toList();

            response.setImages(imageResponses);
            response.setSizes(sizeResponses);
            return response;
        }).toList();
    }

    @Override
    public ProductResponse save(ProductRequest productRequest) {
        return null;
    }

    @Override
    public Page<ProductResponse> advancedSearch(ProductSearchRequest searchRequest) {
        log.info("Advanced search request: {}", searchRequest);

        // Tạo specification cho advanced search
        Specification<Product> spec = ProductSpecification.filterProducts(
            searchRequest.getKeyword(),
            searchRequest.getCategoryId(),
            searchRequest.getMinPrice(),
            searchRequest.getMaxPrice()
        );

        // Kiểm tra xem có sort theo giá hay không
        boolean isPriceSort = searchRequest.getSortBy() != null &&
            (searchRequest.getSortBy().equalsIgnoreCase("price_asc") ||
             searchRequest.getSortBy().equalsIgnoreCase("price_desc"));

        // Nếu sort theo giá, fetch tất cả và sort trong memory
        // Để tránh lỗi nullsLast() với Criteria Queries
        Page<Product> products;
        if (isPriceSort) {
            // Lấy tất cả kết quả phù hợp điều kiện (không phân trang)
            List<Product> allProducts = productRepository.findAll(spec);

            // Sort theo effective price (salePrice nếu có, không thì dùng price)
            allProducts.sort((p1, p2) -> {
                BigDecimal effectivePrice1 = p1.getSalePrice() != null ? p1.getSalePrice() : p1.getPrice();
                BigDecimal effectivePrice2 = p2.getSalePrice() != null ? p2.getSalePrice() : p2.getPrice();

                int comparison = effectivePrice1.compareTo(effectivePrice2);
                return searchRequest.getSortBy().equalsIgnoreCase("price_desc") ? -comparison : comparison;
            });

            // Tạo pageable và phân trang thủ công
            int pageSize = searchRequest.getPageSize() != null && searchRequest.getPageSize() > 0
                ? searchRequest.getPageSize() : 10;
            int pageNo = searchRequest.getPageNo() != null && searchRequest.getPageNo() > 0
                ? searchRequest.getPageNo() - 1 : 0;

            int start = Math.min(pageNo * pageSize, allProducts.size());
            int end = Math.min(start + pageSize, allProducts.size());
            List<Product> pageContent = allProducts.subList(start, end);

            Pageable pageable = PageRequest.of(pageNo, pageSize);
            products = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, allProducts.size());
        } else {
            // Sort thông thường - chỉ dùng Sort đơn giản, không dùng nullsLast
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            if (searchRequest.getSortBy() != null && searchRequest.getSortBy().equalsIgnoreCase("newest")) {
                sort = Sort.by(Sort.Direction.DESC, "createdAt");
            }

            Pageable pageable;
            if (searchRequest.getPageSize() == null || searchRequest.getPageSize() <= 0) {
                // Không dùng sort khi lấy tất cả để tránh lỗi với Criteria Query
                pageable = PageRequest.of(0, Integer.MAX_VALUE);
                // Sẽ sort trong memory sau khi fetch
                List<Product> allProducts = productRepository.findAll(spec);
                allProducts.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
                products = new org.springframework.data.domain.PageImpl<>(allProducts, pageable, allProducts.size());
            } else {
                int pageNo = searchRequest.getPageNo() != null && searchRequest.getPageNo() > 0
                    ? searchRequest.getPageNo() - 1 : 0;
                pageable = PageRequest.of(pageNo, searchRequest.getPageSize(), sort);
                products = productRepository.findAll(spec, pageable);
            }
        }


        List<String> productIds = products.getContent().stream().map(Product::getId).toList();
        List<ProductImage> productImages = productImageRepository.findByProductIdIn(productIds);
        List<ProductSize> productSizes = productSizeRepository.findByProductIdIn(productIds);

        // Nhóm hình ảnh theo productId
        Map<String, List<ProductImage>> imagesByProductId = new HashMap<>();
        for (ProductImage image : productImages){
            String productId = image.getProduct().getId();
            if (!imagesByProductId.containsKey(productId)) {
                imagesByProductId.put(productId, new ArrayList<>());
            }
            imagesByProductId.get(productId).add(image);
        }

        // Nhóm sizes theo productId
        Map<String, List<ProductSize>> sizesByProductId = new HashMap<>();
        for (ProductSize size : productSizes){
            String productId = size.getProduct().getId();
            if (!sizesByProductId.containsKey(productId)) {
                sizesByProductId.put(productId, new ArrayList<>());
            }
            sizesByProductId.get(productId).add(size);
        }

        return products.map(product -> {
            ProductResponse response = productMapper.toProductResponse(product);

            List<ProductImage> images = imagesByProductId.getOrDefault(product.getId(), new ArrayList<>());
            List<ProductImageResponse> imageResponses = images.stream()
                    .map(productImageMapper::toProductImageResponse)
                    .toList();

            List<ProductSize> sizes = sizesByProductId.getOrDefault(product.getId(), new ArrayList<>());
            List<ProductSizeResponse> sizeResponses = sizes.stream()
                    .map(productSizeMapper::toProductSizeResponse)
                    .toList();

            response.setImages(imageResponses);
            response.setSizes(sizeResponses);
            return response;
        });
    }

    @Override
    public Long count() {
        return productRepository.count();
    }

    @Override
    public List<ProductResponse> findByCategoryId(String categoryId) {
        List<Product> productList = productRepository.findByCategoryId(categoryId);
        return productList.stream().map(product -> {
            ProductResponse productResponse = productMapper.toProductResponse(product);

            List<ProductImage> productImages = productImageRepository.findByProductId(product.getId());
            productResponse.setImages(productImages.stream().map(productImageMapper::toProductImageResponse).toList());

            List<ProductSize> productSizes = productSizeRepository.findByProductId(product.getId());
            productResponse.setSizes(productSizes.stream().map(productSizeMapper::toProductSizeResponse).toList());

            return productResponse;
        }).toList();
    }
}
