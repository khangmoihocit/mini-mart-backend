package com.khangmoihocit.minimart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    String name;

    @NotBlank(message = "Giá sản phẩm không được để trống")
    @Min(value = 0, message = "Giá sản phẩm phải lớn hơn hoặc bằng 0")
    BigDecimal price;

    BigDecimal salePrice;

    @NotBlank(message = "Mô tả sản phẩm không được để trống")
    String description;

    @Min(value = 0, message = "Số lượng trong kho phải lớn hơn hoặc bằng 0")
    Integer stockQuantity = 0;

    @NotBlank(message = "Danh mục sản phẩm không được để trống")
    String categoryId;

    List<MultipartFile> images;
}
