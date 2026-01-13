package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository

public interface CartRepository extends JpaRepository<Cart, String> {

    boolean existsByUserId(String userId);


    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId")
    Optional<Cart> findByUserId(@Param("userId") String userId);

}