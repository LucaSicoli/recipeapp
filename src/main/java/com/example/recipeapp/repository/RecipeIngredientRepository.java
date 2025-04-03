package com.example.recipeapp.repository;

import com.example.recipeapp.model.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    // Obtener todos los ingredientes de una receta en particular.
    List<RecipeIngredient> findByRecipeId(Long recipeId);
}
