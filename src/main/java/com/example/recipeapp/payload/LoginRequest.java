package com.example.recipeapp.payload;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String alias;
    private String password;
}
