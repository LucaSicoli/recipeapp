package com.example.recipeapp.controller;

import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    // Crear una receta
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        Recipe created = recipeService.createRecipe(recipe);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Obtener recetas por estado de aprobación
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Recipe>> getRecipesByEstado(@PathVariable String estado) {
        List<Recipe> recipes = recipeService.getRecipesByEstado(estado);
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    // Obtener una receta por ID
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        Optional<Recipe> recipe = recipeService.getRecipeById(id);
        return recipe.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Buscar recetas por nombre (o parte del nombre), ordenadas de más nuevas a más antiguas
    @GetMapping("/buscar")
    public ResponseEntity<List<Recipe>> getRecipesByName(@RequestParam String nombre) {
        List<Recipe> recipes = recipeService.getRecipesByName(nombre);
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    @GetMapping("/tipo")
    public ResponseEntity<List<Recipe>> getRecipesByTipoPlato(
            @RequestParam String tipoPlato,
            @RequestParam(required = false, defaultValue = "nombre") String orden) {

        List<Recipe> recipes = recipeService.getRecipesByTipoPlato(tipoPlato, orden);
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }


    /*
    GET /recipes/tipo?tipoPlato=pasta&orden=fecha
    GET /recipes/tipo?tipoPlato=ensalada&orden=usuario
    GET /recipes/tipo?tipoPlato=postre
    Ejemplos endpoints para agarrar tipo de plato y ordenarlo
    */


    @GetMapping("/usuario")
    public ResponseEntity<List<Recipe>> getRecipesByNombreUsuario(
            @RequestParam String nombre,
            @RequestParam(required = false, defaultValue = "nombre") String orden) {

        List<Recipe> recipes = recipeService.getRecipesByNombreUsuario(nombre, orden);
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    /*
        GET /recipes/usuario?nombre=juan&orden=fecha
        GET /recipes/usuario?nombre=camila

     */

    // Actualizar una receta
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe) {
        Recipe updated = recipeService.updateRecipe(recipe);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Eliminar una receta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
