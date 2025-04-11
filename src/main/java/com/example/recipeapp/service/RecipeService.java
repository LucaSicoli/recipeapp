package com.example.recipeapp.service;

import com.example.recipeapp.factory.RecipeIngredientFactory;
import com.example.recipeapp.model.*;
import com.example.recipeapp.payload.RecipeIngredientRequest;
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
    private RecipeIngredientFactory recipeIngredientFactory; // Inyectamos la factory

    // Método para crear una receta con ingredientes usando el Factory Pattern
    public Recipe createRecipe(Recipe recipe, List<RecipeIngredientRequest> ingredientsReq) {
        // Asigna fecha, estado y el usuario autenticado
        recipe.setFechaCreacion(LocalDateTime.now());
        recipe.setEstado(EstadoAprobacion.PENDIENTE);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            recipe.setUsuarioCreador(optionalUser.get());
        } else {
            throw new RuntimeException("Usuario autenticado no encontrado en la base de datos");
        }

        // Guarda la receta principal
        Recipe createdRecipe = recipeRepository.save(recipe);

        // Procesa la lista de ingredientes, si se envía
        if (ingredientsReq != null) {
            ingredientsReq.forEach(ingredientReq -> {
                // Utiliza el factory para crear la relación RecipeIngredient
                RecipeIngredient recipeIngredient = recipeIngredientFactory.createRecipeIngredient(ingredientReq, createdRecipe);
                recipeIngredientService.addIngredientToRecipe(recipeIngredient);
                createdRecipe.getIngredients().add(recipeIngredient);
            });
        }

        return createdRecipe;
    }

    // Otros métodos existentes...
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
