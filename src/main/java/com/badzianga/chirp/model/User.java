package com.badzianga.chirp.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String username;
    private String password;

    @OneToMany(mappedBy = "author")
    private List<Post> posts;

    public User() {}

    @Override
    public String toString() {
        return String.format("User[id=%d, email=%s, username='%s', password='%s']", id, email, username, password);
    }
}
