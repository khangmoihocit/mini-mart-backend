package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, String> {
    Optional<Token> findByToken(String token);
}
