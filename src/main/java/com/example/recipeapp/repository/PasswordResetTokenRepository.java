// src/main/java/com/example/recipeapp/repository/PasswordResetTokenRepository.java
package com.example.recipeapp.repository;

import com.example.recipeapp.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    /**
     * Busca un token por email y código.
     */
    Optional<PasswordResetToken> findByEmailAndCode(String email, String code);

    /**
     * Elimina todos los tokens asociados a un email (por ej. tras reset exitoso).
     */
    void deleteByEmail(String email);

    /**
     * Limpia los tokens que hayan expirado antes de la fecha indicada.
     */
    void deleteByExpiresAtBefore(LocalDateTime cutoff);
}