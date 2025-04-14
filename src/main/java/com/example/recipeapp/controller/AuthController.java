package com.example.recipeapp.controller;

import com.example.recipeapp.payload.JwtResponse;
import com.example.recipeapp.payload.LoginRequest;
import com.example.recipeapp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        // Verifica que se hayan enviado email, alias y password
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty() ||
                loginRequest.getAlias() == null || loginRequest.getAlias().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Email, alias y password son requeridos");
        }

        // Construir el identificador compuesto: "email|alias"
        String compoundIdentifier = loginRequest.getEmail() + "|" + loginRequest.getAlias();

        // Autenticación: si falla, se lanzará AuthenticationException
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(compoundIdentifier, loginRequest.getPassword())
        );
        System.out.println("Usuario autenticado: " + authentication.getName());

        // Genera el token JWT usando el identificador compuesto
        String jwt = jwtUtil.generateJwtToken(compoundIdentifier);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }
}
