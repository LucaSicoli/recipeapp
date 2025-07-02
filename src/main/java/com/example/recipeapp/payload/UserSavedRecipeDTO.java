package com.example.recipeapp.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserSavedRecipeDTO {
    private Long id;
    private Long recipeId;
    private String recipeNombre;
    private String fechaAgregado;
    private List<String> mediaUrls;

    public UserSavedRecipeDTO() {}

    public UserSavedRecipeDTO(
            Long id,
            Long recipeId,
            String recipeNombre,
            String fechaAgregado,
            List<String> mediaUrls
    ) {
        this.id             = id;
        this.recipeId       = recipeId;
        this.recipeNombre   = recipeNombre;
        this.fechaAgregado  = fechaAgregado;
        this.mediaUrls      = mediaUrls;
    }

}