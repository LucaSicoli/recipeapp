package com.example.recipeapp.controller;

import com.example.recipeapp.model.RecipeStep;
import com.example.recipeapp.service.RecipeStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipe-steps")
public class RecipeStepController {

    @Autowired
    private RecipeStepService recipeStepService;

    // Agregar un paso a una receta
    @PostMapping
    public ResponseEntity<RecipeStep> addRecipeStep(@RequestBody RecipeStep recipeStep) {
        RecipeStep created = recipeStepService.addRecipeStep(recipeStep);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Obtener los pasos de una receta ordenados por número de paso
    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<RecipeStep>> getStepsByRecipe(@PathVariable Long recipeId) {
        List<RecipeStep> steps = recipeStepService.getStepsByRecipeId(recipeId);
        return new ResponseEntity<>(steps, HttpStatus.OK);
    }

    // Actualizar un paso de la receta
    @PutMapping("/{id}")
    public ResponseEntity<RecipeStep> updateRecipeStep(@PathVariable Long id, @RequestBody RecipeStep recipeStep) {
        RecipeStep updated = recipeStepService.updateRecipeStep(recipeStep);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Eliminar un paso de la receta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipeStep(@PathVariable Long id) {
        recipeStepService.removeRecipeStep(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
