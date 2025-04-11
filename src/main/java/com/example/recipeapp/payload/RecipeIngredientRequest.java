package com.example.recipeapp.payload;

import lombok.Data;

@Data
public class RecipeIngredientRequest {
    private String nombre; // Nombre del ingrediente que no existe
    private Long ingredientId; // Id del ingrediente que ya existe
    private Double cantidad;
    private String unidadMedida;
}
