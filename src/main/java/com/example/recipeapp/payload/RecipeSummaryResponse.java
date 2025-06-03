package com.example.recipeapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO que vamos a devolver para la lista de recetas en el home,
 * incluyendo alias del creador y promedio de rating.
 */
@Data
@AllArgsConstructor
public class RecipeSummaryResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String fotoPrincipal;
    private Long tiempo;
    private Integer porciones;
    private String tipoPlato;
    private String categoria;
    private String usuarioCreadorAlias;
    private Double promedioRating;
}
