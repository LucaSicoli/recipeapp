package com.example.recipeapp.service;

import com.example.recipeapp.model.RecipeStep;
import com.example.recipeapp.repository.RecipeStepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeStepService {

    @Autowired
    private RecipeStepRepository recipeStepRepository;

    public RecipeStep addRecipeStep(RecipeStep recipeStep) {
        return recipeStepRepository.save(recipeStep);
    }

    public List<RecipeStep> getStepsByRecipeId(Long recipeId) {
        return recipeStepRepository.findByRecipeIdOrderByNumeroPasoAsc(recipeId);
    }

    public RecipeStep updateRecipeStep(RecipeStep recipeStep) {
        return recipeStepRepository.save(recipeStep);
    }

    public void removeRecipeStep(Long id) {
        recipeStepRepository.deleteById(id);
    }
}
