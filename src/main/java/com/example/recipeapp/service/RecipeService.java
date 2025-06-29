package com.example.recipeapp.service;

import com.example.recipeapp.factory.RecipeFactory;
import com.example.recipeapp.factory.RecipeIngredientFactory;
import com.example.recipeapp.factory.RecipeStepFactory;
import com.example.recipeapp.model.*;
import com.example.recipeapp.payload.*;
import com.example.recipeapp.repository.RatingRepository;
import com.example.recipeapp.repository.RecipeRepository;
import com.example.recipeapp.repository.UserRepository;
import com.example.recipeapp.repository.UserSavedRecipeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private RecipeIngredientService recipeIngredientService;

    @Autowired
    private RecipeStepService recipeStepService;            // <-- corregido

    @Autowired
    private RecipeFactory recipeFactory;

    @Autowired
    private RecipeStepFactory recipeStepFactory;

    @Autowired
    private RecipeIngredientFactory recipeIngredientFactory;

    @Autowired
    private UserSavedRecipeRepository userSavedRecipeRepository;

    // ------------------------------------------------------------
    // Crear receta
    // ------------------------------------------------------------
    public Recipe createRecipe(RecipeRequest request) {
        Recipe r = recipeFactory.createRecipeFromRequest(request);
        r.setFechaCreacion(LocalDateTime.now());
        r.setEstado(EstadoAprobacion.PENDIENTE);
        r.setEstadoPublicacion(EstadoPublicacion.PUBLICADO);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        r.setUsuarioCreador(u);

        return recipeRepository.save(r);
    }

    // ------------------------------------------------------------
    // Listado con alias y promedio de rating
    // ------------------------------------------------------------
    public List<RecipeSummaryResponse> getAllRecipesWithAverage() {
        return recipeRepository.findAll().stream()
                .map(recipe -> {
                    String alias = recipe.getUsuarioCreador().getAlias();
                    Double avg = ratingRepository.findAverageRatingByRecipeId(recipe.getId());
                    return new RecipeSummaryResponse(
                            recipe.getId(),
                            recipe.getNombre(),
                            recipe.getDescripcion(),
                            recipe.getMediaUrls(),
                            recipe.getTiempo(),
                            recipe.getPorciones(),
                            recipe.getTipoPlato().name(),
                            recipe.getCategoria().name(),
                            alias,
                            (avg != null) ? avg : 0.0
                    );
                })
                .collect(Collectors.toList());
    }

    // -----------------------------------------------
    // Búsquedas y operaciones básicas
    // -----------------------------------------------
    public List<Recipe> getRecipesByIngredient(String ingredientName) {
        return recipeRepository.findRecipesByIngredient(ingredientName);
    }

    public List<Recipe> getRecipesWithoutIngredient(String ingredientName) {
        return recipeRepository.findRecipesWithoutIngredient(ingredientName);
    }

    public Optional<Recipe> getRecipeByIdWithCreator(Long id) {
        return recipeRepository.findByIdWithUsuarioCreador(id);
    }

    public boolean existsById(Long id) {
        return recipeRepository.existsById(id);
    }

    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }

    public Recipe updateRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllRecipes() {
        recipeRepository.deleteAll();
    }

    public Optional<RecipeDetailResponse> getRecipeDetailWithAverage(Long id) {
        Optional<Recipe> recipeOpt = recipeRepository.findByIdWithUsuarioCreador(id);
        if (recipeOpt.isEmpty()) return Optional.empty();
        Recipe receta = recipeOpt.get();
        Double avg = ratingRepository.findAverageRatingByRecipeId(id);
        RecipeDetailResponse dto = RecipeDetailResponse.fromEntity(receta, (avg != null) ? avg : 0.0);
        return Optional.of(dto);
    }

    public List<Recipe> getRecipesByEstado(String estado) {
        EstadoAprobacion estadoEnum = EstadoAprobacion.valueOf(estado.toUpperCase());
        return recipeRepository.findByEstado(estadoEnum);
    }

    // ------------------------------------------------------------
    // Borradores
    // ------------------------------------------------------------
    public Recipe saveDraftFromRequest(String email, RecipeRequest request) {
        Recipe draft = recipeFactory.createRecipeFromRequest(request);
        draft.setFechaCreacion(LocalDateTime.now());
        draft.setEstadoPublicacion(EstadoPublicacion.BORRADOR);
        draft.setEstado(EstadoAprobacion.PENDIENTE);

        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        draft.setUsuarioCreador(u);

        return recipeRepository.save(draft);
    }

    public List<Recipe> getDraftsByUserEmail(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return recipeRepository.findByUsuarioCreadorIdAndEstadoPublicacion(
                u.getId(), EstadoPublicacion.BORRADOR);
    }

    public Recipe publish(Long recipeId) {
        Recipe r = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        r.setEstadoPublicacion(EstadoPublicacion.PUBLICADO);
        return recipeRepository.save(r);
    }

    public boolean isOwner(Long recipeId, String email) {
        return recipeRepository.findById(recipeId)
                .map(r -> r.getUsuarioCreador().getEmail().equalsIgnoreCase(email))
                .orElse(false);
    }

    @Transactional
    public Recipe updateDraftFromRequest(Long id, String email, RecipeRequest request) {
        Recipe existing = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        if (!isOwner(id, email)) {
            throw new AccessDeniedException("No tienes permiso para editar este borrador");
        }
        existing.setNombre(request.getNombre());
        existing.setDescripcion(request.getDescripcion());
        existing.setTiempo(request.getTiempo());
        existing.setPorciones(request.getPorciones());
        existing.setTipoPlato(TipoPlato.valueOf(request.getTipoPlato().toUpperCase()));
        existing.setCategoria(Categoria.valueOf(request.getCategoria().toUpperCase()));
        existing.setMediaUrls(request.getMediaUrls());
        existing.setEstadoPublicacion(EstadoPublicacion.BORRADOR);
        existing.setEstado(EstadoAprobacion.PENDIENTE);
        return recipeRepository.save(existing);
    }

    public List<RecipeSummaryResponse> getMyDraftsSummary(String email) {
        return getDraftsByUserEmail(email).stream()
                .map(r -> new RecipeSummaryResponse(
                        r.getId(),
                        r.getNombre(),
                        r.getDescripcion(),
                        Collections.singletonList(r.getMediaUrls().isEmpty() ? null : r.getMediaUrls().get(0)),
                        r.getTiempo(),
                        r.getPorciones(),
                        r.getTipoPlato().name(),
                        r.getCategoria().name(),
                        r.getUsuarioCreador().getAlias(),
                        ratingRepository.findAverageRatingByRecipeId(r.getId())
                ))
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------
    // Nuevo método: sincronizar ingredientes y pasos de un borrador
    // ------------------------------------------------------------
    @Transactional
    public Recipe syncDraftIngredientsAndSteps(Long id, String email, RecipeRequest request) {
        Recipe existing = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        if (!isOwner(id, email)) {
            throw new AccessDeniedException("No tienes permiso para editar este borrador");
        }

        // 1) Actualizo los campos simples de la receta
        existing.setNombre(request.getNombre());
        existing.setDescripcion(request.getDescripcion());
        existing.setTiempo(request.getTiempo());
        existing.setPorciones(request.getPorciones());
        existing.setCategoria(Categoria.valueOf(request.getCategoria().toUpperCase()));
        existing.setTipoPlato(TipoPlato.valueOf(request.getTipoPlato().toUpperCase()));
        existing.setMediaUrls(request.getMediaUrls());  // portada o vídeo

        // 2) Limpio ingredientes y pasos para que orphanRemoval los borre
        existing.getIngredients().clear();
        existing.getSteps().clear();

        // 3) Recréo ingredientes
        if (request.getIngredients() != null) {
            for (RecipeIngredientRequest ir : request.getIngredients()) {
                RecipeIngredient ri = recipeIngredientFactory.createRecipeIngredient(ir, existing);
                existing.getIngredients().add(ri);
            }
        }

        // 4) Recréo pasos
        if (request.getSteps() != null) {
            int numero = 1;
            for (RecipeStepRequest sr : request.getSteps()) {
                sr.setNumeroPaso(numero++);
                RecipeStep rs = recipeStepFactory.createRecipeStep(sr, existing);
                existing.getSteps().add(rs);
            }
        }

        existing.setEstadoPublicacion(EstadoPublicacion.BORRADOR);
        // 5) Guardo todo de una vez
        return recipeRepository.save(existing);
    }

    // ------------------------------------------------------------
    // Otros listados
    // ------------------------------------------------------------
    public List<RecipeSummaryResponse> getLatestApprovedPublishedSummaries() {
        return recipeRepository
                .findTop3ByEstadoAndEstadoPublicacionOrderByFechaCreacionDesc(
                        EstadoAprobacion.APROBADO,
                        EstadoPublicacion.PUBLICADO)
                .stream()
                .map(r -> new RecipeSummaryResponse(
                        r.getId(), r.getNombre(), r.getDescripcion(), r.getMediaUrls(),
                        r.getTiempo(), r.getPorciones(), r.getTipoPlato().name(),
                        r.getCategoria().name(), r.getUsuarioCreador().getAlias(),
                        ratingRepository.findAverageRatingByRecipeId(r.getId())
                ))
                .collect(Collectors.toList());
    }

    public List<UserSavedRecipeDTO> getMySavedRecipesSummary(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return userSavedRecipeRepository.findByUserId(u.getId()).stream()
                .map(sr -> new UserSavedRecipeDTO(
                        sr.getId(),
                        sr.getRecipe().getId(),
                        sr.getRecipe().getNombre(),
                        sr.getFechaAgregado().toString()
                ))
                .collect(Collectors.toList());
    }

    public List<RecipeSummaryResponse> getMyPublishedSummaries(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return recipeRepository
                .findByUsuarioCreadorIdAndEstadoPublicacion(u.getId(), EstadoPublicacion.PUBLICADO)
                .stream()
                .map(r -> new RecipeSummaryResponse(
                        r.getId(),
                        r.getNombre(),
                        r.getDescripcion(),
                        r.getMediaUrls(),
                        r.getTiempo(),
                        r.getPorciones(),
                        r.getTipoPlato().name(),
                        r.getCategoria().name(),
                        r.getUsuarioCreador().getAlias(),
                        ratingRepository.findAverageRatingByRecipeId(r.getId())
                ))
                .collect(Collectors.toList());
    }
}