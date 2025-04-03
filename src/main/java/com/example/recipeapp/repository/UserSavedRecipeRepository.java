package com.example.recipeapp.repository;

import com.example.recipeapp.model.UserSavedRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserSavedRecipeRepository extends JpaRepository<UserSavedRecipe, Long> {
    // Obtener las recetas guardadas por un usuario
    List<UserSavedRecipe> findByUserId(Long userId);
}
