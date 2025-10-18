package com.khangmoihocit.minimart.mapper;

import com.khangmoihocit.minimart.dto.response.ProductImageResponse;
import com.khangmoihocit.minimart.entity.ProductImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    ProductImageResponse toProductImageResponse(ProductImage productImage);
}