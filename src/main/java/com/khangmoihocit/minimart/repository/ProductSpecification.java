package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.Product;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProductSpecification {

    public static Specification<Product> filterProducts(String keyword, String categoryId,
                                                        BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            log.info("Building specification with: keyword={}, categoryId={}, minPrice={}, maxPrice={}",
                keyword, categoryId, minPrice, maxPrice);

            // Fetch category eagerly to avoid N+1 problem
            if (query != null && query.getResultType().equals(Product.class)) {
                root.fetch("category", jakarta.persistence.criteria.JoinType.LEFT);
                query.distinct(true);
                log.info("Added fetch join for category and set distinct");
            }

            // Search by keyword in name or description
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), likePattern);
                Predicate descPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), likePattern);
                predicates.add(criteriaBuilder.or(namePredicate, descPredicate));
                log.info("Added keyword predicate");
            }

            // Filter by category
            if (categoryId != null && !categoryId.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
                log.info("Added category predicate");
            }

            // Filter by sale price range (use salePrice if available, otherwise use price)
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    criteriaBuilder.coalesce(root.get("salePrice"), root.get("price")), minPrice));
                log.info("Added minPrice predicate");
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    criteriaBuilder.coalesce(root.get("salePrice"), root.get("price")), maxPrice));
                log.info("Added maxPrice predicate");
            }

            log.info("Total predicates: {}", predicates.size());

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

