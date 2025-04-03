package com.example.recipeapp.controller;

import com.example.recipeapp.model.RecipeIngredient;
import com.example.recipeapp.service.RecipeIngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipe-ingredients")
public class RecipeIngredientController {

    @Autowired
    private RecipeIngredientService recipeIngredientService;

    // Agregar un ingrediente a una receta
    @PostMapping
    public ResponseEntity<RecipeIngredient> addIngredientToRecipe(@RequestBody RecipeIngredient recipeIngredient) {
        RecipeIngredient created = recipeIngredientService.addIngredientToRecipe(recipeIngredient);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Obtener ingredientes de una receta por el ID de la receta
    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<RecipeIngredient>> getIngredientsByRecipe(@PathVariable Long recipeId) {
        List<RecipeIngredient> ingredients = recipeIngredientService.getIngredientsByRecipeId(recipeId);
        return new ResponseEntity<>(ingredients, HttpStatus.OK);
    }

    // Actualizar un registro de RecipeIngredient
    @PutMapping("/{id}")
    public ResponseEntity<RecipeIngredient> updateRecipeIngredient(@PathVariable Long id, @RequestBody RecipeIngredient recipeIngredient) {
        RecipeIngredient updated = recipeIngredientService.updateRecipeIngredient(recipeIngredient);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Eliminar un registro de RecipeIngredient
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipeIngredient(@PathVariable Long id) {
        recipeIngredientService.removeRecipeIngredient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
