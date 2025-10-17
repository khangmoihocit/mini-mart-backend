package com.khangmoihocit.minimart.repository;


import com.khangmoihocit.minimart.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
