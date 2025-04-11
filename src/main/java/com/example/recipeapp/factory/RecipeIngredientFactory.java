package com.example.recipeapp.factory;

import com.example.recipeapp.model.Ingredient;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.model.RecipeIngredient;
import com.example.recipeapp.payload.RecipeIngredientRequest;
import com.example.recipeapp.service.IngredientService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RecipeIngredientFactory {

    private final IngredientService ingredientService;

    public RecipeIngredientFactory(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    public RecipeIngredient createRecipeIngredient(RecipeIngredientRequest req, Recipe recipe) {
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setRecipe(recipe);

        Ingredient ingredient;
        if (req.getIngredientId() != null) {
            Optional<Ingredient> ingredientOpt = ingredientService.getIngredientById(req.getIngredientId());
            if (ingredientOpt.isPresent()) {
                ingredient = ingredientOpt.get();
            } else if (req.getNombre() != null && !req.getNombre().isEmpty()) {
                Optional<Ingredient> byNameOpt = ingredientService.getIngredientByNombre(req.getNombre());
                ingredient = byNameOpt.orElseGet(() -> {
                    Ingredient newIngredient = new Ingredient();
                    newIngredient.setNombre(req.getNombre());
                    return ingredientService.createIngredient(newIngredient);
                });
            } else {
                throw new RuntimeException("Ingrediente no encontrado con ID: " + req.getIngredientId());
            }
        } else if (req.getNombre() != null && !req.getNombre().isEmpty()) {
            Optional<Ingredient> byNameOpt = ingredientService.getIngredientByNombre(req.getNombre());
            ingredient = byNameOpt.orElseGet(() -> {
                Ingredient newIngredient = new Ingredient();
                newIngredient.setNombre(req.getNombre());
                return ingredientService.createIngredient(newIngredient);
            });
        } else {
            throw new RuntimeException("Falta información del ingrediente (ID o nombre).");
        }

        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setCantidad(req.getCantidad());
        recipeIngredient.setUnidadMedida(req.getUnidadMedida());

        return recipeIngredient;
    }
}
