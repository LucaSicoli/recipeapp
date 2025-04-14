package com.example.recipeapp.service;

import com.example.recipeapp.factory.RecipeFactory;
import com.example.recipeapp.model.*;
import com.example.recipeapp.payload.RecipeRequest;
import com.example.recipeapp.payload.RecipeIngredientRequest;
import com.example.recipeapp.payload.RecipeStepRequest;
import com.example.recipeapp.repository.RecipeRepository;
import com.example.recipeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private RecipeIngredientService recipeIngredientService;
    @Autowired
    private RecipeFactory recipeFactory;  // Inyectamos el RecipeFactory

    // Método para crear una receta a partir de RecipeRequest completo
    public Recipe createRecipe(RecipeRequest request) {
        // Usa el factory para construir la receta (con ingredientes y steps asociados)
        Recipe recipe = recipeFactory.createRecipeFromRequest(request);

        // Asigna campos adicionales, como fecha, estado y usuario autenticado
        recipe.setFechaCreacion(LocalDateTime.now());
        recipe.setEstado(EstadoAprobacion.PENDIENTE);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            recipe.setUsuarioCreador(optionalUser.get());
        } else {
            throw new RuntimeException("Usuario autenticado no encontrado en la base de datos");
        }

        // Persiste la receta completa.
        // Dado que Recipe tiene CascadeType.ALL en las relaciones, ingredientes y pasos se persistirán en cascada.
        return recipeRepository.save(recipe);
    }

    // Otros métodos de servicio siguen igual...
    public List<Recipe> getRecipesByIngredient(String ingredientName) {
        return recipeRepository.findRecipesByIngredient(ingredientName);
    }

    public List<Recipe> getRecipesWithoutIngredient(String ingredientName) {
        return recipeRepository.findRecipesWithoutIngredient(ingredientName);
    }

    public Optional<Recipe> getRecipeByIdWithCreator(Long id) {
        return recipeRepository.findByIdWithUsuarioCreador(id);
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

    public List<Recipe> getRecipesByEstado(String estado) {
        EstadoAprobacion estadoEnum = EstadoAprobacion.valueOf(estado.toUpperCase());
        return recipeRepository.findByEstado(estadoEnum);
    }
}
