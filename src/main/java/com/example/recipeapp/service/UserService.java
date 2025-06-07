// src/main/java/com/example/recipeapp/service/UserService.java
package com.example.recipeapp.service;

import com.example.recipeapp.model.User;
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
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired private UserRepository userRepo;
    @Autowired private VerificationTokenRepository tokenRepo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JavaMailSender mailSender;

    // ————— Registro —————

    /** 1) Inicia el registro: crea usuario INACTIVO y envía código de confirmación */
    public void iniciarRegistro(String email, String alias) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email ya registrado");
        }

        // 1.1) crea usuario sin contraseña y marcado como inactivo
        var u = new User();
        u.setEmail(email);
        u.setAlias(alias);
        u.setActivo(false);
        u.setFechaCreacion(LocalDateTime.now());
        userRepo.save(u);

        // 1.2) genera y almacena token de tipo REGISTER
        tokenRepo.deleteByEmailAndType(email, TokenType.REGISTER);
        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        var vt = new VerificationToken();
        vt.setEmail(email);
        vt.setCode(code);
        vt.setType(TokenType.REGISTER);
        vt.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        tokenRepo.save(vt);

        // 1.3) envía correo con el código
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Confirma tu registro");
        mail.setText("Tu código de registro es: " + code);
        mailSender.send(mail);
    }

    /** 2) Completa el registro: verifica código, activa user y guarda contraseña */
    public void completarRegistro(String email, String code, String password) {
        // 2.1) valida token
        var vt = tokenRepo.findByEmailAndCodeAndType(email, code, TokenType.REGISTER)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido"));
        if (vt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código expirado");
        }

        // 2.2) activa usuario y asigna contraseña
        var u = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no existe"));
        u.setPassword(encoder.encode(password));
        u.setActivo(true);
        userRepo.save(u);

        // 2.3) limpia token
        tokenRepo.deleteByEmailAndType(email, TokenType.REGISTER);
    }

    // ————— Reseteo de contraseña —————

    /** 3) Solicita reset: genera código numérico y lo envía por mail */
    public void requestReset(String email) {
        var u = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email no registrado"));

        tokenRepo.deleteByEmailAndType(email, TokenType.RESET_PASSWORD);

        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        var vt = new VerificationToken();
        vt.setEmail(email);
        vt.setCode(code);
        vt.setType(TokenType.RESET_PASSWORD);
        vt.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        tokenRepo.save(vt);

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Código para resetear tu contraseña");
        mail.setText("Tu código de reseteo es: " + code);
        mailSender.send(mail);
    }

    /** 4) Verifica que el código de reseteo sea válido */
    public void verifyCode(String email, String code) {
        var vt = tokenRepo.findByEmailAndCodeAndType(email, code, TokenType.RESET_PASSWORD)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido"));
        if (vt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código expirado");
        }
    }

    /** 5) Completa el reseteo: asigna nueva contraseña al usuario */
    public void resetPassword(String email, String code, String newPassword) {
        verifyCode(email, code);
        var u = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no existe"));
        u.setPassword(encoder.encode(newPassword));
        userRepo.save(u);
        tokenRepo.deleteByEmailAndType(email, TokenType.RESET_PASSWORD);
    }

    // ————— CRUD para UserController —————

    public User createUser(User user) {
        user.setFechaCreacion(LocalDateTime.now());
        user.setActivo(true);
        return userRepo.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepo.findById(id);
    }

    public User updateUser(User user) {
        return userRepo.save(user);
    }

    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }
}
