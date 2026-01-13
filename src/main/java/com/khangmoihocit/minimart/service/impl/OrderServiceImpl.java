package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.request.OrderRequest;
import com.khangmoihocit.minimart.dto.request.UpdateOrderStatusRequest;
import com.khangmoihocit.minimart.dto.response.OrderDetailResponse;
import com.khangmoihocit.minimart.dto.response.OrderResponse;
import com.khangmoihocit.minimart.dto.response.PageResponse;
import com.khangmoihocit.minimart.entity.*;
import com.khangmoihocit.minimart.enums.ErrorCode;
import com.khangmoihocit.minimart.enums.OrderStatus;
import com.khangmoihocit.minimart.exception.AppException;
import com.khangmoihocit.minimart.repository.*;
import com.khangmoihocit.minimart.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepository;
    OrderDetailRepository orderDetailRepository;
    CartRepository cartRepository;
    CartDetailRepository cartDetailRepository;
    UserRepository userRepository;
    ProductRepository productRepository;
    ProductImageRepository productImageRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        String userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Lấy giỏ hàng
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_IS_EMPTY));

        // Lấy các items trong giỏ
        List<CartDetail> cartDetails = cartDetailRepository.findByCartId(cart.getId());
        if (cartDetails.isEmpty()) {
            throw new AppException(ErrorCode.CART_IS_EMPTY);
        }
        System.out.println(cartDetails);

        // Tính tổng tiền và kiểm tra tồn kho
        BigDecimal totalMoney = BigDecimal.ZERO;
        for (CartDetail cartDetail : cartDetails) {
            Product product = cartDetail.getProduct();
            ProductSize productSize = cartDetail.getProductSize();
            int requestQuantity = cartDetail.getQuantity();

            // Kiểm tra tồn kho
            int availableQuantity = productSize != null
                    ? productSize.getQuantity()
                    : product.getStockQuantity();

            if (availableQuantity < requestQuantity) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }

            // Tính giá
            BigDecimal effectivePrice = product.getSalePrice() != null
                    ? product.getSalePrice()
                    : product.getPrice();

            BigDecimal itemTotal = effectivePrice.multiply(BigDecimal.valueOf(requestQuantity));
            totalMoney = totalMoney.add(itemTotal);
        }

        // Tạo đơn hàng
        Order order = Order.builder()
                .user(user)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .shippingAddress(request.getShippingAddress())
                .note(request.getNote())
                .status(OrderStatus.PENDING)
                .totalMoney(totalMoney)
                .shippingMethod(request.getShippingMethod())
                .paymentMethod(request.getPaymentMethod())
                .build();
        order = orderRepository.save(order);

        // Tạo order details và cập nhật tồn kho
        for (CartDetail cartDetail : cartDetails) {
            Product product = cartDetail.getProduct();
            ProductSize productSize = cartDetail.getProductSize();
            int requestQuantity = cartDetail.getQuantity();

            // Tính giá
            BigDecimal effectivePrice = product.getSalePrice() != null
                    ? product.getSalePrice()
                    : product.getPrice();

            BigDecimal itemTotal = effectivePrice.multiply(BigDecimal.valueOf(requestQuantity));

            // Tạo order detail
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .price(effectivePrice)
                    .numberOfProducts(requestQuantity)
                    .totalMoney(itemTotal)
                    .sizeName(productSize != null ? productSize.getSizeName() : null)
                    .build();
            orderDetailRepository.save(orderDetail);

            // Cập nhật tồn kho
            if (productSize != null) {
                productSize.setQuantity(productSize.getQuantity() - requestQuantity);
            } else {
                product.setStockQuantity(product.getStockQuantity() - requestQuantity);
            }
        }

        // Xóa giỏ hàng sau khi đặt hàng thành công
        cartDetailRepository.deleteByCartId(cart.getId());

        return buildOrderResponse(order);
    }

    @Override
    public PageResponse<OrderResponse> getMyOrders(int page, int size) {
        String userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Order> orderPage = orderRepository.findByUserIdOrderByOrderDateDesc(userId, pageable);

        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(this::buildOrderResponse)
                .collect(Collectors.toList());

        return PageResponse.<OrderResponse>builder()
                .pageNo(page)
                .pageSize(orderPage.getSize())
                .totalPages(orderPage.getTotalPages())
                .totalElements(orderPage.getTotalElements())
                .first(orderPage.isFirst())
                .last(orderPage.isLast())
                .content(orderResponses)
                .build();
    }

    @Override
    public OrderResponse getOrderById(String orderId) {
        String userId = getCurrentUserId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Kiểm tra quyền truy cập
        if (!order.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return buildOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String orderId) {
        String userId = getCurrentUserId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Kiểm tra quyền
        if (!order.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        // Chỉ cho phép hủy đơn hàng ở trạng thái PENDING
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new AppException(ErrorCode.CANNOT_CANCEL_ORDER);
        }

        // Hoàn lại tồn kho
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        for (OrderDetail orderDetail : orderDetails) {
            Product product = orderDetail.getProduct();
            product.setStockQuantity(product.getStockQuantity() + orderDetail.getNumberOfProducts());
            productRepository.save(product);
        }

        // Cập nhật trạng thái đơn hàng
        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        return buildOrderResponse(order);
    }


    @Override
    public PageResponse<OrderResponse> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Order> orderPage = orderRepository.findAllByOrderByOrderDateDesc(pageable);

        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(this::buildOrderResponse)
                .collect(Collectors.toList());

        return PageResponse.<OrderResponse>builder()
                .pageNo(page)
                .pageSize(orderPage.getSize())
                .totalPages(orderPage.getTotalPages())
                .totalElements(orderPage.getTotalElements())
                .first(orderPage.isFirst())
                .last(orderPage.isLast())
                .content(orderResponses)
                .build();
    }

    @Override
    public PageResponse<OrderResponse> getOrdersByStatus(OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Order> orderPage = orderRepository.findByStatusOrderByOrderDateDesc(status, pageable);

        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(this::buildOrderResponse)
                .collect(Collectors.toList());

        return PageResponse.<OrderResponse>builder()
                .pageNo(page)
                .pageSize(orderPage.getSize())
                .totalPages(orderPage.getTotalPages())
                .totalElements(orderPage.getTotalElements())
                .first(orderPage.isFirst())
                .last(orderPage.isLast())
                .content(orderResponses)
                .build();
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Nếu chuyển sang CANCELLED, hoàn lại tồn kho
        if (request.getStatus() == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
            for (OrderDetail orderDetail : orderDetails) {
                Product product = orderDetail.getProduct();
                product.setStockQuantity(product.getStockQuantity() + orderDetail.getNumberOfProducts());
                productRepository.save(product);
            }
        }

        order.setStatus(request.getStatus());
        order = orderRepository.save(order);

        return buildOrderResponse(order);
    }

    @Override
    @Transactional
    public void deleteOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Nếu đơn hàng không ở trạng thái CANCELLED, hoàn lại tồn kho
        if (order.getStatus() != OrderStatus.CANCELLED) {
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
            for (OrderDetail orderDetail : orderDetails) {
                Product product = orderDetail.getProduct();
                product.setStockQuantity(product.getStockQuantity() + orderDetail.getNumberOfProducts());
                productRepository.save(product);
            }
        }

        orderRepository.delete(order);
    }


    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return user.getId();
    }

    private OrderResponse buildOrderResponse(Order order) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());

        // Lấy product IDs
        List<String> productIds = orderDetails.stream()
                .map(od -> od.getProduct().getId())
                .toList();

        // Lấy tất cả images của các sản phẩm
        List<ProductImage> productImages = productImageRepository.findByProductIdIn(productIds);
        Map<String, String> firstImageMap = productImages.stream()
                .collect(Collectors.groupingBy(
                        img -> img.getProduct().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.isEmpty() ? null : list.get(0).getImageUrl()
                        )
                ));

        // Build order detail responses
        List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
                .map(od -> OrderDetailResponse.builder()
                        .id(od.getId())
                        .productId(od.getProduct().getId())
                        .productName(od.getProduct().getName())
                        .productImage(firstImageMap.get(od.getProduct().getId()))
                        .price(od.getPrice())
                        .numberOfProducts(od.getNumberOfProducts())
                        .totalMoney(od.getTotalMoney())
                        .productSize(od.getSizeName())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .fullName(order.getFullName())
                .email(order.getEmail())
                .phoneNumber(order.getPhoneNumber())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .status(order.getStatus())
                .totalMoney(order.getTotalMoney())
                .shippingMethod(order.getShippingMethod())
                .paymentMethod(order.getPaymentMethod())
                .orderDate(order.getOrderDate())
                .orderDetails(orderDetailResponses)
                .build();
    }
}

