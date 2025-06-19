package com.example.recipeapp.service;

import com.example.recipeapp.payload.UserSavedRecipeDTO;
import com.example.recipeapp.model.UserSavedRecipe;
import com.example.recipeapp.repository.UserSavedRecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserSavedRecipeService {

    private static final int MAX_SAVED_RECIPES = 10;

    @Autowired
    private UserSavedRecipeRepository userSavedRecipeRepository;

    public UserSavedRecipe addUserSavedRecipe(UserSavedRecipe userSavedRecipe) {
        Long userId = userSavedRecipe.getUser().getId();
        Long recipeId = userSavedRecipe.getRecipe().getId();

        // 1. Verificar si el usuario ya guardó esa receta (evitar duplicados)
        Optional<UserSavedRecipe> existing = userSavedRecipeRepository.findByUserIdAndRecipeId(userId, recipeId);
        if (existing.isPresent()) {
            // Lanza excepción o maneja el error de la forma que prefieras
            throw new RuntimeException("El usuario ya guardó esta receta anteriormente.");
        }

        // 2. Verificar si el usuario alcanzó el máximo de recetas guardadas
        List<UserSavedRecipe> savedRecipes = userSavedRecipeRepository.findByUserId(userId);
        if (savedRecipes.size() >= MAX_SAVED_RECIPES) {
            throw new RuntimeException("El usuario ya tiene el máximo permitido de recetas guardadas (" + MAX_SAVED_RECIPES + ").");
        }

        // 3. Asignar la fecha de agregado
        userSavedRecipe.setFechaAgregado(LocalDateTime.now());

        // 4. Guardar la nueva relación
        return userSavedRecipeRepository.save(userSavedRecipe);
    }

    public List<UserSavedRecipeDTO> getSavedRecipesDTOByUserId(Long userId) {
        List<UserSavedRecipe> list = userSavedRecipeRepository.findByUserId(userId);
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private UserSavedRecipeDTO mapToDto(UserSavedRecipe usr) {
        UserSavedRecipeDTO dto = new UserSavedRecipeDTO();
        dto.setId(usr.getId());
        dto.setRecipeId(usr.getRecipe().getId());
        dto.setRecipeNombre(usr.getRecipe().getNombre());
        dto.setFechaAgregado(usr.getFechaAgregado());
        return dto;
    }

    public void removeUserSavedRecipe(Long id) {
        userSavedRecipeRepository.deleteById(id);
    }

    public int countSavedRecipes(Long userId) {
        return userSavedRecipeRepository.countByUserId(userId);
    }
}
