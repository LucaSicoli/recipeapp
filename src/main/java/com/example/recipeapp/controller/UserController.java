package com.example.recipeapp.controller;

import com.example.recipeapp.model.User;
import com.example.recipeapp.payload.UserMeResponse;
import com.example.recipeapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Crear un nuevo usuario
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.status(201).body(created);
    }

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(u -> ResponseEntity.ok(u))
                .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar un usuario
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        User updated = userService.updateUser(user);
        return ResponseEntity.ok(updated);
    }

    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener datos del usuario autenticado
    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        UserMeResponse dto = new UserMeResponse(
                u.getId(),
                u.getAlias(),
                u.getEmail(),
                u.getUrlFotoPerfil(),
                u.getDescripcion()
        );
        return ResponseEntity.ok(dto);
    }
}