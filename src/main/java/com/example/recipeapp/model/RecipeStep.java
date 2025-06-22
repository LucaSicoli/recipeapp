package com.example.recipeapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recipe_steps")
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    private Integer numeroPaso;  // Orden del paso en la receta.

    @Column(length = 2000)
    private String descripcion;

    // URL para una imagen o video del paso, almacenado externamente.
    @ElementCollection
    @CollectionTable(name = "step_media", joinColumns = @JoinColumn(name = "step_id"))
    @Column(name = "url")
    private List<String> mediaUrls = new ArrayList<>();

    private String titulo;


}
