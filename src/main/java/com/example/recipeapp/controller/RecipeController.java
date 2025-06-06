package com.example.recipeapp.controller;

import com.example.recipeapp.model.*;
import com.example.recipeapp.payload.RecipeDetailResponse;
import com.example.recipeapp.payload.RecipeRequest;
import com.example.recipeapp.payload.RecipeSummaryResponse;
import com.example.recipeapp.service.IngredientService;
import com.example.recipeapp.service.RecipeIngredientService;
import com.example.recipeapp.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private IngredientService ingredientService;  // Agrega esta línea

    @Autowired
    private RecipeIngredientService recipeIngredientService;

    // Crear una receta
    @PostMapping("/create")
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody RecipeRequest recipeRequest) {
        Recipe createdRecipe = recipeService.createRecipe(recipeRequest);
        return new ResponseEntity<>(createdRecipe, HttpStatus.CREATED);
    }


    @GetMapping("/{id}/full")
    public ResponseEntity<Recipe> getRecipeByIdWithCreator(@PathVariable Long id) {
        Optional<Recipe> recipeOpt = recipeService.getRecipeByIdWithCreator(id);
        return recipeOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Obtener recetas por estado de aprobación
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Recipe>> getRecipesByEstado(@PathVariable String estado) {
        List<Recipe> recipes = recipeService.getRecipesByEstado(estado);
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    // Obtener una receta por ID
    @GetMapping("/{id}")
    public ResponseEntity<RecipeDetailResponse> getRecipeById(@PathVariable Long id) {
        Optional<RecipeDetailResponse> dtoOpt = recipeService.getRecipeDetailWithAverage(id);
        return dtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Actualizar una receta
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe) {
        Recipe updated = recipeService.updateRecipe(recipe);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        if (!recipeService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllRecipes() {
        recipeService.deleteAllRecipes();
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllApprovedRecipes() {
        List<Recipe> recipes = recipeService.getRecipesByEstado("APROBADO");
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/summary")
    public ResponseEntity<List<RecipeSummaryResponse>> getAllRecipesSummary() {
        List<RecipeSummaryResponse> dtos = recipeService.getAllRecipesWithAverage();
        return ResponseEntity.ok(dtos);
    }
}
