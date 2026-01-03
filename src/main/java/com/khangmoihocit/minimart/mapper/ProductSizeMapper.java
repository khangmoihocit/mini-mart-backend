package com.khangmoihocit.minimart.mapper;

import com.khangmoihocit.minimart.dto.request.ProductSizeRequest;
import com.khangmoihocit.minimart.dto.response.ProductSizeResponse;
import com.khangmoihocit.minimart.entity.ProductSize;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductSizeMapper {
    @Mapping(target = "product", ignore = true)
    ProductSize toProductSize(ProductSizeRequest request);

    ProductSizeResponse toProductSizeResponse(ProductSize productSize);
}

