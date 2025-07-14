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

import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @PostMapping("/create")
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody RecipeRequest recipeRequest) {
        Recipe created = recipeService.createRecipe(recipeRequest);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/draft")
    public ResponseEntity<Recipe> saveDraft(@Valid @RequestBody RecipeRequest recipeRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Recipe draft = recipeService.saveDraftFromRequest(email, recipeRequest);
        return new ResponseEntity<>(draft, HttpStatus.CREATED);
    }

    @GetMapping("/drafts")
    public ResponseEntity<List<RecipeSummaryResponse>> listDrafts() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<RecipeSummaryResponse> summaries = recipeService.getMyDraftsSummary(email);
        return ResponseEntity.ok(summaries);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<Recipe> publishDraft(@PathVariable Long id) {
        if (!recipeService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Recipe published = recipeService.publish(id);
        return ResponseEntity.ok(published);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDetailResponse> getRecipeById(@PathVariable Long id) {
        Optional<RecipeDetailResponse> dtoOpt = recipeService.getRecipeDetailWithAverage(id);
        if (dtoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        RecipeDetailResponse dto = dtoOpt.get();

        // Si es borrador, solo su autor puede verlo
        if ("BORRADOR".equalsIgnoreCase(dto.getEstado())) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!recipeService.isOwner(id, email)) {
                throw new AccessDeniedException("No tiene permiso para ver este borrador");
            }
        }
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/draft")
    public ResponseEntity<Recipe> updateDraft(
            @PathVariable Long id,
            @Valid @RequestBody RecipeRequest recipeRequest) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!recipeService.existsById(id) || !recipeService.isOwner(id, email)) {
            return ResponseEntity.notFound().build();
        }
        Recipe updated = recipeService.updateDraftFromRequest(id, email, recipeRequest);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/full")
    public ResponseEntity<Recipe> getRecipeByIdWithCreator(@PathVariable Long id) {
        return recipeService.getRecipeByIdWithCreator(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/draft/full")
    public ResponseEntity<RecipeDetailResponse> syncDraftFull(
            @PathVariable Long id,
            @Valid @RequestBody RecipeRequest request
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        recipeService.syncDraftIngredientsAndSteps(id, email, request);
        // Reconstruyo el Response con el factory:
        RecipeDetailResponse dto = recipeService
                .getRecipeDetailWithAverage(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Recipe>> getRecipesByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(recipeService.getRecipesByEstado(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe) {
        if (!recipeService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipeService.updateRecipe(recipe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        if (!recipeService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllRecipes() {
        recipeService.deleteAllRecipes();
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllApprovedRecipes() {
        return ResponseEntity.ok(recipeService.getRecipesByEstado("APROBADO"));
    }

    @GetMapping("/summary")
    public ResponseEntity<List<RecipeSummaryResponse>> getAllRecipesSummary() {
        return ResponseEntity.ok(recipeService.getLatestApprovedPublishedSummaries());
    }

    @GetMapping("/saved")
    public ResponseEntity<List<UserSavedRecipeDTO>> listSavedRecipes() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(recipeService.getMySavedRecipesSummary(email));
    }

    @PutMapping("/{id}/save")
    public ResponseEntity<Void> saveRecipe(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        recipeService.saveRecipeForUser(id, email);
        return ResponseEntity.ok().build();
    }

    /**
     * Quita la receta de los guardados del usuario
     */
    @DeleteMapping("/{id}/save")
    public ResponseEntity<Void> unsaveRecipe(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        recipeService.unsaveRecipeForUser(id, email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/created")
    public ResponseEntity<List<RecipeSummaryResponse>> listMyPublished() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(recipeService.getMyPublishedSummaries(email));
    }

    // GET /recipes/search?name=&type=&ingredient=&excludeIngredient=&userAlias=&sort=
    @GetMapping("/search")
    public ResponseEntity<List<RecipeSummaryResponse>> searchRecipes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String ingredient,
            @RequestParam(required = false) String excludeIngredient,
            @RequestParam(required = false) String userAlias,
            @RequestParam(required = false, defaultValue = "name") String sort
    ) {
        List<RecipeSummaryResponse> results =
                recipeService.searchRecipes(name, type, ingredient, excludeIngredient, userAlias, sort);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/check-name")
    public ResponseEntity<RecipeSummaryResponse> checkRecipeName(@RequestParam String nombre) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        RecipeSummaryResponse response = recipeService.checkRecipeNameForUser(nombre, email);
        return ResponseEntity.ok(response);
    }
}