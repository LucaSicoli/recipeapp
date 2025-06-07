package com.example.recipeapp.service;

import com.example.recipeapp.model.VerificationToken;
import com.example.recipeapp.model.TokenType;
import com.example.recipeapp.repository.UserRepository;
import com.example.recipeapp.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Transactional
public class PasswordResetService {

    @Autowired private UserRepository userRepo;
    @Autowired private VerificationTokenRepository tokenRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JavaMailSender mailSender;

    /**
     * 1) Genera código de 6 dígitos, guarda createdAt/expiresAt y envía mail
     */
    public void requestReset(String email) {
        var u = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no existe"));

        // Borra tokens anteriores de este tipo
        tokenRepo.deleteByEmailAndType(email, TokenType.RESET_PASSWORD);

        // Genera el código
        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));

        // Crea el token y fije todas las fechas
        var vt = new VerificationToken();
        vt.setCode(code);
        vt.setType(TokenType.RESET_PASSWORD);
        vt.setEmail(email);
        vt.setCreatedAt(LocalDateTime.now());              // <-- ojo aquí
        vt.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        tokenRepo.save(vt);

        // Envía el mail
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Código para resetear tu contraseña");
        mail.setText("Tu código de reseteo es: " + code);
        mailSender.send(mail);
    }

    /** Verifica email, código y expiración */
    public void verifyCode(String email, String code) {
        VerificationToken vt = tokenRepo
                .findByEmailAndCodeAndType(email, code, TokenType.RESET_PASSWORD)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido"));
        if (vt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código expirado");
        }
    }

    /** Resetea contraseña tras validación */
    public void resetPassword(String email, String code, String newPassword) {
        verifyCode(email, code);
        userRepo.findByEmail(email).ifPresent(u -> {
            u.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(u);
            tokenRepo.deleteByEmailAndType(email, TokenType.RESET_PASSWORD);
        });
    }
}