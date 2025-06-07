// src/main/java/com/example/recipeapp/payload/ConfirmRequest.java
package com.example.recipeapp.payload;

import jakarta.validation.constraints.NotBlank;

public record ConfirmRequest(
        @NotBlank String email,
        @NotBlank String code,
        @NotBlank String password
) {}
