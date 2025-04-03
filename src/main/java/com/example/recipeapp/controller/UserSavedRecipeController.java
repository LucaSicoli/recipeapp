package com.example.recipeapp.controller;

import com.example.recipeapp.model.UserSavedRecipe;
import com.example.recipeapp.service.UserSavedRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-saved-recipes")
public class UserSavedRecipeController {

    @Autowired
    private UserSavedRecipeService userSavedRecipeService;

    // Agregar una receta a la lista guardada de un usuario
    @PostMapping
    public ResponseEntity<UserSavedRecipe> addUserSavedRecipe(@RequestBody UserSavedRecipe userSavedRecipe) {
        UserSavedRecipe created = userSavedRecipeService.addUserSavedRecipe(userSavedRecipe);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Obtener las recetas guardadas por un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserSavedRecipe>> getSavedRecipesByUser(@PathVariable Long userId) {
        List<UserSavedRecipe> savedRecipes = userSavedRecipeService.getSavedRecipesByUserId(userId);
        return new ResponseEntity<>(savedRecipes, HttpStatus.OK);
    }

    // Eliminar una receta guardada
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserSavedRecipe(@PathVariable Long id) {
        userSavedRecipeService.removeUserSavedRecipe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
