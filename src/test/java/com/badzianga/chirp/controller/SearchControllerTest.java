package com.badzianga.chirp.controller;

import com.badzianga.chirp.model.User;
import com.badzianga.chirp.service.UserService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
@ActiveProfiles("test")
public class SearchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

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
}
