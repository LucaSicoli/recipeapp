package com.example.recipeapp.service;

import com.example.recipeapp.factory.RecipeFactory;
import com.example.recipeapp.model.EstadoAprobacion;
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
        Recipe recipe = recipeFactory.createRecipeFromRequest(request);
        recipe.setFechaCreacion(LocalDateTime.now());
        recipe.setEstado(EstadoAprobacion.PENDIENTE);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            recipe.setUsuarioCreador(optionalUser.get());
        } else {
            throw new RuntimeException("Usuario autenticado no encontrado en la base de datos");
        }

        return recipeRepository.save(recipe);
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
}
