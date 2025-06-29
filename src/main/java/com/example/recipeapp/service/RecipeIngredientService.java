package com.example.recipeapp.service;

import com.example.recipeapp.model.RecipeIngredient;
import com.example.recipeapp.repository.RecipeIngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeIngredientService {
    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    public RecipeIngredient addIngredientToRecipe(RecipeIngredient recipeIngredient) {
        return recipeIngredientRepository.save(recipeIngredient);
    }

    public List<RecipeIngredient> getIngredientsByRecipeId(Long recipeId) {
        return recipeIngredientRepository.findByRecipeId(recipeId);
    }

    public RecipeIngredient updateRecipeIngredient(RecipeIngredient recipeIngredient) {
        return recipeIngredientRepository.save(recipeIngredient);
    }

    public void removeRecipeIngredient(Long id) {
        recipeIngredientRepository.deleteById(id);
    }

    // ← Nuevo método:
    public void deleteByRecipeId(Long recipeId) {
        recipeIngredientRepository
                .findByRecipeId(recipeId)
                .forEach(recipeIngredientRepository::delete);
    }
}