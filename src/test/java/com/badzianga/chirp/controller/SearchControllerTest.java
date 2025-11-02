package com.badzianga.chirp.controller;

import com.badzianga.chirp.filter.JwtAuthFilter;
import com.badzianga.chirp.model.Post;
import com.badzianga.chirp.model.User;
import com.badzianga.chirp.service.PostService;
import com.badzianga.chirp.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SearchController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
})
@ActiveProfiles("test")
public class SearchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private final UserService userService = Mockito.mock(UserService.class);

    @MockitoBean
    private final PostService postService = Mockito.mock(PostService.class);

    @Value("/${api.prefix}/search")
    private String url;

    @Test
    void shouldReturnUsersWithSimilarUsername() throws Exception {
        Mockito.when(userService.findUsersWithSimilarUsername("TEST")).thenReturn(List.of(
                new User("test@email.com", "test", "password"),
                new User("test123@email.com", "Test123", "password")
        ));

        mockMvc.perform(get(url + "/users?query=TEST").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldReturnPostsContainingGivenPhrase() throws Exception {
        Mockito.when(postService.findPostsWithGivenPhrase("post")).thenReturn(List.of(
                new Post("post", new User("a@email.com", "a", "password")),
                new Post("another post", new User("b@email.com", "b", "abc"))
        ));

        mockMvc.perform(get(url + "/posts?query=post").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }
}
