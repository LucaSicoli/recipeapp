package com.example.recipeapp.repository;

import com.example.recipeapp.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    // Obtener valoraciones de una receta específica
    List<Rating> findByRecipeId(Long recipeId);
}
