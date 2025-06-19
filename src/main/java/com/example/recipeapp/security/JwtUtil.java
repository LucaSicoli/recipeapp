package com.example.recipeapp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Genera una key segura (>= 512 bits) para HS512
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // Tiempo de expiración (24 horas)
    private final long jwtExpirationMs = 86400000;

    // Genera el token JWT usando la 'key' en vez de un string
    public String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // Extrae el username (subject) del token JWT
    public String getUsernameFromJwtToken(String token) {
        // A partir de jjwt 0.11.0, se recomienda usar parserBuilder()
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }



    // Valida el token JWT
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            System.err.println("Firma JWT inválida: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Token JWT malformado: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Token JWT expirado: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Token JWT no soportado: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Claims JWT vacíos: " + e.getMessage());
        }
        return false;
    }
}
