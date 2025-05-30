package com.example.recipeapp.repository;

import com.example.recipeapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByAlias(String alias);
    Optional<User> findByEmailAndAlias(String email, String alias);
}
