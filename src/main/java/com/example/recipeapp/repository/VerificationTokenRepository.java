// src/main/java/com/example/recipeapp/repository/VerificationTokenRepository.java
package com.example.recipeapp.repository;

import com.example.recipeapp.model.VerificationToken;
import com.example.recipeapp.model.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
    Optional<VerificationToken> findByEmailAndCodeAndType(String email, String code, TokenType type);
    void deleteByEmailAndType(String email, TokenType type);
}
