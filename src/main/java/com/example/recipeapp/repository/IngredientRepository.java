package com.example.recipeapp.repository;

import com.example.recipeapp.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    // Buscar un ingrediente por nombre, ya que es único.
    Optional<Ingredient> findByNombre(String nombre);
}
