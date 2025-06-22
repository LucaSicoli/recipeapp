package com.example.recipeapp.factory;

import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.model.RecipeStep;
import com.example.recipeapp.payload.RecipeStepRequest;
import org.springframework.stereotype.Component;

@Component
public class RecipeStepFactory {

    public RecipeStep createRecipeStep(RecipeStepRequest stepReq, Recipe recipe) {
        RecipeStep step = new RecipeStep();
        // Asocia la receta
        step.setRecipe(recipe);

        // Número de paso y título/descripcion igual que antes
        step.setNumeroPaso(stepReq.getNumeroPaso());
        step.setTitulo(stepReq.getTitulo());
        step.setDescripcion(stepReq.getDescripcion());

        // Ahora mediaUrls es List<String>, no un String
        step.setMediaUrls(stepReq.getMediaUrls());

        return step;
    }
}