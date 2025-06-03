package com.example.recipeapp.service;

import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.model.User;
import com.example.recipeapp.payload.CreateRatingRequest;
import com.example.recipeapp.model.Rating;
import com.example.recipeapp.payload.RatingResponse;
import com.example.recipeapp.repository.RatingRepository;
import com.example.recipeapp.repository.RecipeRepository;
import com.example.recipeapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeRepository recipeRepository;

    /**
     * Agrega un rating para una receta.
     * Si el usuario ya ha calificado esa receta, elimina el rating anterior y guarda el nuevo.
     */
    @Transactional
    public RatingResponse addRatingFromRequest(CreateRatingRequest request) {
        // Obtener al usuario autenticado
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        // Buscar la receta
        Recipe recipe = recipeRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        // Validar puntos
        if (request.getPuntos() < 1 || request.getPuntos() > 5) {
            throw new IllegalArgumentException("El valor de puntos debe estar entre 1 y 5.");
        }

        // Si ya existe un rating de este usuario para esta receta, elimínalo
        ratingRepository.findByUserIdAndRecipeId(user.getId(), recipe.getId())
                .ifPresent(existingRating -> ratingRepository.deleteById(existingRating.getId()));

        // Crear y guardar el nuevo rating
        Rating rating = new Rating();
        rating.setUser(user);
        rating.setRecipe(recipe);
        rating.setPuntos(request.getPuntos());
        rating.setComentario(request.getComentario());
        rating.setFecha(LocalDateTime.now());

        Rating saved = ratingRepository.save(rating);
        return mapToDTO(saved);
    }

    public List<RatingResponse> getRatingsDTOByRecipeId(Long recipeId) {
        List<Rating> ratings = ratingRepository.findByRecipeId(recipeId);
        return ratings.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private RatingResponse mapToDTO(Rating rating) {
        RatingResponse dto = new RatingResponse();
        dto.setId(rating.getId());
        dto.setUserAlias(rating.getUser().getAlias());
        dto.setRecipeNombre(rating.getRecipe().getNombre());
        dto.setPuntos(rating.getPuntos());
        dto.setComentario(rating.getComentario());
        dto.setFecha(rating.getFecha());
        return dto;
    }

    public Rating updateRating(Rating rating) {
        // Guarda cambios si quieres actualizar campos puntualmente
        return ratingRepository.save(rating);
    }

    public Double getAverageRatingForRecipe(Long recipeId) {
        return ratingRepository.findAverageRatingByRecipeId(recipeId);
    }

    public void removeRating(Long id) {
        ratingRepository.deleteById(id);
    }
}
