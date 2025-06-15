// src/main/java/com/example/recipeapp/service/PasswordResetService.java
package com.example.recipeapp.service;

import com.example.recipeapp.model.PasswordResetToken;
import com.example.recipeapp.repository.PasswordResetTokenRepository;
import com.example.recipeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
public class PasswordResetService {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordResetTokenRepository tokenRepo;
    @Autowired private JavaMailSender mailSender;
    @Autowired private PasswordEncoder passwordEncoder;

    /**
     * Paso 1: solicita un nuevo código.
     */
    public void requestReset(String email) {
        // 1.1) Verifica que el email exista
        if (!userRepo.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email no registrado");
        }
        // 1.2) Limpia tokens expirados de toda la tabla
        tokenRepo.deleteByExpiresAtBefore(LocalDateTime.now());

        // 1.3) Borra cualquier token anterior de este email
        tokenRepo.deleteByEmail(email);

        // 1.4) Genera y guarda el nuevo token
        String code = String.format("%06d", new Random().nextInt(1_000_000));
        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(email);
        token.setCode(code);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        tokenRepo.save(token);

        // 1.5) Envía el correo con el código
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Código de recuperación de contraseña");
        msg.setText("Tu código de recuperación es: " + code);
        mailSender.send(msg);
    }

    /**
     * Paso 2: verifica que el código sea válido y no haya expirado.
     */
    public void verifyCode(String email, String code) {
        PasswordResetToken t = tokenRepo
                .findByEmailAndCode(email, code)
                .orElseThrow(() -> new RuntimeException("Código inválido"));

        if (t.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Código expirado");
        }
    }

    /**
     * Paso 3: una vez verificado el código, actualiza la contraseña y limpia el token.
     */
    public void resetPassword(String email, String code, String newPassword) {
        // 3.1) Vuelve a verificar el código (por seguridad)
        verifyCode(email, code);

        // 3.2) Cambia la contraseña
        userRepo.findByEmail(email).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);
        });

        // 3.3) Borra todos los tokens de este email
        tokenRepo.deleteByEmail(email);
    }
}