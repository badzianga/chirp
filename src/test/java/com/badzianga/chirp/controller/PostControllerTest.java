package com.badzianga.chirp.controller;

import com.badzianga.chirp.exception.ResourceNotFoundException;
import com.badzianga.chirp.model.Post;
import com.badzianga.chirp.model.User;
import com.badzianga.chirp.service.PostService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@ActiveProfiles("test")
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private final PostService postService = Mockito.mock(PostService.class);

    @Value("/${api.prefix}/posts")
    private String url;

    @Test
    void shouldReturnAllPostsWhenUserIsNotSpecified() throws Exception {
        Mockito.when(postService.getAllPosts()).thenReturn(List.of(
                new Post("Content", new User("test@email.com", "test", "password")),
                new Post("Post", new User("another@email.com", "test2", "password"))
        ));

        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data[0].content").value("Content"))
                .andExpect(jsonPath("$.data[1].content").value("Post"));
    }

    @Test
    void shouldReturnPostsOfSpecifiedUser() throws Exception {
        User user = new User("test@email.com", "test", "password");
        user.setId(1L);
        Mockito.when(postService.getPostsOfAuthor(1L)).thenReturn(List.of(
                new Post("Content", user),
                new Post("Another content", user)
        ));

        mockMvc.perform(get(url + "?user=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data[0].content").value("Content"))
                .andExpect(jsonPath("$.data[1].content").value("Another content"));
    }

    @Test
    void shouldReturnPostById() throws Exception {
        User user = new User("test@email.com", "test", "password");
        Mockito.when(postService.getPostById(1L)).thenReturn(new Post("Content", user));

        mockMvc.perform(get(url + "/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.content").value("Content"));
    }

    @Test
    void shouldThrowExceptionWhenPostIsNotFound() throws Exception {
        Mockito.when(postService.getPostById(Mockito.any()))
                .thenThrow(new ResourceNotFoundException("Post not found"));

        mockMvc.perform(get(url + "/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
