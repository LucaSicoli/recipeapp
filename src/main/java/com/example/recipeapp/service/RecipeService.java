package com.example.recipeapp.service;

import com.example.recipeapp.model.EstadoAprobacion;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    // Método existente para crear una receta (como ejemplo)
    public Recipe createRecipe(Recipe recipe) {
        // Validar si el usuario ya tiene una receta con ese nombre
        Optional<Recipe> existente = recipeRepository.findByNombreAndUserId(recipe.getNombre(), recipe.getUsuarioCreador().getId());

        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe una receta con ese nombre para este usuario.");
        }

        recipe.setFechaCreacion(LocalDateTime.now());
        recipe.setEstado(EstadoAprobacion.PENDIENTE);
        return recipeRepository.save(recipe);
    }

    // Obtiene recetas que contengan un ingrediente específico
    public List<Recipe> getRecipesByIngredient(String ingredientName) {
        return recipeRepository.findRecipesByIngredient(ingredientName);
    }

    // Obtiene recetas que no contengan un ingrediente específico
    public List<Recipe> getRecipesWithoutIngredient(String ingredientName) {
        return recipeRepository.findRecipesWithoutIngredient(ingredientName);
    }

    //Obtiene recetas por alguna parte del nombre de la receta
    public List<Recipe> getRecipesByName(String namePart) {
        return recipeRepository.findByNameContainingOrderByFechaDesc(namePart);
    }

    public List<Recipe> getRecipesByTipoPlato(String tipoPlato, String orden) {
        switch (orden.toLowerCase()) {
            case "fecha":
                return recipeRepository.findByTipoPlatoOrderByFechaDesc(tipoPlato);
            case "usuario":
                return recipeRepository.findByTipoPlatoOrderByUsuarioAsc(tipoPlato);
            case "nombre":
            default:
                return recipeRepository.findByTipoPlatoOrderByNombreAsc(tipoPlato);
        }
    }

    public List<Recipe> getRecipesByNombreUsuario(String nombreUsuario, String orden) {
        switch (orden.toLowerCase()) {
            case "fecha":
                return recipeRepository.findByUserNombreOrderByFechaDesc(nombreUsuario);
            case "nombre":
            default:
                return recipeRepository.findByUserNombreOrderByNombreAsc(nombreUsuario);
        }
    }

    // Otros métodos de servicio existentes…
    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }

    public Recipe updateRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    public List<Recipe> getRecipesByEstado(String estado) {
        EstadoAprobacion estadoEnum = EstadoAprobacion.valueOf(estado.toUpperCase());
        return recipeRepository.findByEstado(estadoEnum);
    }
}
