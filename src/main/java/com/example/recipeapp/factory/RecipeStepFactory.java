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
        // Asigna el número de paso, descripción, URL de media y título
        step.setNumeroPaso(stepReq.getNumeroPaso());
        step.setDescripcion(stepReq.getDescripcion());
        step.setUrlMedia(stepReq.getUrlMedia());
        step.setTitulo(stepReq.getTitulo());
        return step;
    }
}
