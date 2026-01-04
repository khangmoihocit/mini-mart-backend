package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.request.AddToCartRequest;
import com.khangmoihocit.minimart.dto.request.UpdateCartItemRequest;
import com.khangmoihocit.minimart.dto.response.CartItemResponse;
import com.khangmoihocit.minimart.dto.response.CartResponse;
import com.khangmoihocit.minimart.entity.Cart;
import com.khangmoihocit.minimart.entity.CartDetail;
import com.khangmoihocit.minimart.entity.Product;
import com.khangmoihocit.minimart.entity.ProductImage;
import com.khangmoihocit.minimart.entity.ProductSize;
import com.khangmoihocit.minimart.entity.User;
import com.khangmoihocit.minimart.enums.ErrorCode;
import com.khangmoihocit.minimart.exception.AppException;
import com.khangmoihocit.minimart.repository.CartDetailRepository;
import com.khangmoihocit.minimart.repository.CartRepository;
import com.khangmoihocit.minimart.repository.ProductImageRepository;
import com.khangmoihocit.minimart.repository.ProductRepository;
import com.khangmoihocit.minimart.repository.ProductSizeRepository;
import com.khangmoihocit.minimart.repository.UserRepository;
import com.khangmoihocit.minimart.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl implements CartService {
    CartRepository cartRepository;
    CartDetailRepository cartDetailRepository;
    ProductRepository productRepository;
    ProductImageRepository productImageRepository;
    ProductSizeRepository productSizeRepository;
    UserRepository userRepository;

    @Override
    public CartResponse getMyCart() {
        String userId = getCurrentUserId();
        Cart cart = getOrCreateCart(userId);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {
        String userId = getCurrentUserId();
        log.info("userid: " + userId);

        // Lấy hoặc tạo giỏ hàng
        Cart cart = getOrCreateCart(userId);

        // Kiểm tra sản phẩm
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Xử lý size và kiểm tra số lượng
        ProductSize productSize = null;
        int availableQuantity;

        if (request.getProductSizeId() != null && !request.getProductSizeId().isEmpty()) {
            // Nếu có chọn size, lấy thông tin size
            productSize = productSizeRepository.findById(request.getProductSizeId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_SIZE_NOT_FOUND));

            // Kiểm tra size có thuộc sản phẩm này không
            if (!productSize.getProduct().getId().equals(product.getId())) {
                throw new AppException(ErrorCode.INVALID_PRODUCT_SIZE);
            }

            availableQuantity = productSize.getQuantity();
        } else {
            // Nếu không có size, lấy số lượng từ product
            availableQuantity = product.getStockQuantity();
        }

        // Kiểm tra số lượng tồn kho
        if (availableQuantity < request.getQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        // Kiểm tra xem sản phẩm (+ size nếu có) đã có trong giỏ chưa
        CartDetail existingItem = cartDetailRepository
                .findByCartIdAndProductIdAndSize(cart.getId(), product.getId(),
                        request.getProductSizeId())
                .orElse(null);

        if (existingItem != null) {
            // Nếu đã có → cộng thêm số lượng
            int newQuantity = existingItem.getQuantity() + request.getQuantity();

            // Kiểm tra lại tồn kho
            if (availableQuantity < newQuantity) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }

            existingItem.setQuantity(newQuantity);
            cartDetailRepository.save(existingItem);
        } else {
            // Nếu chưa có → tạo mới
            CartDetail newItem = CartDetail.builder()
                    .cart(cart)
                    .product(product)
                    .productSize(productSize)
                    .quantity(request.getQuantity())
                    .build();
            cartDetailRepository.save(newItem);
        }

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(String cartDetailId, UpdateCartItemRequest request) {
        String userId = getCurrentUserId();
        Cart cart = getOrCreateCart(userId);

        // Tìm cart item
        CartDetail cartDetail = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        // Kiểm tra xem cart item có thuộc về user không
        if (!cartDetail.getCart().getId().equals(cart.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        // Kiểm tra số lượng tồn kho
        Product product = cartDetail.getProduct();
        int availableQuantity;

        if (cartDetail.getProductSize() != null) {
            // Nếu có size, kiểm tra số lượng từ size
            availableQuantity = cartDetail.getProductSize().getQuantity();
        } else {
            // Nếu không có size, kiểm tra số lượng từ product
            availableQuantity = product.getStockQuantity();
        }

        if (availableQuantity < request.getQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        // Cập nhật số lượng
        cartDetail.setQuantity(request.getQuantity());
        cartDetailRepository.save(cartDetail);

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(String cartDetailId) {
        String userId = getCurrentUserId();
        Cart cart = getOrCreateCart(userId);

        // Tìm cart item
        CartDetail cartDetail = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        // Kiểm tra xem cart item có thuộc về user không
        if (!cartDetail.getCart().getId().equals(cart.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        // Xóa cart item
        cartDetailRepository.delete(cartDetail);

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public void clearCart() {
        String userId = getCurrentUserId();
        Cart cart = getOrCreateCart(userId);

        // Xóa tất cả cart items
        cartDetailRepository.deleteByCartId(cart.getId());
    }

    @Override
    public int getCartItemCount() {
        String userId = getCurrentUserId();
        Cart cart = getOrCreateCart(userId);
        return cartDetailRepository.countByCartId(cart.getId());
    }

    // ==================== Helper Methods ====================

    /**
     * Lấy user ID từ Security Context
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return user.getId();
    }

    /**
     * Lấy hoặc tạo giỏ hàng cho user
     */
    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Build CartResponse từ Cart entity
     */
    private CartResponse buildCartResponse(Cart cart) {
        // Lấy tất cả cart items
        List<CartDetail> cartDetails = cartDetailRepository.findByCartId(cart.getId());

        if (cartDetails.isEmpty()) {
            return CartResponse.builder()
                    .cartId(cart.getId())
                    .userId(cart.getUser().getId())
                    .items(new ArrayList<>())
                    .totalItems(0)
                    .totalQuantity(0)
                    .totalAmount(BigDecimal.ZERO)
                    .build();
        }

        // Lấy product IDs
        List<String> productIds = cartDetails.stream()
                .map(cd -> cd.getProduct().getId())
                .toList();

        // Lấy tất cả images của các sản phẩm (tối ưu query)
        List<ProductImage> productImages = productImageRepository.findByProductIdIn(productIds);

        // Map productId -> first image
        Map<String, String> firstImageMap = productImages.stream()
                .collect(Collectors.groupingBy(
                        img -> img.getProduct().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.isEmpty() ? null : list.get(0).getImageUrl()
                        )
                ));

        // Build cart item responses
        List<CartItemResponse> items = new ArrayList<>();
        int totalQuantity = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartDetail detail : cartDetails) {
            Product product = detail.getProduct();
            ProductSize productSize = detail.getProductSize();

            // Lấy giá hiệu dụng (salePrice nếu có, không thì lấy price)
            BigDecimal effectivePrice = product.getSalePrice() != null
                    ? product.getSalePrice()
                    : product.getPrice();

            // Tính subtotal
            BigDecimal subtotal = effectivePrice.multiply(BigDecimal.valueOf(detail.getQuantity()));

            // Lấy số lượng còn lại (từ size nếu có, hoặc từ product)
            Integer availableQuantity = productSize != null
                    ? productSize.getQuantity()
                    : product.getStockQuantity();

            CartItemResponse itemResponse = CartItemResponse.builder()
                    .id(detail.getId())
                    .productId(product.getId())
                    .productName(product.getName())
                    .price(product.getPrice())
                    .salePrice(product.getSalePrice())
                    .imageUrl(firstImageMap.get(product.getId()))
                    .quantity(detail.getQuantity())
                    .subtotal(subtotal)
                    .productSizeId(productSize != null ? productSize.getId() : null)
                    .sizeName(productSize != null ? productSize.getSizeName() : null)
                    .availableQuantity(availableQuantity)
                    .build();

            items.add(itemResponse);
            totalQuantity += detail.getQuantity();
            totalAmount = totalAmount.add(subtotal);
        }

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .totalItems(items.size())
                .totalQuantity(totalQuantity)
                .totalAmount(totalAmount)
                .build();
    }
}

