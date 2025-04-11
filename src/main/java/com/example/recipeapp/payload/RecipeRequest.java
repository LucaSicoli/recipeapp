package com.example.recipeapp.payload;

import lombok.Data;

import java.util.List;

@Data
public class RecipeRequest {
    private String nombre;
    private String descripcion;
    private long tiempo;
    private Integer porciones;
    private String fotoPrincipal;
    private String categoria;
    private String tipoPlato;
    private List<RecipeIngredientRequest> ingredients;
}
