package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM users u WHERE u.is_active = true", nativeQuery = true)
    List<User> findUserActive();

}
