package com.badzianga.chirp.service;

import com.badzianga.chirp.exception.UserAlreadyExistsException;
import com.badzianga.chirp.model.User;
import com.badzianga.chirp.repository.UserRepository;
import com.badzianga.chirp.request.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldAddUser() {
        // given
        RegisterRequest request = new RegisterRequest("test@email.com", "test", "password");
        User savedUser = new User("test@email.com", "test", "encoded-password");

        Mockito.when(userRepository.existsByEmailIgnoreCase(request.email())).thenReturn(false);
        Mockito.when(userRepository.existsByUsernameIgnoreCase(request.username())).thenReturn(false);
        Mockito.when(userRepository.save(any(User.class))).thenReturn(savedUser);
        Mockito.when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");

        // when
        authService.register(request);

        // then
        Mockito.verify(userRepository).save(Mockito.argThat( user ->
                user.getEmail().equalsIgnoreCase(request.email())
                && user.getUsername().equalsIgnoreCase(request.username())
                && user.getPassword().equalsIgnoreCase("encoded-password")
        ));
    }

    @Test
    void shouldThrowExceptionIfEmailAlreadyExists() {
        // given
        RegisterRequest request = new RegisterRequest("test@email.com", "test", "password");

        Mockito.when(userRepository.existsByEmailIgnoreCase(request.email())).thenReturn(true);

        // when and then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Email already in use");
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionIfUsernameAlreadyExists() {
        // given
        RegisterRequest request = new RegisterRequest("test@email.com", "test", "password");

        Mockito.when(userRepository.existsByEmailIgnoreCase(request.email())).thenReturn(false);
        Mockito.when(userRepository.existsByUsernameIgnoreCase(request.username())).thenReturn(true);

        // when and then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Username already in use");
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }
}
