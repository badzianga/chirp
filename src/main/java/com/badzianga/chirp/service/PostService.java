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

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsOfAuthor(Long authorId) {
        return postRepository.findByAuthor_Id(authorId);
    }

    public Post getPostById(Long postId) throws ResourceNotFoundException {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }

    public void deletePost(Long postId) throws ResourceNotFoundException {
        postRepository.findById(postId).ifPresentOrElse(postRepository::delete, () -> {
            throw new ResourceNotFoundException("Post not found");
        });
    }
}
