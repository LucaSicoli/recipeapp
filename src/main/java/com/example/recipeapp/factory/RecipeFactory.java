package com.example.recipeapp.factory;

import com.example.recipeapp.model.Categoria;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.model.RecipeIngredient;
import com.example.recipeapp.model.RecipeStep;
import com.example.recipeapp.model.TipoPlato;
import com.example.recipeapp.payload.RecipeIngredientRequest;
import com.example.recipeapp.payload.RecipeRequest;
import com.example.recipeapp.payload.RecipeStepRequest;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RecipeFactory {

    private final RecipeIngredientFactory recipeIngredientFactory;
    private final RecipeStepFactory recipeStepFactory;

    public RecipeFactory(RecipeIngredientFactory recipeIngredientFactory, RecipeStepFactory recipeStepFactory) {
        this.recipeIngredientFactory = recipeIngredientFactory;
        this.recipeStepFactory = recipeStepFactory;
    }

    /**
     * Crea una instancia de Recipe a partir del RecipeRequest.
     * Nota: campos como fechaCreacion, estado y usuarioCreador se asignarán en el service.
     */
    public Recipe createRecipeFromRequest(RecipeRequest request) {
        Recipe recipe = new Recipe();
        recipe.setNombre(request.getNombre());
        recipe.setDescripcion(request.getDescripcion());
        recipe.setTiempo(request.getTiempo());
        recipe.setPorciones(request.getPorciones());
        recipe.setMediaUrls(request.getMediaUrls());
        recipe.setCategoria(Categoria.valueOf(request.getCategoria().toUpperCase()));
        recipe.setTipoPlato(TipoPlato.valueOf(request.getTipoPlato().toUpperCase()));

        // Procesa ingredientes (si los hay)
        List<RecipeIngredientRequest> ingredientRequests = request.getIngredients();
        if (ingredientRequests != null) {
            ingredientRequests.forEach(ingredientReq -> {
                // El factory de ingredientes crea y asocia el objeto RecipeIngredient con esta receta
                RecipeIngredient recipeIngredient = recipeIngredientFactory.createRecipeIngredient(ingredientReq, recipe);
                recipe.getIngredients().add(recipeIngredient);
            });
        }

        // Procesa pasos (steps) si los hay
        List<RecipeStepRequest> stepRequests = request.getSteps();
        if (stepRequests != null) {
            stepRequests.forEach(stepReq -> {
                RecipeStep recipeStep = recipeStepFactory.createRecipeStep(stepReq, recipe);
                recipe.getSteps().add(recipeStep);
            });
        }

        return recipe;
    }
}
