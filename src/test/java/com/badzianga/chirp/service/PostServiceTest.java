package com.badzianga.chirp.service;

import com.badzianga.chirp.exception.ResourceNotFoundException;
import com.badzianga.chirp.model.Post;
import com.badzianga.chirp.model.User;
import com.badzianga.chirp.repository.PostRepository;
import com.badzianga.chirp.request.CreatePostRequest;
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
    private UserService userService;
    private PostService postService;

    @BeforeEach
    public void setUp() {
        postRepository = Mockito.mock(PostRepository.class);
        userService = Mockito.mock(UserService.class);
        postService = new PostService(postRepository, userService);
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

    @Test
    void shouldCreatePost() {
        // given
        String content = "Content of the post";
        Long authorId = 1L;
        User author = new User("test@email.com", "test", "password");
        author.setId(authorId);
        Post post = new Post(content, author);

        Mockito.when(userService.findUserById(1L)).thenReturn(author);
        Mockito.when(postRepository.save(Mockito.any(Post.class))).thenReturn(post);

        // when
        Post createdPost = postService.createPost(new CreatePostRequest(content, authorId));

        // then
        assertThat(createdPost.getId()).isEqualTo(post.getId());
        assertThat(createdPost.getContent()).isEqualTo(post.getContent());
        assertThat(createdPost.getAuthor().getId()).isEqualTo(authorId);
        Mockito.verify(postRepository, Mockito.times(1)).save(Mockito.any(Post.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundDuringPostCreation() {
        // given
        Long authorId = 1L;

        Mockito.when(userService.findUserById(authorId)).thenThrow(ResourceNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> postService.createPost(new CreatePostRequest("content", authorId)))
                .isInstanceOf(ResourceNotFoundException.class);
        Mockito.verify(postRepository, Mockito.never()).save(any());
    }

    @Test
    void shouldDeletePostWhenExists() {
        // given
        Long postId = 100L;
        Post post = new Post("Content", new User("test@email.com", "test", "password"));
        post.setId(postId);

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // when
        postService.deletePost(postId);

        // then
        Mockito.verify(postRepository, Mockito.times(1)).delete(post);
        Mockito.verify(postRepository, Mockito.never()).save(any(Post.class));
    }

    @Test
    void shouldThrowExceptionWhenPostToDeleteDoesNotExist() {
        // given
        Long postId = 1L;

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> postService.deletePost(postId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found");
        Mockito.verify(postRepository, Mockito.never()).delete(any());
    }

    @Test
    void shouldGetPostsContainingGivenPhrase() {
        // given
        Post post1 = new Post("Content of the post", new User("test@email.com", "test", "password"));
        Post post2 = new Post("Another post", new User("test2@email.com", "test2", "password"));

        Mockito.when(postRepository.findByContentContainingIgnoreCase("POST")).thenReturn(List.of(post1, post2));

        // when
        List<Post> posts = postService.findPostsWithGivenPhrase("POST");

        // then
        assertThat(posts).hasSize(2);
        assertThat(posts.get(0)).isEqualTo(post1);
        assertThat(posts.get(1)).isEqualTo(post2);
        Mockito.verify(postRepository, Mockito.times(1)).findByContentContainingIgnoreCase("POST");
    }
}
