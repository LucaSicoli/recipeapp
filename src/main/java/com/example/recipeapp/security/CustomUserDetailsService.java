package com.example.recipeapp.security;

import com.example.recipeapp.model.User;
import com.example.recipeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Se espera que el identificador compuesto tenga el formato "email|alias".
     */
    @Override
    public UserDetails loadUserByUsername(String compoundIdentifier) throws UsernameNotFoundException {
        String[] parts = compoundIdentifier.split("\\|");
        if (parts.length != 2) {
            throw new UsernameNotFoundException("El identificador debe incluir email y alias separados por '|'");
        }
        String email = parts[0];
        String alias = parts[1];

        Optional<User> userOpt = userRepository.findByEmail(email);
        User appUser = userOpt.orElseThrow(() ->
                new UsernameNotFoundException("No se encontró el usuario con email " + email + " y alias " + alias)
        );
        return org.springframework.security.core.userdetails.User.builder()
                .username(appUser.getEmail()) // Puedes elegir usar el email o el alias
                .password(appUser.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
