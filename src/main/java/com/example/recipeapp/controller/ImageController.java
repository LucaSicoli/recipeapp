package com.example.recipeapp.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    // Carpeta relativa al directorio desde donde arranques la JVM
    private final Path uploadDir = Paths.get("uploads", "recipes");

    @PostConstruct
    public void init() throws IOException {
        // Crea la carpeta si no existe
        Files.createDirectories(uploadDir);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No se envió ningún archivo");
        }
        // Genera un nombre único y limpia caracteres peligrosos
        String filename = UUID.randomUUID() + "-" +
                Paths.get(file.getOriginalFilename()).getFileName().toString();
        Path target = uploadDir.resolve(filename);
        // Guarda el fichero en disco
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Construye la URL pública que luego guardarás en fotoPrincipal
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images/")
                .path(filename)
                .toUriString();

        return ResponseEntity.ok(url);
    }
}
