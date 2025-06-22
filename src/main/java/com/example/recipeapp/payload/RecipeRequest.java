package com.example.recipeapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecipeRequest {
    private String nombre;
    private String descripcion;
    private long tiempo;
    private Integer porciones;

    /** Lista de URLs (imágenes o vídeos) de portada */
    private List<String> mediaUrls;

    private String tipoPlato;
    private String categoria;

    private List<RecipeIngredientRequest> ingredients;
    private List<RecipeStepRequest> steps;
}