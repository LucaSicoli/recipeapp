package com.example.recipeapp.repository;

import com.example.recipeapp.model.UserSavedRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserSavedRecipeRepository extends JpaRepository<UserSavedRecipe, Long> {
    // Obtener las recetas guardadas por un usuario
    List<UserSavedRecipe> findByUserId(Long userId);
    Optional<UserSavedRecipe> findByUserIdAndRecipeId(Long userId, Long recipeId);
    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);
}
