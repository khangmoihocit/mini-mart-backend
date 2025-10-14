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

    @Query(value = "select * from users u " +
                    "where (u.fullname like %:fullName% or :fullName is null) and (u.email = :email or :email is null) and (u.address like %:address% or :address is null)",
            countQuery = "select count(*) from users u " +
                    "where (u.fullname like %:fullName% or :fullName is null) and (u.email = :email or :email is null) and (u.address like %:address% or :address is null)",
            nativeQuery = true)
    Page<User> search(@Param("fullName") String fullName,
                      @Param("email") String email,
                      @Param("address") String address,
                      Pageable pageable);


}
