package com.example.recipeapp.payload;

import java.time.LocalDateTime;
import lombok.Data;


@Data
public class RatingDTO {
    private Long id;
    private Long userId;
    private String userAlias; // si quieres mostrar el alias del usuario
    private Long recipeId;
    private Integer puntos;
    private String comentario;
    private LocalDateTime fecha;

}
