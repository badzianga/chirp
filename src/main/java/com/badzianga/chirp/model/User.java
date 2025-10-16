package com.badzianga.chirp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String username;
    private String password;

    public User() {}

    @Override
    public String toString() {
        return String.format("User[id=%d, email=%s, username='%s', password='%s']", id, email, username, password);
    }
}
