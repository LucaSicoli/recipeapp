package com.example.recipeapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    private Integer puntos; // Por ejemplo, de 1 a 5.

    @Column(length = 2000)
    private String comentario;

    private LocalDateTime fecha;

    // Estado para moderar el comentario: "PENDIENTE", "APROBADA", "RECHAZADA"
    private String estado;
}
