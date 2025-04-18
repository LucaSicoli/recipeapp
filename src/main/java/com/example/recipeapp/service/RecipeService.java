package com.example.recipeapp.service;

import com.example.recipeapp.model.EstadoAprobacion;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.model.User;
import com.example.recipeapp.repository.RecipeRepository;
import com.example.recipeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;

    // Método existente para crear una receta (como ejemplo)
    public Recipe createRecipe(Recipe recipe) {

        // Validar si el usuario ya tiene una receta con ese nombre
        Optional<Recipe> existente = recipeRepository.findByNombreAndUserId(recipe.getNombre(), recipe.getUsuarioCreador().getId());

        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe una receta con ese nombre para este usuario.");
        }


        // Establece la fecha de creación y el estado

        recipe.setFechaCreacion(LocalDateTime.now());
        recipe.setEstado(EstadoAprobacion.PENDIENTE);

        // Obtén el usuario autenticado del contexto de seguridad
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            recipe.setUsuarioCreador(optionalUser.get());
        } else {
            // Maneja el caso en que el usuario autenticado no se encuentre (posiblemente lanza una excepción)
            throw new RuntimeException("Usuario autenticado no encontrado en la base de datos");
        }

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
