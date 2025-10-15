package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM users u WHERE u.email = ?1 and u.is_active is true", nativeQuery = true)
    Optional<User> findUser(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM users u WHERE u.is_active = true", nativeQuery = true)
    List<User> findUsersActive();

    @Query(value = "SELECT * FROM users u WHERE " +
            "(:keyword IS NULL OR " +
            "u.fullname LIKE %:keyword% OR " +
            "u.email LIKE %:keyword% OR " +
            "u.address LIKE %:keyword% OR " +
            "u.phone_number LIKE %:keyword%)",
            countQuery = "SELECT count(*) FROM users u WHERE " +
                    "(:keyword IS NULL OR " +
                    "u.fullname LIKE %:keyword% OR " +
                    "u.email LIKE %:keyword% OR " +
                    "u.address LIKE %:keyword% OR " +
                    "u.phone_number LIKE %:keyword%)",
            nativeQuery = true)
    Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
