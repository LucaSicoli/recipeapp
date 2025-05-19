package com.example.recipeapp.repository;

import com.example.recipeapp.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByEmailAndCode(String email, String code);
    void deleteByEmail(String email);
}
