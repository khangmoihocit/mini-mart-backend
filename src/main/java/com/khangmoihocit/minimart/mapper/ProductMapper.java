package com.khangmoihocit.minimart.mapper;

import com.khangmoihocit.minimart.dto.response.ProductResponse;
import com.khangmoihocit.minimart.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toProductResponse(Product product);
}
