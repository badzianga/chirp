package com.badzianga.chirp.repository;

import com.badzianga.chirp.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor_Id(Long userId);
}
