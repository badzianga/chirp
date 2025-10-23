package com.badzianga.chirp.service;

import com.badzianga.chirp.exception.ResourceNotFoundException;
import com.badzianga.chirp.model.Post;
import com.badzianga.chirp.model.User;
import com.badzianga.chirp.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

public class PostServiceTest {
    private PostRepository postRepository;
    private PostService postService;

    @BeforeEach
    public void setUp() {
        postRepository = Mockito.mock(PostRepository.class);
        postService = new PostService(postRepository);
    }

    @Test
    void shouldGetAllPosts() {
        // given
        Post post1 = new Post("Content of the post", new User("test@email.com", "test", "password"));
        Post post2 = new Post("Another post", new User("another@email.com", "another", "password"));

        Mockito.when(postRepository.findAll()).thenReturn(List.of(post1, post2));

        // when
        List<Post> posts = postService.getAllPosts();

        // then
        assertThat(posts).hasSize(2);
        assertThat(posts.get(0)).isEqualTo(post1);
        assertThat(posts.get(1)).isEqualTo(post2);
    }

    @Test
    void shouldGetAllPostsOfAuthor() {
        // given
        User author = new User("test@email.com", "test", "password");
        Post post1 = new Post("Content of the post", author);
        Post post2 = new Post("Another post", author);

        Mockito.when(postRepository.findByAuthor_Id(author.getId())).thenReturn(List.of(post1, post2));

        // when
        List<Post> posts = postService.getPostsOfAuthor(author.getId());

        // then
        assertThat(posts).hasSize(2);
        assertThat(posts.get(0)).isEqualTo(post1);
        assertThat(posts.get(1)).isEqualTo(post2);
    }

    @Test
    void shouldGetPostById() {
        // given
        Post post = new Post("Content", new User("test@email.com", "test", "password"));
        Long id = post.getId();

        Mockito.when(postRepository.findById(id)).thenReturn(Optional.of(post));

        // when
        Post foundPost = postService.getPostById(id);

        // then
        assertThat(foundPost.getId()).isEqualTo(id);
        assertThat(foundPost.getContent()).isEqualTo(post.getContent());
    }

    @Test
    void shouldThrowExceptionWhenPostNotFound() {
        // given
        Mockito.when(postRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.getPostById(any()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post not found");
    }
}
