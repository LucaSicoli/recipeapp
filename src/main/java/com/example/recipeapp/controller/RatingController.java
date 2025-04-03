package com.example.recipeapp.controller;

import com.example.recipeapp.model.Rating;
import com.example.recipeapp.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    // Agregar una valoración a una receta
    @PostMapping
    public ResponseEntity<Rating> addRating(@RequestBody Rating rating) {
        Rating created = ratingService.addRating(rating);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Obtener valoraciones de una receta
    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<Rating>> getRatingsByRecipe(@PathVariable Long recipeId) {
        List<Rating> ratings = ratingService.getRatingsByRecipeId(recipeId);
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }

    // Actualizar una valoración
    @PutMapping("/{id}")
    public ResponseEntity<Rating> updateRating(@PathVariable Long id, @RequestBody Rating rating) {
        Rating updated = ratingService.updateRating(rating);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Eliminar una valoración
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        ratingService.removeRating(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
