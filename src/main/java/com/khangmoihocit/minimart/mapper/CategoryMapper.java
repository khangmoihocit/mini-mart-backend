package com.khangmoihocit.minimart.mapper;

import com.khangmoihocit.minimart.dto.response.CategoryResponse;
import com.khangmoihocit.minimart.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toCategoryResponse(Category category);
}
