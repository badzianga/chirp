package com.badzianga.chirp.repository;

import com.badzianga.chirp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByUsernameIgnoreCase(String username);

    Optional<User> findByUsernameIgnoreCase(String username);
    List<User> findByUsernameContainingIgnoreCase(String username);
}
