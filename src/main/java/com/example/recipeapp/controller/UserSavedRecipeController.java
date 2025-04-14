package com.example.recipeapp.controller;

import com.example.recipeapp.payload.UserSavedRecipeDTO;
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

    @PostMapping
    public ResponseEntity<?> addUserSavedRecipe(@RequestBody UserSavedRecipe userSavedRecipe) {
            UserSavedRecipe created = userSavedRecipeService.addUserSavedRecipe(userSavedRecipe);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserSavedRecipeDTO>> getSavedRecipesByUser(@PathVariable Long userId) {
        List<UserSavedRecipeDTO> savedRecipes = userSavedRecipeService.getSavedRecipesDTOByUserId(userId);
        return new ResponseEntity<>(savedRecipes, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserSavedRecipe(@PathVariable Long id) {
        userSavedRecipeService.removeUserSavedRecipe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
