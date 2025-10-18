package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.request.CategoryRequest;
import com.khangmoihocit.minimart.dto.response.CategoryResponse;
import com.khangmoihocit.minimart.entity.Category;
import com.khangmoihocit.minimart.exception.OurException;
import com.khangmoihocit.minimart.repository.CategoryRepository;
import com.khangmoihocit.minimart.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;

    @Override
    public CategoryResponse save(CategoryRequest categoryRequest) {
        Category category = Category.builder()
                .name(categoryRequest.getName())
                .build();
        category = categoryRepository.save(category);

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    @Override
    public CategoryResponse update(String id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            category.setName(categoryRequest.getName());
            category = categoryRepository.save(category);
            return CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .build();
        } else {
            throw new OurException("CategoryController not found");
        }
    }

    @Override
    public void delete(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new OurException("CategoryController not found"));
        categoryRepository.delete(category);
    }

    @Override
    public CategoryResponse findById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new OurException("CategoryController not found"));

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    @Override
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build()).toList();
    }
}
