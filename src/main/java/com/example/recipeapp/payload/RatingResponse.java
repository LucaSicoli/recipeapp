package com.example.recipeapp.payload;

import com.example.recipeapp.model.EstadoAprobacion;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingResponse {
    private Long id;
    private String userAlias;
    private String recipeNombre;
    private Integer puntos;
    private String comentario;
    private LocalDateTime fecha;
    private EstadoAprobacion estadoAprobacion;
}
