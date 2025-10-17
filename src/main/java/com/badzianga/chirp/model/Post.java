package com.badzianga.chirp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    private Long id;

    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    public Post() {}
}
