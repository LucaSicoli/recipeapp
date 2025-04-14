package com.example.recipeapp.payload;

import java.time.LocalDateTime;

public class UserSavedRecipeDTO {
    private Long id;
    private Long recipeId;
    private String recipeNombre;
    private LocalDateTime fechaAgregado;

    // Getters y Setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getRecipeId() {
        return recipeId;
    }
    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }
    public String getRecipeNombre() {
        return recipeNombre;
    }
    public void setRecipeNombre(String recipeNombre) {
        this.recipeNombre = recipeNombre;
    }
    public LocalDateTime getFechaAgregado() {
        return fechaAgregado;
    }
    public void setFechaAgregado(LocalDateTime fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }
}
