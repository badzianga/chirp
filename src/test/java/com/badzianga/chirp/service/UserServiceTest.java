package com.badzianga.chirp.service;

import com.badzianga.chirp.exception.UserAlreadyExistsException;
import com.badzianga.chirp.model.User;
import com.badzianga.chirp.repository.UserRepository;
import com.badzianga.chirp.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

public class UserServiceTest {
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void shouldAddUser() {
        // given
        CreateUserRequest request = new CreateUserRequest("test@email.com", "test", "password");
        User user = new User("test@email.com", "test", "password");

        Mockito.when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        Mockito.when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        User result = userService.addUser(request);

        // then
        assertThat(result.getEmail()).isEqualTo(request.getEmail());
        assertThat(result.getUsername()).isEqualTo(request.getUsername());
        assertThat(result.getPassword()).isEqualTo(request.getPassword());
        Mockito.verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionIfEmailAlreadyExists() {
        // given
        CreateUserRequest request = new CreateUserRequest("test@email.com", "test", "password");

        Mockito.when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // when and then
        assertThatThrownBy(() -> userService.addUser(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("User with this email is already registered");
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionIfUsernameAlreadyExists() {
        // given
        CreateUserRequest request = new CreateUserRequest("test@email.com", "test", "password");

        Mockito.when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        // when and then
        assertThatThrownBy(() -> userService.addUser(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("This username is taken");
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    void shouldReturnListOfUsersWithSimilarUsername() {
        // given
        List<User> users = List.of(
                new User("user@email.com", "user", "password"),
                new User("user123@email.com", "User123", "password")
        );

        Mockito.when(userRepository.findByUsernameContainingIgnoreCase("USER")).thenReturn(users);

        // when
        List<User> found = userService.findUsersWithSimilarUsername("USER");
        List<User> notFound = userService.findUsersWithSimilarUsername("not existing");

        // then
        assertThat(found).isEqualTo(users);
        assertThat(notFound).isEqualTo(List.of());
    }
}
