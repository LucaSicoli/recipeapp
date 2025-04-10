package com.example.recipeapp.repository;

import com.example.recipeapp.model.EstadoAprobacion;
import com.example.recipeapp.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    // Consulta para encontrar recetas que contengan el ingrediente cuyo nombre es 'ingredientName'
    @Query("SELECT DISTINCT r FROM Recipe r " +
            "JOIN RecipeIngredient ri ON r.id = ri.recipe.id " +
            "JOIN Ingredient i ON ri.ingredient.id = i.id " +
            "WHERE i.nombre = :ingredientName")
    List<Recipe> findRecipesByIngredient(@Param("ingredientName") String ingredientName);

    // Consulta para encontrar recetas que NO contengan el ingrediente cuyo nombre es 'ingredientName'
    @Query("SELECT r FROM Recipe r " +
            "WHERE r.id NOT IN (" +
            "   SELECT ri.recipe.id FROM RecipeIngredient ri " +
            "   JOIN ri.ingredient i " +
            "   WHERE i.nombre = :ingredientName" +
            ")")
    List<Recipe> findRecipesWithoutIngredient(@Param("ingredientName") String ingredientName);

    // Método para buscar recetas por el estado (basado en la propiedad 'estado' de Recipe)
    List<Recipe> findByEstado(EstadoAprobacion estado);
}
