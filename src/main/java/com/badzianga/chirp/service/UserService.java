package com.badzianga.chirp.service;

import com.badzianga.chirp.exception.ResourceNotFoundException;
import com.badzianga.chirp.model.User;
import com.badzianga.chirp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findUserById(Long id) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        throw new ResourceNotFoundException("User not found");
    }

    public User findUserByUsername(String username) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findByUsernameIgnoreCase(username);
        if (user.isPresent()) {
            return user.get();
        }
        throw new ResourceNotFoundException("User not found");
    }

    public List<User> findUsersWithSimilarUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }

    public void deleteUser(Long userId) throws ResourceNotFoundException {
        userRepository.findById(userId).ifPresentOrElse(userRepository::delete, () -> {
            throw new ResourceNotFoundException("User with given id does not exist");
        });
    }
}
