package com.example.recipeapp.repository;

import com.example.recipeapp.model.RecipeStep;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {
    // Obtiene los pasos de una receta ordenados ascendentemente por el número de paso
    List<RecipeStep> findByRecipeIdOrderByNumeroPasoAsc(Long recipeId);
}
