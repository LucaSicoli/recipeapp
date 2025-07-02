package com.example.recipeapp.payload;

public class UserMeResponse {
    private Long id;
    private String alias;
    private String email;
    private String urlFotoPerfil;
    private String descripcion;

    public UserMeResponse() {}

    public UserMeResponse(Long id,
                          String alias,
                          String email,
                          String urlFotoPerfil,
                          String descripcion) {
        this.id             = id;
        this.alias          = alias;
        this.email          = email;
        this.urlFotoPerfil  = urlFotoPerfil;
        this.descripcion    = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUrlFotoPerfil() { return urlFotoPerfil; }
    public void setUrlFotoPerfil(String urlFotoPerfil) { this.urlFotoPerfil = urlFotoPerfil; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}