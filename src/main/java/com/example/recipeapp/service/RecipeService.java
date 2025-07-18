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
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
                    User creator = recipe.getUsuarioCreador();
                    String alias = creator.getAlias();
                    String foto  = creator.getUrlFotoPerfil();
                    Double avg   = ratingRepository.findAverageRatingByRecipeId(recipe.getId());
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
                            foto,
                            (avg != null) ? avg : 0.0,
                            recipe.getEstadoPublicacion().name(),
                            recipe.getEstado().name(),
                            recipe.getFechaCreacion() != null ? recipe.getFechaCreacion().toString() : null
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
        r.setEstado(EstadoAprobacion.PENDIENTE);
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
        // Solo traer recetas APROBADAS y PUBLICADAS para el home
        List<Recipe> recetas = recipeRepository.findByEstadoAndEstadoPublicacion(
            EstadoAprobacion.APROBADO,
            EstadoPublicacion.PUBLICADO
        );
        return recetas.stream()
                .sorted((a, b) -> b.getFechaCreacion().compareTo(a.getFechaCreacion()))
                .limit(3)
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
                        r.getUsuarioCreador().getUrlFotoPerfil(),
                        ratingRepository.findAverageRatingByRecipeId(r.getId()),
                        r.getEstadoPublicacion().name(),
                        r.getEstado().name(),
                        r.getFechaCreacion() != null ? r.getFechaCreacion().toString() : null
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
                        sr.getFechaAgregado().toString(),
                        sr.getRecipe().getMediaUrls()               // ← aquí
                ))
                .collect(Collectors.toList());
    }

    public List<RecipeSummaryResponse> getMyPublishedSummaries(String email) {
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
        return recipeRepository
                .findByUsuarioCreadorIdAndEstadoPublicacion(userId, EstadoPublicacion.PUBLICADO)
                .stream()
                .map(r -> {
                    User creator = r.getUsuarioCreador();
                    return new RecipeSummaryResponse(
                            r.getId(),
                            r.getNombre(),
                            r.getDescripcion(),
                            r.getMediaUrls(),
                            Long.valueOf(r.getTiempo()),
                            r.getPorciones(),
                            r.getTipoPlato().name(),
                            r.getCategoria().name(),
                            creator.getAlias(),
                            creator.getUrlFotoPerfil(),
                            ratingRepository.findAverageRatingByRecipeId(r.getId()),
                            r.getEstadoPublicacion().name(),
                            r.getEstado().name(),
                            r.getFechaCreacion() != null ? r.getFechaCreacion().toString() : null
                    );
                })
                .collect(Collectors.toList());
    }

    public List<RecipeSummaryResponse> getMyDraftsSummary(String email) {
        return getDraftsByUserEmail(email).stream()
                .map(r -> {
                    String firstMedia = r.getMediaUrls().isEmpty()
                            ? null
                            : r.getMediaUrls().get(0);
                    return new RecipeSummaryResponse(
                            r.getId(),
                            r.getNombre(),
                            r.getDescripcion(),
                            java.util.Collections.singletonList(firstMedia),
                            Long.valueOf(r.getTiempo()),
                            r.getPorciones(),
                            r.getTipoPlato().name(),
                            r.getCategoria().name(),
                            r.getUsuarioCreador().getAlias(),
                            r.getUsuarioCreador().getUrlFotoPerfil(),
                            ratingRepository.findAverageRatingByRecipeId(r.getId()),
                            r.getEstadoPublicacion().name(),
                            r.getEstado().name(),
                            r.getFechaCreacion() != null ? r.getFechaCreacion().toString() : null
                    );
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public void saveRecipeForUser(Long recipeId, String email) {
        User u = userRepository.findByEmail(email).orElseThrow();
        Recipe r = recipeRepository.findById(recipeId).orElseThrow();
        UserSavedRecipe sr = new UserSavedRecipe(u, r, LocalDateTime.now());
        userSavedRecipeRepository.save(sr);
    }

    @Transactional
    public void unsaveRecipeForUser(Long recipeId, String email) {
        User u = userRepository.findByEmail(email).orElseThrow();
        userSavedRecipeRepository.deleteByUserIdAndRecipeId(u.getId(), recipeId);
    }

    public List<RecipeSummaryResponse> searchRecipes(
            String name,
            String type,
            String ingredient,
            String excludeIngredient,
            String userAlias,
            String sort
    ) {
        Specification<Recipe> spec = Specification.where(null);

        // ────────────────────────────────
        // Sólo recetas aprobadas y publicadas
        // ────────────────────────────────
        spec = spec
                .and((root, cq, cb) ->
                        cb.equal(
                                root.get("estado"),
                                EstadoAprobacion.APROBADO
                        )
                )
                .and((root, cq, cb) ->
                        cb.equal(
                                root.get("estadoPublicacion"),
                                EstadoPublicacion.PUBLICADO
                        )
                );

        if (name != null && !name.isBlank()) {
            spec = spec.and((root, cq, cb) ->
                    cb.like(
                            cb.lower(root.get("nombre")),
                            "%" + name.toLowerCase() + "%"
                    )
            );
        }

        if (type != null && !type.isBlank()) {
            spec = spec.and((root, cq, cb) ->
                    cb.equal(
                            root.get("tipoPlato"),
                            TipoPlato.valueOf(type.toUpperCase())
                    )
            );
        }

        if (ingredient != null && !ingredient.isBlank()) {
            spec = spec.and((root, cq, cb) -> {
                Join<?,?> joinIng = root.join("ingredients", JoinType.INNER);
                return cb.equal(
                        cb.lower(joinIng.get("ingredient").get("nombre")),
                        ingredient.toLowerCase()
                );
            });
        }

        if (excludeIngredient != null && !excludeIngredient.isBlank()) {
            spec = spec.and((root, cq, cb) -> {
                Join<?,?> joinIng = root.join("ingredients", JoinType.LEFT);
                return cb.notEqual(
                        cb.lower(joinIng.get("ingredient").get("nombre")),
                        excludeIngredient.toLowerCase()
                );
            });
        }

        if (userAlias != null && !userAlias.isBlank()) {
            spec = spec.and((root, cq, cb) ->
                    cb.equal(
                            cb.lower(root.get("usuarioCreador").get("alias")),
                            userAlias.toLowerCase()
                    )
            );
        }

        // ────────────────────────────────
        // Ordenamiento
        // ────────────────────────────────
        Sort sortOrder = Sort.by("nombre"); // alfabético por defecto
        if ("newest".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "fechaCreacion");
        } else if ("user".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by(Sort.Direction.ASC, "usuarioCreador.alias");
        }

        // ────────────────────────────────
        // Ejecuta la consulta y mapea
        // ────────────────────────────────
        List<Recipe> recipes = recipeRepository.findAll(spec, sortOrder);
        return recipes.stream()
                .map(r -> {
                    Double avg = ratingRepository.findAverageRatingByRecipeId(r.getId());
                    return new RecipeSummaryResponse(
                            r.getId(),
                            r.getNombre(),
                            r.getDescripcion(),
                            r.getMediaUrls(),
                            Long.valueOf(r.getTiempo()),
                            r.getPorciones(),
                            r.getTipoPlato().name(),
                            r.getCategoria().name(),
                            r.getUsuarioCreador().getAlias(),
                            r.getUsuarioCreador().getUrlFotoPerfil(),
                            avg != null ? avg : 0.0,
                            r.getEstadoPublicacion().name(),
                            r.getEstado().name(),
                            r.getFechaCreacion() != null ? r.getFechaCreacion().toString() : null
                    );
                })
                .collect(java.util.stream.Collectors.toList());
    }

    public RecipeNameCheckResponse checkRecipeNameForUser(String nombre, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<Recipe> recetas = recipeRepository.findByUsuarioCreadorId(user.getId());
        for (Recipe r : recetas) {
            if (r.getNombre().equalsIgnoreCase(nombre)) {
                Double avg = ratingRepository.findAverageRatingByRecipeId(r.getId());
                RecipeSummaryResponse existingRecipe = new RecipeSummaryResponse(
                    r.getId(),
                    r.getNombre(),
                    r.getDescripcion(),
                    r.getMediaUrls(),
                    Long.valueOf(r.getTiempo()),
                    r.getPorciones(),
                    r.getTipoPlato() != null ? r.getTipoPlato().name() : null,
                    r.getCategoria() != null ? r.getCategoria().name() : null,
                    r.getUsuarioCreador().getAlias(),
                    r.getUsuarioCreador().getUrlFotoPerfil(),
                    avg != null ? avg : 0.0,
                    r.getEstadoPublicacion() != null ? r.getEstadoPublicacion().name() : null,
                    r.getEstado() != null ? r.getEstado().name() : null,
                    r.getFechaCreacion() != null ? r.getFechaCreacion().toString() : null
                );
                String message = "Ya tienes una receta con el nombre '" + nombre + "'. " +
                               "¿Deseas reemplazar la receta existente o editarla?";
                return new RecipeNameCheckResponse(true, message, existingRecipe);
            }
        }
        String message = "El nombre '" + nombre + "' está disponible. Puedes crear tu receta.";
        return new RecipeNameCheckResponse(false, message, null);
    }

    @Transactional
    public Recipe replaceRecipe(Long existingRecipeId, String email, RecipeRequest newRecipeData) {
        Recipe existing = recipeRepository.findById(existingRecipeId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        if (!isOwner(existingRecipeId, email)) {
            throw new AccessDeniedException("No tienes permiso para reemplazar esta receta");
        }

        // Limpiar ingredientes y pasos existentes
        existing.getIngredients().clear();
        existing.getSteps().clear();

        // Actualizar todos los campos con los nuevos datos
        existing.setNombre(newRecipeData.getNombre());
        existing.setDescripcion(newRecipeData.getDescripcion());
        existing.setTiempo(newRecipeData.getTiempo());
        existing.setPorciones(newRecipeData.getPorciones());
        existing.setTipoPlato(TipoPlato.valueOf(newRecipeData.getTipoPlato().toUpperCase()));
        existing.setCategoria(Categoria.valueOf(newRecipeData.getCategoria().toUpperCase()));
        existing.setMediaUrls(newRecipeData.getMediaUrls());
        existing.setFechaCreacion(LocalDateTime.now()); // Nueva fecha de creación
        existing.setEstado(EstadoAprobacion.PENDIENTE); // Vuelve a revisión
        existing.setEstadoPublicacion(EstadoPublicacion.BORRADOR); // Comienza como borrador

        // Recrear ingredientes
        if (newRecipeData.getIngredients() != null) {
            for (RecipeIngredientRequest ir : newRecipeData.getIngredients()) {
                RecipeIngredient ri = recipeIngredientFactory.createRecipeIngredient(ir, existing);
                existing.getIngredients().add(ri);
            }
        }

        // Recrear pasos
        if (newRecipeData.getSteps() != null) {
            int numero = 1;
            for (RecipeStepRequest sr : newRecipeData.getSteps()) {
                sr.setNumeroPaso(numero++);
                RecipeStep rs = recipeStepFactory.createRecipeStep(sr, existing);
                existing.getSteps().add(rs);
            }
        }

        return recipeRepository.save(existing);
    }
}

