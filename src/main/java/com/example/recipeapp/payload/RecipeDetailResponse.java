package com.example.recipeapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO que representará todos los datos de la receta + alias del creador + promedio de rating.
 */
@Data
@AllArgsConstructor
public class RecipeDetailResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String fotoPrincipal;
    private long tiempo;
    private Integer porciones;
    private String tipoPlato;
    private String categoria;
    private List<RecipeIngredientDTO> ingredients;
    private List<RecipeStepDTO> steps;
    private LocalDateTime fechaCreacion;
    private String estado;

    // Estos dos campos extra:
    private String usuarioCreadorAlias;
    private Double promedioRating;

    // Sub‐DTOs para ingredient y step:
    @Data
    @AllArgsConstructor
    public static class RecipeIngredientDTO {
        private String nombre;
        private Double cantidad;
        private String unidadMedida;
    }

    @Data
    @AllArgsConstructor
    public static class RecipeStepDTO {
        private Integer numeroPaso;
        private String titulo;
        private String descripcion;
        private String urlMedia;
    }

    /**
     * Método de conveniencia para mapear entidad Recipe -> RecipeDetailResponse
     */
    public static RecipeDetailResponse fromEntity(
            com.example.recipeapp.model.Recipe recipe,
            Double promedioRating
    ) {
        // Mapear lista de RecipeIngredient a nuestros DTOs
        List<RecipeIngredientDTO> ingredients = recipe.getIngredients().stream()
                .map(ri -> new RecipeIngredientDTO(
                        ri.getIngredient().getNombre(),
                        ri.getCantidad(),
                        ri.getUnidadMedida()
                ))
                .collect(Collectors.toList());

        // Mapear lista de RecipeStep a nuestros DTOs
        List<RecipeStepDTO> steps = recipe.getSteps().stream()
                .map(rs -> new RecipeStepDTO(
                        rs.getNumeroPaso(),
                        rs.getTitulo(),
                        rs.getDescripcion(),
                        rs.getUrlMedia()
                ))
                .collect(Collectors.toList());

        return new RecipeDetailResponse(
                recipe.getId(),
                recipe.getNombre(),
                recipe.getDescripcion(),
                recipe.getFotoPrincipal(),
                recipe.getTiempo(),
                recipe.getPorciones(),
                recipe.getTipoPlato().name(),
                recipe.getCategoria().name(),
                ingredients,
                steps,
                recipe.getFechaCreacion(),
                recipe.getEstado().name(),
                // alias del creador:
                recipe.getUsuarioCreador().getAlias(),
                // promedio calculado en el service:
                promedioRating != null ? promedioRating : 0.0
        );
    }
}
