package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
