package com.example.recipeapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String alias; // Alias único del usuario

    @Column(nullable = false)
    private String password;

    private String nombre; // Nombre completo (opcional)

    private String rol; // Puedes definir un Enum para roles si lo prefieres

    private LocalDateTime fechaCreacion;

    private Boolean activo = true;

    private String urlFotoPerfil;  // Recomiendo camelCase

    // Relación bidireccional: un usuario puede tener varias recetas guardadas
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserSavedRecipe> savedRecipes = new ArrayList<>();



}
