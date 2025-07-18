package com.example.recipeapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecipeSummaryResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    /** Primer media (thumbnail) o todas */
    private List<String> mediaUrls;
    private Long tiempo;
    private Integer porciones;
    private String tipoPlato;
    private String categoria;
    private String usuarioCreadorAlias;
    private String usuarioFotoPerfil;    // ← NUEVO campo
    private Double promedioRating;
    private String estadoPublicacion; // NUEVO
    private String estado; // NUEVO
    private String fechaCreacion;
}