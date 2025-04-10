package com.example.recipeapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    // Estados posibles: "PENDIENTE", "APROBADA", "RECHAZADA"
    private EstadoAprobacion estado;

    // Relación Many-to-One: muchas recetas pueden ser creadas por un mismo usuario.
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creador_id", nullable = false)
    private User usuarioCreador;

    // Opcional: URL de una imagen principal almacenada externamente.
    private String fotoPrincipal;

    private Categoria categoria;

    private TipoPlato tipoPlato;

}

