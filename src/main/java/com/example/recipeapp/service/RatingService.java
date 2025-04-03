package com.example.recipeapp.service;

import com.example.recipeapp.model.Rating;
import com.example.recipeapp.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    public Rating addRating(Rating rating) {
        rating.setFecha(LocalDateTime.now());
        rating.setEstado("PENDIENTE");
        return ratingRepository.save(rating);
    }

    public List<Rating> getRatingsByRecipeId(Long recipeId) {
        return ratingRepository.findByRecipeId(recipeId);
    }

    public Rating updateRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    public void removeRating(Long id) {
        ratingRepository.deleteById(id);
    }
}
