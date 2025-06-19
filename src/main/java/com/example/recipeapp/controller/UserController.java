package com.example.recipeapp.controller;

import com.example.recipeapp.model.User;
import com.example.recipeapp.payload.ProfileSummaryDTO;
import com.example.recipeapp.repository.UserRepository;
import com.example.recipeapp.security.JwtUtil;
import com.example.recipeapp.service.RatingService;
import com.example.recipeapp.service.RecipeService;
import com.example.recipeapp.service.UserSavedRecipeService;
import com.example.recipeapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserSavedRecipeService userSavedRecipeService;
    @Autowired
    private RatingService ratingService;

    // Crear un nuevo usuario
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Actualizar un usuario
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        // Opcional: validar que el ID coincida con el del objeto user
        User updated = userService.updateUser(user);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.getUsernameFromJwtToken(token);// suponiendo que tenés una clase JwtUtil que lo hace
        String emailValido = email.split("\\|")[0];
        System.out.println(emailValido + "User controller");
        Optional<User> user = userService.getUserByEmail(emailValido);
       // Optional<User> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/profile-summary")
    public ResponseEntity<ProfileSummaryDTO> getProfileSummary(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        // Obtener directamente el email desde el token
        String email = jwtUtil.getUsernameFromJwtToken(token);// suponiendo que tenés una clase JwtUtil que lo hace
        String emailValido = email.split("\\|")[0];


        // Buscar el usuario una sola vez (email es único)
        Optional<User> userOpt = userService.getUserByEmail(emailValido);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        Long userId = user.getId();

        int countRecetasPublicadas = recipeService.countRecipesUser(userId);
        int countRecetasGuardadas = userSavedRecipeService.countSavedRecipes(userId);
        int countReseñasPublicadas = ratingService.countResenasPublicadas(userId);

        ProfileSummaryDTO dto = new ProfileSummaryDTO(
                userId,
                countRecetasPublicadas,
                countRecetasGuardadas,
                countReseñasPublicadas
        );

        return ResponseEntity.ok(dto);
    }





}
