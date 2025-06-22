package com.example.recipeapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class RecipeDetailResponse {
    private Long id;
    private String nombre;
    private String descripcion;

    /** Portada plural */
    private List<String> mediaUrls;

    private long tiempo;
    private Integer porciones;
    private String tipoPlato;
    private String categoria;

    private List<RecipeIngredientDTO> ingredients;
    private List<RecipeStepDTO> steps;

    private LocalDateTime fechaCreacion;
    private String estado;

    private String usuarioCreadorAlias;
    private Double promedioRating;

    @Data @AllArgsConstructor
    public static class RecipeIngredientDTO {
        private String nombre;
        private Double cantidad;
        private String unidadMedida;
    }

    @Data @AllArgsConstructor
    public static class RecipeStepDTO {
        private Integer numeroPaso;
        private String titulo;
        private String descripcion;
        /** Media plural por paso */
        private List<String> mediaUrls;
    }

    public static RecipeDetailResponse fromEntity(
            com.example.recipeapp.model.Recipe recipe,
            Double promedioRating
    ) {
        var ingredients = recipe.getIngredients().stream()
                .map(ri -> new RecipeIngredientDTO(
                        ri.getIngredient().getNombre(),
                        ri.getCantidad(),
                        ri.getUnidadMedida()
                ))
                .collect(Collectors.toList());

        var steps = recipe.getSteps().stream()
                .map(rs -> new RecipeStepDTO(
                        rs.getNumeroPaso(),
                        rs.getTitulo(),
                        rs.getDescripcion(),
                        rs.getMediaUrls()
                ))
                .collect(Collectors.toList());

        return new RecipeDetailResponse(
                recipe.getId(),
                recipe.getNombre(),
                recipe.getDescripcion(),

                recipe.getMediaUrls(),    // <— aquí
                recipe.getTiempo(),
                recipe.getPorciones(),
                recipe.getTipoPlato().name(),
                recipe.getCategoria().name(),

                ingredients,
                steps,

                recipe.getFechaCreacion(),
                recipe.getEstado().name(),

                recipe.getUsuarioCreador().getAlias(),
                promedioRating != null ? promedioRating : 0.0
        );
    }
}