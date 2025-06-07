package com.example.recipeapp.payload;
import jakarta.validation.constraints.NotBlank;

public record CodeDTO(
        @NotBlank String email,
        @NotBlank String code
) {}
