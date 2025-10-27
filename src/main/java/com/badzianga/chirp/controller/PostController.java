package com.badzianga.chirp.controller;

import com.badzianga.chirp.exception.ResourceNotFoundException;
import com.badzianga.chirp.model.Post;
import com.badzianga.chirp.request.CreatePostRequest;
import com.badzianga.chirp.response.ApiResponse;
import com.badzianga.chirp.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/posts")
public class PostController {
    private final PostService postService;

    @GetMapping
    ResponseEntity<ApiResponse> getPosts(@RequestParam(value="user", required = false) Long userId) {
        List<Post> posts;
        if (userId == null) {
            posts = postService.getAllPosts();
        }
        else {
            posts = postService.getPostsOfAuthor(userId);
        }
        return ResponseEntity.ok(new ApiResponse("success", posts));
    }

    @GetMapping("/{postId}")
    ResponseEntity<ApiResponse> getPost(@PathVariable Long postId) {
        try {
            Post post = postService.getPostById(postId);
            return ResponseEntity.ok(new ApiResponse("success", post));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping
    ResponseEntity<ApiResponse> createPost(@RequestBody CreatePostRequest request) {
        try {
            Post post = postService.createPost(request);
            return ResponseEntity.ok(new ApiResponse("success", post));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{postId}")
    ResponseEntity<ApiResponse> deletePost(@PathVariable Long postId) {
        try {
            postService.deletePost(postId);
            return ResponseEntity.ok(new ApiResponse("success", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
