package com.badzianga.chirp.controller;

import com.badzianga.chirp.exception.ResourceNotFoundException;
import com.badzianga.chirp.filter.JwtAuthFilter;
import com.badzianga.chirp.model.User;
import com.badzianga.chirp.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
})
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private final UserService userService = Mockito.mock(UserService.class);

    @Value("/${api.prefix}/users")
    private String url;

    @Test
    void shouldReturnAllUsers() throws Exception {
        Mockito.when(userService.getAllUsers()).thenReturn(List.of(
                new User("test@email.com", "test", "password"),
                new User("another@email.com", "another", "p@ssw0rd")
        ));

        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data[0].username").value("test"))
                .andExpect(jsonPath("$.data[1].username").value("another"));

    }

    @Test
    void shouldReturnUserWithGivenUsername() throws Exception {
        Mockito.when(userService.findUserByUsername("test"))
                .thenReturn(new User("test@email.com", "test", "password"));

        mockMvc.perform(get(url + "/test").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.username").value("test"));
    }

    @Test
    void shouldThrowExceptionWhenUserWithGivenUsernameDoesNotExist() throws Exception {
        Mockito.when(userService.findUserByUsername("test"))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get(url + "/test").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldDeleteUser() throws Exception {
        Long userId = 1L;
        Mockito.doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete(url + '/' + userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldThrowExceptionWhenUserToDeleteDoesNotExist() throws Exception {
        Long userId = 99L;
        Mockito.doThrow(new ResourceNotFoundException("User with given id does not exist"))
                .when(userService).deleteUser(userId);

        mockMvc.perform(delete(url + '/' + userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with given id does not exist"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
