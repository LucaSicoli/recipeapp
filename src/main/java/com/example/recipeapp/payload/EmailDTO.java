// src/main/java/com/example/recipeapp/payload/EmailDTO.java
package com.example.recipeapp.payload;
import jakarta.validation.constraints.NotBlank;

public record EmailDTO(@NotBlank String email) {}
