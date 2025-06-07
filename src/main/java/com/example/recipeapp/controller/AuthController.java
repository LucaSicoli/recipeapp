// src/main/java/com/example/recipeapp/controller/AuthController.java
package com.example.recipeapp.controller;

import com.example.recipeapp.payload.*;
import com.example.recipeapp.service.PasswordResetService;
import com.example.recipeapp.service.UserService;
import com.example.recipeapp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private PasswordResetService resetService;
    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtUtil jwtUtil;

    // ————— Registro —————
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest req) {
        userService.iniciarRegistro(req.email(), req.alias());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/register/confirm")
    public ResponseEntity<Void> confirm(
            @RequestBody ConfirmRequest req,
            @RequestParam String email,
            @RequestParam String code
    ) {
        userService.completarRegistro(email, code, req.password());
        return ResponseEntity.ok().build();
    }

    // ————— Login —————
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail() + "|" + req.getAlias(),
                        req.getPassword()
                )
        );
        String token = jwtUtil.generateJwtToken(req.getEmail() + "|" + req.getAlias());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    // ————— Reset contraseña —————
    @PostMapping("/request-reset")
    public ResponseEntity<Void> requestReset(@RequestBody EmailDTO dto) {
        resetService.requestReset(dto.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<Void> verify(@RequestBody CodeDTO dto) {
        resetService.verifyCode(dto.email(), dto.code());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> reset(@RequestBody ResetDTO dto) {
        if (!dto.newPassword().equals(dto.confirmPassword()))
            return ResponseEntity.badRequest().build();
        resetService.resetPassword(dto.email(), dto.code(), dto.newPassword());
        return ResponseEntity.ok().build();
    }
}
