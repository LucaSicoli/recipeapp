package com.example.recipeapp.controller;

import com.example.recipeapp.model.Ingredient;
import com.example.recipeapp.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    // Crear un ingrediente
    @PostMapping
    public ResponseEntity<Ingredient> createIngredient(@RequestBody Ingredient ingredient) {
        Ingredient created = ingredientService.createIngredient(ingredient);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Obtener un ingrediente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id) {
        Optional<Ingredient> ingredient = ingredientService.getIngredientById(id);
        return ingredient.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Actualizar un ingrediente
    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(
            @PathVariable Long id,
            @RequestBody Ingredient incoming) {

        return ingredientService.getIngredientById(id)
                .map(existing -> {
                    existing.setNombre(incoming.getNombre());
                    // si tu entidad tuviera más campos, los ajustas aquí...
                    Ingredient updated = ingredientService.updateIngredient(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Eliminar un ingrediente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
