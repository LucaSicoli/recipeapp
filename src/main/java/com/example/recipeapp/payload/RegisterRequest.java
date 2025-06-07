// src/main/java/com/example/recipeapp/payload/RegisterRequest.java
package com.example.recipeapp.payload;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String email,
        @NotBlank String alias
) {}
