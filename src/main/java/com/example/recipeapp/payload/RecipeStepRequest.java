package com.example.recipeapp.payload;

import lombok.Data;

@Data
public class RecipeStepRequest {
    private Integer numeroPaso;  // Orden del paso en la receta.
    private String descripcion;
    private String urlMedia;
    private String titulo;
}
