package com.example.recipeapp.controller;

import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.payload.RecipeDetailResponse;
import com.example.recipeapp.payload.RecipeRequest;
import com.example.recipeapp.payload.RecipeSummaryResponse;
import com.example.recipeapp.payload.UserSavedRecipeDTO;
import com.example.recipeapp.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    // 1) Crear una receta (envía directamente para aprobación)
    @PostMapping("/create")
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody RecipeRequest recipeRequest) {
        Recipe created = recipeService.createRecipe(recipeRequest);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 2) Guardar un borrador de receta
    @PostMapping("/draft")
    public ResponseEntity<Recipe> saveDraft(@Valid @RequestBody RecipeRequest recipeRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Recipe draft = recipeService.saveDraftFromRequest(email, recipeRequest);
        return new ResponseEntity<>(draft, HttpStatus.CREATED);
    }

    // 3) Listar todos los borradores del usuario actual
    @GetMapping("/drafts")
    public ResponseEntity<List<RecipeSummaryResponse>> listDrafts() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<RecipeSummaryResponse> summaries = recipeService.getMyDraftsSummary(email);
        return ResponseEntity.ok(summaries);
    }

    // 4) Publicar un borrador existente
    @PutMapping("/{id}/publish")
    public ResponseEntity<Recipe> publishDraft(@PathVariable Long id) {
        if (!recipeService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Recipe published = recipeService.publish(id);
        return ResponseEntity.ok(published);
    }

    // 5) Obtener receta completa (incluye datos del creador)
    @GetMapping("/{id}/full")
    public ResponseEntity<Recipe> getRecipeByIdWithCreator(@PathVariable Long id) {
        Optional<Recipe> opt = recipeService.getRecipeByIdWithCreator(id);
        return opt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 6) Filtrar recetas por estado de aprobación
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Recipe>> getRecipesByEstado(@PathVariable String estado) {
        List<Recipe> recipes = recipeService.getRecipesByEstado(estado);
        return ResponseEntity.ok(recipes);
    }

    // 7) Obtener detalle de receta con promedio de rating
    @GetMapping("/{id}")
    public ResponseEntity<RecipeDetailResponse> getRecipeById(@PathVariable Long id) {
        Optional<RecipeDetailResponse> dto = recipeService.getRecipeDetailWithAverage(id);
        return dto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 8) Actualizar receta
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe) {
        if (!recipeService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Recipe updated = recipeService.updateRecipe(recipe);
        return ResponseEntity.ok(updated);
    }

    // 9) Eliminar una receta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        if (!recipeService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    // 10) Eliminar todas las recetas
    @DeleteMapping
    public ResponseEntity<Void> deleteAllRecipes() {
        recipeService.deleteAllRecipes();
        return ResponseEntity.noContent().build();
    }

    // 11) Listar todas las recetas aprobadas
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllApprovedRecipes() {
        List<Recipe> recipes = recipeService.getRecipesByEstado("APROBADO");
        return ResponseEntity.ok(recipes);
    }

    // 12) Nuevo: resumen de todas las recetas con alias de creador y promedio de rating
    @GetMapping("/summary")
    public ResponseEntity<List<RecipeSummaryResponse>> getAllRecipesSummary() {
        return ResponseEntity.ok(
                recipeService.getLatestApprovedPublishedSummaries()
        );
    }

    @GetMapping("/saved")
    public ResponseEntity<List<UserSavedRecipeDTO>> listSavedRecipes() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(recipeService.getMySavedRecipesSummary(email));
    }

}