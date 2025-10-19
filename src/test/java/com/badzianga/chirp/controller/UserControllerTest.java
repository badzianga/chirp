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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Test
    void shouldReturnAllUsers() throws Exception {
        Mockito.when(userService.getAllUsers()).thenReturn(List.of(
                new User("test@email.com", "test", "password"),
                new User("another@email.com", "another", "p@ssw0rd")
        ));

        mockMvc.perform(get('/' + apiPrefix + "/users/all").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data[0].username").value("test"))
                .andExpect(jsonPath("$.data[1].username").value("another"));

    }

    @Test
    void shouldReturnUsersWithSimilarUsername() throws Exception {
        Mockito.when(userService.findUsersWithSimilarUsername("TEST")).thenReturn(List.of(
                new User("test@email.com", "test", "password"),
                new User("test123@email.com", "Test123", "password")
        ));

        mockMvc.perform(get('/' + apiPrefix + "/users/find/TEST").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }
}
