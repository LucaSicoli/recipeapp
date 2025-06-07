// src/main/java/com/example/recipeapp/model/VerificationToken.java
package com.example.recipeapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "verification_tokens")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código de 6 dígitos, no nulo
    @Column(name = "token", length = 6, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType type;            // REGISTER or RESET_PASSWORD

    // Fecha de generación, no nula
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Fecha de expiración, no nula
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // El email al que va dirigido, no nulo
    @Column(nullable = false)
    private String email;
}
