package com.example.recipeapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_saved_recipes")
public class UserSavedRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    private LocalDateTime fechaAgregado;

    public UserSavedRecipe(User user, Recipe recipe, LocalDateTime fechaAgregado) {
        this.user = user;
        this.recipe = recipe;
        this.fechaAgregado = fechaAgregado;
    }
}
