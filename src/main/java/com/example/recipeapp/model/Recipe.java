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

    // Agregar la relación One-to-Many a RecipeIngredient
    // Usamos FetchType.EAGER para que se carguen automáticamente al obtener la receta,
    // pero también se podría usar LAZY y usar JOIN FETCH en el repository.
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("recipe") // Evita la recursión en la serialización
    private List<RecipeIngredient> ingredients = new ArrayList<>();
}
