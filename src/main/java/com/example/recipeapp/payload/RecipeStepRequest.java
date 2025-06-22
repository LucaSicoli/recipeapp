package com.example.recipeapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/** DTO de cada paso al crear/editar receta */
@Data
@AllArgsConstructor
public class RecipeStepRequest {
    private Integer numeroPaso;
    private String titulo;
    private String descripcion;

    /** Ahora plural: varios medios por paso */
    private List<String> mediaUrls;
}