package com.example.recipeapp.model;

import jakarta.persistence.*;
import lombok.*;

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
    private String urlMedia; // list<String>

    private String titulo;


}
