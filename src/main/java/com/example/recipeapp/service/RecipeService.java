package com.example.recipeapp.service;

import com.example.recipeapp.factory.RecipeFactory;
import com.example.recipeapp.model.EstadoAprobacion;
import com.example.recipeapp.model.EstadoPublicacion;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.model.User;
import com.example.recipeapp.payload.*;
import com.example.recipeapp.repository.RatingRepository;
import com.example.recipeapp.repository.RecipeRepository;
import com.example.recipeapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private RecipeIngredientService recipeIngredientService;

    @Autowired
    private RecipeFactory recipeFactory;  // Inyectamos el RecipeFactory

    // ------------------------------------------------------------
    // Método existente: crear una receta a partir de RecipeRequest
    // ------------------------------------------------------------
    public Recipe createRecipe(RecipeRequest request) {
        Recipe r = recipeFactory.createRecipeFromRequest(request);
        r.setFechaCreacion(LocalDateTime.now());
        // estado de aprobación siempre PENDIENTE (correcto)
        r.setEstado(EstadoAprobacion.PENDIENTE);
        // PUBLICAMOS directamente:
        r.setEstadoPublicacion(EstadoPublicacion.PUBLICADO);

        // asignamos creador…
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        r.setUsuarioCreador(u);

        return recipeRepository.save(r);
    }

    // ------------------------------------------------------------
    // Ahora, agregamos este nuevo método para devolver el listado
    // de recetas con alias del creador y promedio de rating
    // ------------------------------------------------------------
    public List<RecipeSummaryResponse> getAllRecipesWithAverage() {
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream()
                .map(recipe -> {
                    String alias = recipe.getUsuarioCreador().getAlias();
                    Double avg = ratingRepository.findAverageRatingByRecipeId(recipe.getId());
                    // En el summary quizá quieras sólo la primera URL como “thumb”
                    String thumb = recipe.getMediaUrls().isEmpty()
                            ? null
                            : recipe.getMediaUrls().get(0);

                    return new RecipeSummaryResponse(
                            recipe.getId(),
                            recipe.getNombre(),
                            recipe.getDescripcion(),
                            recipe.getMediaUrls(),                 // en lugar de fotoPrincipal
                            recipe.getTiempo(),
                            recipe.getPorciones(),
                            recipe.getTipoPlato().name(),
                            recipe.getCategoria().name(),
                            alias,
                            (avg != null) ? avg : 0.0
                    );
                })
                .collect(Collectors.toList());
    }


    // -----------------------------------------------
    // Métodos existentes para búsquedas y actualizaciones
    // -----------------------------------------------
    public List<Recipe> getRecipesByIngredient(String ingredientName) {
        return recipeRepository.findRecipesByIngredient(ingredientName);
    }

    public List<Recipe> getRecipesWithoutIngredient(String ingredientName) {
        return recipeRepository.findRecipesWithoutIngredient(ingredientName);
    }

    public Optional<Recipe> getRecipeByIdWithCreator(Long id) {
        return recipeRepository.findByIdWithUsuarioCreador(id);
    }

    public boolean existsById(Long id) {
        return recipeRepository.existsById(id);
    }

    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }

    public Recipe updateRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllRecipes() {
        recipeRepository.deleteAll();
    }

    public Optional<RecipeDetailResponse> getRecipeDetailWithAverage(Long id) {
        // 1) Buscar la receta con join al usuario creador, ingredientes y pasos
        Optional<Recipe> recipeOpt = recipeRepository.findByIdWithUsuarioCreador(id);
        if (recipeOpt.isEmpty()) {
            return Optional.empty();
        }
        Recipe receta = recipeOpt.get();

        // 2) Calcular promedio de rating (puede devolver null si no hay ratings)
        Double avg = ratingRepository.findAverageRatingByRecipeId(id);
        Double promedio = (avg != null) ? avg : 0.0;

        // 3) Mapeamos a DTO
        RecipeDetailResponse dto = RecipeDetailResponse.fromEntity(receta, promedio);
        return Optional.of(dto);
    }

    public List<Recipe> getRecipesByEstado(String estado) {
        EstadoAprobacion estadoEnum = EstadoAprobacion.valueOf(estado.toUpperCase());
        return recipeRepository.findByEstado(estadoEnum);
    }

    public Recipe saveDraftFromRequest(String email, RecipeRequest request) {
        Recipe draft = recipeFactory.createRecipeFromRequest(request);
        draft.setFechaCreacion(LocalDateTime.now());
        // marcamos este draft como borrador
        draft.setEstadoPublicacion(EstadoPublicacion.BORRADOR);
        // ¡muy importante! dejamos el estado de aprobación también en PENDIENTE
        draft.setEstado(EstadoAprobacion.PENDIENTE);

        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        draft.setUsuarioCreador(u);

        return recipeRepository.save(draft);
    }

    // 2. Listar borradores de un usuario por email
    public List<Recipe> getDraftsByUserEmail(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return recipeRepository.findByUsuarioCreadorIdAndEstadoPublicacion(
                u.getId(), EstadoPublicacion.BORRADOR);
    }

    // 3. Publicar un borrador existente
    public Recipe publish(Long recipeId) {
        Recipe r = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        r.setEstadoPublicacion(EstadoPublicacion.PUBLICADO);
        return recipeRepository.save(r);
    }

    public List<RecipeSummaryResponse> getMyDraftsSummary(String email) {
        List<Recipe> drafts = getDraftsByUserEmail(email);
        return drafts.stream()
                .map(r -> new RecipeSummaryResponse(
                        r.getId(),
                        r.getNombre(),
                        r.getDescripcion(),
                        Collections.singletonList(r.getMediaUrls().isEmpty() ? null : r.getMediaUrls().get(0)),
                        r.getTiempo(),
                        r.getPorciones(),
                        r.getTipoPlato().name(),
                        r.getCategoria().name(),
                        r.getUsuarioCreador().getAlias(),
                        ratingRepository.findAverageRatingByRecipeId(r.getId())
                ))
                .collect(Collectors.toList());
    }

    public List<RecipeSummaryResponse> getLatestApprovedPublishedSummaries() {
        var recipes = recipeRepository.findTop3ByEstadoAndEstadoPublicacionOrderByFechaCreacionDesc(
                EstadoAprobacion.APROBADO,
                EstadoPublicacion.PUBLICADO
        );
        return recipes.stream().map(r -> new RecipeSummaryResponse(
                r.getId(), r.getNombre(), r.getDescripcion(), r.getMediaUrls(),
                r.getTiempo(), r.getPorciones(), r.getTipoPlato().name(),
                r.getCategoria().name(), r.getUsuarioCreador().getAlias(),
                ratingRepository.findAverageRatingByRecipeId(r.getId())
        )).toList();
    }
}
