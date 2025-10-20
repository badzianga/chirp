package com.badzianga.chirp.repository;

import com.badzianga.chirp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByUsernameIgnoreCase(String username);

    List<User> findByUsernameContainingIgnoreCase(String username);
}
