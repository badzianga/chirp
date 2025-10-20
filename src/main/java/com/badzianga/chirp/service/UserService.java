package com.badzianga.chirp.service;

import com.badzianga.chirp.exception.UserAlreadyExistsException;
import com.badzianga.chirp.model.User;
import com.badzianga.chirp.repository.UserRepository;
import com.badzianga.chirp.request.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addUser(CreateUserRequest request) throws UserAlreadyExistsException {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new UserAlreadyExistsException("User with this email is already registered");
        }
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new UserAlreadyExistsException("This username is taken");
        }
        return userRepository.save(new User(request.getEmail(), request.getUsername(), request.getPassword()));
    }

    public List<User> findUsersWithSimilarUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }
}
