package com.example.recipeapp.service;

import com.example.recipeapp.model.Ingredient;
import com.example.recipeapp.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    public Ingredient createIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public Optional<Ingredient> getIngredientById(Long id) {
        return ingredientRepository.findById(id);
    }

    public Optional<Ingredient> getIngredientByNombre(String nombre) {
        return ingredientRepository.findByNombre(nombre);
    }

    public Ingredient updateIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public void deleteIngredient(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado"));

        if (!ingredient.getRecipeIngredients().isEmpty()) {
            throw new RuntimeException("No se puede eliminar: el ingrediente está en uso en una receta.");
        }

        ingredientRepository.deleteById(id);
    }

}
