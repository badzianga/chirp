package com.badzianga.chirp.service;

import com.badzianga.chirp.exception.ResourceNotFoundException;
import com.badzianga.chirp.exception.UserAlreadyExistsException;
import com.badzianga.chirp.model.User;
import com.badzianga.chirp.repository.UserRepository;
import com.badzianga.chirp.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

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

        Mockito.when(userRepository.existsByEmailIgnoreCase(request.getEmail())).thenReturn(false);
        Mockito.when(userRepository.existsByUsernameIgnoreCase(request.getUsername())).thenReturn(false);
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

        Mockito.when(userRepository.existsByEmailIgnoreCase(request.getEmail())).thenReturn(true);

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

        Mockito.when(userRepository.existsByUsernameIgnoreCase(request.getUsername())).thenReturn(true);

        // when and then
        assertThatThrownBy(() -> userService.addUser(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("This username is taken");
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    void shouldReturnUserWithGivenUsername() {
        // given
        User user = new User("test@email.com", "test", "password");

        Mockito.when(userRepository.findByUsernameIgnoreCase("TEST")).thenReturn(Optional.of(user));

        // when
        User foundUser = userService.findUserByUsername("TEST");

        // then
        assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(foundUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenUserWithGivenUsernameDoesNotExist() {
        // given
        Mockito.when(userRepository.findByUsernameIgnoreCase("test"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findUserByUsername("test"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
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

    @Test
    void shouldDeleteUserWhenExists() {
        // given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        userService.deleteUser(userId);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUserToDeleteDoesNotExist() {
        // given
        Long userId = 1L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with given id does not exist");
        Mockito.verify(userRepository, Mockito.never()).delete(any());
    }
}
