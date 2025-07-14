package com.example.recipeapp.payload;

public class RecipeNameCheckResponse {
    private boolean exists;
    private String message;
    private RecipeSummaryResponse existingRecipe;

    public RecipeNameCheckResponse() {}

    public RecipeNameCheckResponse(boolean exists, String message, RecipeSummaryResponse existingRecipe) {
        this.exists = exists;
        this.message = message;
        this.existingRecipe = existingRecipe;
    }

    // Getters y setters
    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RecipeSummaryResponse getExistingRecipe() {
        return existingRecipe;
    }

    public void setExistingRecipe(RecipeSummaryResponse existingRecipe) {
        this.existingRecipe = existingRecipe;
    }
}
