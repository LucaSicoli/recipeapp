package com.example.recipeapp.controller;

import com.example.recipeapp.model.*;
import com.example.recipeapp.payload.RecipeRequest;
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
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        Optional<Recipe> recipe = recipeService.getRecipeByIdWithCreator(id); // Usa el método join fetch
        return recipe.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Actualizar una receta
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe) {
        Recipe updated = recipeService.updateRecipe(recipe);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Eliminar una receta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllApprovedRecipes() {
        List<Recipe> recipes = recipeService.getRecipesByEstado("APROBADO");
        return ResponseEntity.ok(recipes);
    }
}
