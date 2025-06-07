package com.example.recipeapp.payload;

import jakarta.validation.constraints.NotBlank;

public record ResetDTO(
        @NotBlank String email,
        @NotBlank String code,
        @NotBlank String newPassword,
        @NotBlank String confirmPassword
) {}

