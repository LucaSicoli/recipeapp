package com.example.recipeapp.model;

import com.example.recipeapp.config.UserSimpleSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;
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

    private long tiempo; // En minutos

    private Integer porciones;

    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    private EstadoAprobacion estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creador_id", nullable = false)
    @JsonSerialize(using = UserSimpleSerializer.class)
    private User usuarioCreador;

    private String fotoPrincipal;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    private TipoPlato tipoPlato;

    // Relación con ingredientes
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("recipe")
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    // NUEVO: Relación con pasos (steps)
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("recipe")
    private List<RecipeStep> steps = new ArrayList<>();
}
