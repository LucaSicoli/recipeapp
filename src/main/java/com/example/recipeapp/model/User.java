package com.example.recipeapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    private String URLfotoPerfil;
}
