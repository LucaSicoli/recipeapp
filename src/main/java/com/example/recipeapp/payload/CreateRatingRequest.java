package com.example.recipeapp.payload;

import lombok.Data;

@Data
public class CreateRatingRequest {
    private Long recipeId;
    private Integer puntos;
    private String comentario;
}
