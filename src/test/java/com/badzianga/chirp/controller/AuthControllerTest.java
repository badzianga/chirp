package com.badzianga.chirp.controller;

import com.badzianga.chirp.exception.UserAlreadyExistsException;
import com.badzianga.chirp.filter.JwtAuthFilter;
import com.badzianga.chirp.request.RegisterRequest;
import com.badzianga.chirp.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
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

@WebMvcTest(controllers = AuthController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
})
@ActiveProfiles("test")
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private final AuthService authService = Mockito.mock(AuthService.class);

    @Value("/${api.prefix}/auth")
    private String url;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        // given
        RegisterRequest request = new RegisterRequest("test@email.com", "test", "password");

        Mockito.doNothing()
                .when(authService).register(request);

        // when & then
        mockMvc.perform(post(url + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnConflictWhenUsernameIsAlreadyUsed() throws Exception {
        // given
        RegisterRequest request = new RegisterRequest("test@email.com", "test", "password");

        Mockito.doThrow(new UserAlreadyExistsException("Username already in use"))
                .when(authService).register(any(RegisterRequest.class));

        // when + then
        mockMvc.perform(post(url + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username already in use"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldReturnConflictWhenEmailIsAlreadyUsed() throws Exception {
        // given
        RegisterRequest request = new RegisterRequest("test@email.com", "test", "password");

        Mockito.doThrow(new UserAlreadyExistsException("Email already in use"))
                .when(authService).register(any(RegisterRequest.class));

        // when + then
        mockMvc.perform(post(url + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already in use"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
