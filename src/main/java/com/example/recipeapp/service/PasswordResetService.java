package com.example.recipeapp.service;

import com.example.recipeapp.model.PasswordResetToken;
import com.example.recipeapp.repository.PasswordResetTokenRepository;
import com.example.recipeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;  // ← Importa esto

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional  // ← Ahora TODOS los métodos de este servicio correrán dentro de una transacción
public class PasswordResetService {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordResetTokenRepository tokenRepo;
    @Autowired private JavaMailSender mailSender;
    @Autowired private PasswordEncoder passwordEncoder;

    public void requestReset(String email) {
        if (!userRepo.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email no registrado");
        }
        // Borra tokens anteriores y crea uno nuevo
        tokenRepo.deleteByEmail(email);
        String code = String.format("%06d", new Random().nextInt(1_000_000));
        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(email);
        token.setCode(code);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        tokenRepo.save(token);

        // Envía el mail
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Código de recuperación de contraseña");
        msg.setText("Tu código de recuperación es: " + code);
        mailSender.send(msg);
    }

    public void verifyCode(String email, String code) {
        PasswordResetToken t = tokenRepo
                .findByEmailAndCode(email, code)
                .orElseThrow(() -> new RuntimeException("Código inválido"));
        if (t.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Código expirado");
        }
    }

    public void resetPassword(String email, String code, String newPassword) {
        verifyCode(email, code);
        userRepo.findByEmail(email).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);
            // borra el token usado
            tokenRepo.deleteByEmail(email);
        });
    }
}
