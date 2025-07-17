package com.example.recipeapp.repository;

import com.example.recipeapp.model.EstadoAprobacion;
import com.example.recipeapp.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    // Obtener valoraciones de una receta específica
    List<Rating> findByRecipeId(Long recipeId);

    // Obtener solo valoraciones aprobadas de una receta específica
    List<Rating> findByRecipeIdAndEstadoAprobacion(Long recipeId, EstadoAprobacion estadoAprobacion);

    // Método para verificar si ya existe un rating para un usuario y una receta
    Optional<Rating> findByUserIdAndRecipeId(Long userId, Long recipeId);

    // Calcular el promedio de ratings SOLO de los APROBADOS para una receta
    @Query("SELECT AVG(r.puntos) FROM Rating r WHERE r.recipe.id = :recipeId AND r.estadoAprobacion = 'APROBADO'")
    Double findAverageRatingByRecipeId(@Param("recipeId") Long recipeId);

    long countByUserId(Long userId);

    // Obtener ratings pendientes de aprobación para administración
    List<Rating> findByEstadoAprobacion(EstadoAprobacion estadoAprobacion);
}
