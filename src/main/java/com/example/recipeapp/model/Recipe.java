package com.example.recipeapp.model;

import com.example.recipeapp.config.UserSimpleSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(length = 2000)
    private String descripcion;

    private long tiempo;

    private Integer porciones;

    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    private EstadoAprobacion estado;

    @Enumerated(EnumType.STRING)
    private EstadoPublicacion estadoPublicacion = EstadoPublicacion.BORRADOR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creador_id", nullable = false)
    @JsonSerialize(using = UserSimpleSerializer.class)
    private User usuarioCreador;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "recipe_media",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    @Column(name = "url")
    private List<String> mediaUrls = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    private TipoPlato tipoPlato;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("recipe")
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("recipe")
    private List<RecipeStep> steps = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserSavedRecipe> savedRecipes = new ArrayList<>();
}
