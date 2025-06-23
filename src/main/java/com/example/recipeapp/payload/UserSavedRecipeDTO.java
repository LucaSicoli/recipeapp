package com.example.recipeapp.payload;

public class UserSavedRecipeDTO {
    private Long id;
    private Long recipeId;
    private String recipeNombre;
    private String fechaAgregado;

    public UserSavedRecipeDTO() {}

    public UserSavedRecipeDTO(Long id, Long recipeId, String recipeNombre, String fechaAgregado) {
        this.id             = id;
        this.recipeId       = recipeId;
        this.recipeNombre   = recipeNombre;
        this.fechaAgregado  = fechaAgregado;
    }

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRecipeId() { return recipeId; }
    public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }

    public String getRecipeNombre() { return recipeNombre; }
    public void setRecipeNombre(String recipeNombre) { this.recipeNombre = recipeNombre; }

    public String getFechaAgregado() { return fechaAgregado; }
    public void setFechaAgregado(String fechaAgregado) { this.fechaAgregado = fechaAgregado; }
}