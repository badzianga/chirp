package com.badzianga.chirp.repository;

import com.badzianga.chirp.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCheckIfUserExistsByEmail() {
        // given
        User user = new User("user@email.com", "user", "password");
        userRepository.save(user);

        // when
        boolean foundExisting = userRepository.existsByEmailIgnoreCase("user@email.com");
        boolean notFoundNotExisting = userRepository.existsByUsernameIgnoreCase("notexisting@email.com");

        // then
        assertThat(foundExisting).isTrue();
        assertThat(notFoundNotExisting).isFalse();
    }

    @Test
    void shouldCheckIfUserExistsByUsername() {
        // given
        User user = new User("user@email.com", "user", "password");
        userRepository.save(user);

        // when
        boolean foundExisting = userRepository.existsByUsernameIgnoreCase("user");
        boolean notFoundNotExisting = userRepository.existsByUsernameIgnoreCase("not-existing-user");

        // then
        assertThat(foundExisting).isTrue();
        assertThat(notFoundNotExisting).isFalse();
    }

    @Test
    void shouldFindUserByUsername() {
        // given
        userRepository.save(new User("user@email.com", "user", "password"));

        // when
        Optional<User> user = userRepository.findByUsernameIgnoreCase("USER");

        // then
        assertThat(user).isPresent();
        assertThat(user.get().getUsername()).isEqualTo("user");
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserWithGivenUsernameDoesNotExist() {
        // when
        Optional<User> user = userRepository.findByUsernameIgnoreCase("USER");

        // then
        assertThat(user).isNotPresent();
    }
    
    @Test
    void shouldFindUsersWithSimilarUsername() {
        // given
        userRepository.save(new User("user@email.com", "User", "password"));
        userRepository.save(new User("user123@email.com", "user123", "password"));
        userRepository.save(new User("another@email.com", "another", "password"));

        // when
        List<User> users = userRepository.findByUsernameContainingIgnoreCase("USER");

        // then
        assertThat(users.size()).isEqualTo(2);
    }
}
