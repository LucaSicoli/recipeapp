package com.example.recipeapp.controller;

import com.example.recipeapp.payload.RatingDTO;
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

    // Agregar un rating
    @PostMapping
    public ResponseEntity<Rating> addRating(@RequestBody Rating rating) {
        Rating created = ratingService.addRating(rating);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }


    // Obtener valoraciones de una receta, pero devolviendo DTO
    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<RatingDTO>> getRatingsByRecipe(@PathVariable Long recipeId) {
        List<RatingDTO> ratings = ratingService.getRatingsDTOByRecipeId(recipeId);
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }

    // Actualizar un rating
    @PutMapping("/{id}")
    public ResponseEntity<Rating> updateRating(@PathVariable Long id, @RequestBody Rating rating) {
        // Asumiendo que el ID se respeta o que la lógica de actualización no depende del ID del path
        Rating updated = ratingService.updateRating(rating);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Eliminar un rating
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        ratingService.removeRating(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/recipe/{recipeId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long recipeId) {
        Double avg = ratingService.getAverageRatingForRecipe(recipeId);
        // Puedes retornar 0.0 en caso de que no haya ratings, si lo prefieres
        return new ResponseEntity<>(avg != null ? avg : 0.0, HttpStatus.OK);
    }
}
