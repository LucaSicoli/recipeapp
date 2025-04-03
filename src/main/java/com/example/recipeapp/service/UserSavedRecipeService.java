package com.example.recipeapp.service;

import com.example.recipeapp.model.UserSavedRecipe;
import com.example.recipeapp.repository.UserSavedRecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserSavedRecipeService {

    @Autowired
    private UserSavedRecipeRepository userSavedRecipeRepository;

    public UserSavedRecipe addUserSavedRecipe(UserSavedRecipe userSavedRecipe) {
        userSavedRecipe.setFechaAgregado(LocalDateTime.now());
        return userSavedRecipeRepository.save(userSavedRecipe);
    }

    public List<UserSavedRecipe> getSavedRecipesByUserId(Long userId) {
        return userSavedRecipeRepository.findByUserId(userId);
    }

    public void removeUserSavedRecipe(Long id) {
        userSavedRecipeRepository.deleteById(id);
    }
}
