package com.badzianga.chirp.controller;

import com.badzianga.chirp.exception.UserAlreadyExistsException;
import com.badzianga.chirp.model.User;
import com.badzianga.chirp.request.CreateUserRequest;
import com.badzianga.chirp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private final UserService userService = Mockito.mock(UserService.class);

    @Value("/${api.prefix}/auth")
    private String url;

    @Test
    void shouldRegisterUserSuccesfully() throws Exception {
        // given
        CreateUserRequest request = new CreateUserRequest("test@email.com", "test", "password");
        User user = new User("test@email.com", "test", "password");

        Mockito.when(userService.addUser(any(CreateUserRequest.class))).thenReturn(user);

        // when & then
        mockMvc.perform(post(url + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.username").value("test"))
                .andExpect(jsonPath("$.data.email").value("test@email.com"));
    }

    @Test
    void shouldReturnConflictWhenUsernameIsAlreadyUsed() throws Exception {
        // given
        CreateUserRequest request = new CreateUserRequest("test@email.com", "test", "password");

        Mockito.when(userService.addUser(any(CreateUserRequest.class)))
                .thenThrow(new UserAlreadyExistsException("This username is taken"));

        // when + then
        mockMvc.perform(post(url + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("This username is taken"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldReturnConflictWhenEmailIsAlreadyUsed() throws Exception {
        // given
        CreateUserRequest request = new CreateUserRequest("test@email.com", "test", "password");

        Mockito.when(userService.addUser(any(CreateUserRequest.class)))
                .thenThrow(new UserAlreadyExistsException("User with this email is already registered"));

        // when + then
        mockMvc.perform(post(url + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User with this email is already registered"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
