package com.example.recipeapp.controller;

import com.example.recipeapp.service.PasswordResetService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    @Autowired private PasswordResetService resetService;

    @PostMapping("/request-reset")
    public ResponseEntity<?> requestReset(@RequestBody EmailDTO dto) {
        resetService.requestReset(dto.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyCode(@RequestBody CodeDTO dto) {
        resetService.verifyCode(dto.getEmail(), dto.getCode());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> reset(@RequestBody ResetDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords no coinciden");
        }
        resetService.resetPassword(dto.getEmail(), dto.getCode(), dto.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @Data static class EmailDTO { private String email; }
    @Data static class CodeDTO { private String email, code; }
    @Data static class ResetDTO {
        private String email, code, newPassword, confirmPassword;
    }
}
