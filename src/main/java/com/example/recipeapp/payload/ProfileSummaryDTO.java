// src/main/java/com/example/recipeapp/payload/ProfileSummaryDTO.java

package com.example.recipeapp.payload;

public class ProfileSummaryDTO {
    private Long userId;
    private int recetasPublicadas;
    private int recetasGuardadas;
    private int reseñasPublicadas;

    public ProfileSummaryDTO(Long userId, int recetasPublicadas, int recetasGuardadas, int reseñasPublicadas) {
        this.userId = userId;
        this.recetasPublicadas = recetasPublicadas;
        this.recetasGuardadas = recetasGuardadas;
        this.reseñasPublicadas = reseñasPublicadas;
    }

    // Getters y setters

    public Long getUserId() {
        return userId;
    }

    public int getRecetasPublicadas() {
        return recetasPublicadas;
    }

    public int getRecetasGuardadas() {
        return recetasGuardadas;
    }

    public int getReseñasPublicadas() {
        return reseñasPublicadas;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setRecetasPublicadas(int recetasPublicadas) {
        this.recetasPublicadas = recetasPublicadas;
    }

    public void setRecetasGuardadas(int recetasGuardadas) {
        this.recetasGuardadas = recetasGuardadas;
    }

    public void setReseñasPublicadas(int reseñasPublicadas) {
        this.reseñasPublicadas = reseñasPublicadas;
    }
}
