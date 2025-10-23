package com.badzianga.chirp.service;

import com.badzianga.chirp.exception.ResourceNotFoundException;
import com.badzianga.chirp.model.Post;
import com.badzianga.chirp.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    List<Post> getPostsOfAuthor(Long authorId) {
        return postRepository.findByAuthor_Id(authorId);
    }

    Post getPostById(Long postId) throws ResourceNotFoundException {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }
}
