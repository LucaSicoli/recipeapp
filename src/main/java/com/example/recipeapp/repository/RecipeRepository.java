package com.example.recipeapp.repository;

import com.example.recipeapp.model.EstadoAprobacion;
import com.example.recipeapp.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    // Consulta para encontrar recetas que contengan el ingrediente cuyo nombre es 'ingredientName'
    @Query("SELECT DISTINCT r FROM Recipe r " +
            "JOIN RecipeIngredient ri ON r.id = ri.recipe.id " +
            "JOIN Ingredient i ON ri.ingredient.id = i.id " +
            "WHERE i.nombre = :ingredientName")
    List<Recipe> findRecipesByIngredient(@Param("ingredientName") String ingredientName);

    // Consulta para encontrar recetas que NO contengan el ingrediente cuyo nombre es 'ingredientName'
    @Query("SELECT r FROM Recipe r " +
            "WHERE r.id NOT IN (" +
            "   SELECT ri.recipe.id FROM RecipeIngredient ri " +
            "   JOIN ri.ingredient i " +
            "   WHERE i.nombre = :ingredientName" +
            ")")
    List<Recipe> findRecipesWithoutIngredient(@Param("ingredientName") String ingredientName);

<<<<<<< HEAD
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.nombre) LIKE LOWER(CONCAT('%', :namePart, '%')) ORDER BY r.fechaCreacion DESC")
    List<Recipe> findByNameContainingOrderByFechaDesc(@Param("namePart") String namePart);

    // Orden por nombre (alfabéticamente, default) - Busca tipo Plato
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.tipoPlato) = LOWER(:tipoPlato) ORDER BY r.nombre ASC")
    List<Recipe> findByTipoPlatoOrderByNombreAsc(@Param("tipoPlato") String tipoPlato);

    // Orden por fecha de creación
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.tipoPlato) = LOWER(:tipoPlato) ORDER BY r.fechaCreacion DESC")
    List<Recipe> findByTipoPlatoOrderByFechaDesc(@Param("tipoPlato") String tipoPlato);

    // Orden por nombre del usuario
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.tipoPlato) = LOWER(:tipoPlato) ORDER BY r.user.nombre ASC")
    List<Recipe> findByTipoPlatoOrderByUsuarioAsc(@Param("tipoPlato") String tipoPlato);

    // Orden por nombre de la receta - Busca por usuario
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.usuarioCreador.nombre) = LOWER(:nombreUsuario) ORDER BY r.nombre ASC")
    List<Recipe> findByUserNombreOrderByNombreAsc(@Param("nombreUsuario") String nombreUsuario);

    // Orden por fecha de creación - Busca por usuario
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.usuarioCreador.nombre) = LOWER(:nombreUsuario) ORDER BY r.fechaCreacion DESC")
    List<Recipe> findByUserNombreOrderByFechaDesc(@Param("nombreUsuario") String nombreUsuario);

    //Buscara por usuario y nombre de la receta
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.nombre) = LOWER(:nombre) AND LOWER(r.usuarioCreador.nombre) = LOWER(:nombreUsuario)")
    Optional<Recipe> findByNombreAndUserId(@Param("nombre") String nombre, @Param("userId") Long userId);

=======
    // Método para buscar recetas por el estado (basado en la propiedad 'estado' de Recipe)
>>>>>>> c887bdf652b93c508466ea16ab69872ae29a1ae9
    List<Recipe> findByEstado(EstadoAprobacion estado);
}
