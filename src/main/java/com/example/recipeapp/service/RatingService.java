package com.example.recipeapp.service;

import com.example.recipeapp.payload.RatingDTO;
import com.example.recipeapp.model.Rating;
import com.example.recipeapp.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    /**
     * Agrega un rating para una receta.
     * Si el usuario ya ha calificado esa receta, lanza una excepción con un mensaje indicándolo.
     */
    public Rating addRating(Rating rating) {
        // Validar que los puntos estén entre 1 y 5.
        if (rating.getPuntos() == null || rating.getPuntos() < 1 || rating.getPuntos() > 5) {
            throw new IllegalArgumentException("El valor de puntos debe estar entre 1 y 5.");
        }

        // Verificar si el usuario ya ha calificado la misma receta.
        Optional<Rating> existingRating = ratingRepository.findByUserIdAndRecipeId(
                rating.getUser().getId(), rating.getRecipe().getId());
        if (existingRating.isPresent()) {
            throw new RuntimeException("El usuario ya ha calificado esta receta. Por favor, actualiza el rating si deseas modificarlo.");
        }

        rating.setFecha(LocalDateTime.now());
        return ratingRepository.save(rating);
    }

    public List<RatingDTO> getRatingsDTOByRecipeId(Long recipeId) {
        List<Rating> ratings = ratingRepository.findByRecipeId(recipeId);
        return ratings.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private RatingDTO mapToDTO(Rating rating) {
        RatingDTO dto = new RatingDTO();
        dto.setId(rating.getId());
        dto.setUserId(rating.getUser().getId());
        dto.setUserAlias(rating.getUser().getAlias());
        dto.setRecipeId(rating.getRecipe().getId());
        dto.setPuntos(rating.getPuntos());
        dto.setComentario(rating.getComentario());
        dto.setFecha(rating.getFecha());
        return dto;
    }

    public Rating updateRating(Rating rating) {
        // Puedes agregar validaciones adicionales si se requiere
        return ratingRepository.save(rating);
    }

    public Double getAverageRatingForRecipe(Long recipeId) {
        return ratingRepository.findAverageRatingByRecipeId(recipeId);
    }

    public void removeRating(Long id) {
        ratingRepository.deleteById(id);
    }
}
