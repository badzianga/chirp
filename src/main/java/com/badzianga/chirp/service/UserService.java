package com.badzianga.chirp.service;

import com.badzianga.chirp.exception.UserAlreadyExistsException;
import com.badzianga.chirp.model.User;
import com.badzianga.chirp.repository.UserRepository;
import com.badzianga.chirp.request.CreateUserRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addUser(CreateUserRequest request) throws UserAlreadyExistsException {
        if (userRepository.existsByEmail(request.email)) {
            throw new UserAlreadyExistsException("User with this email is already registered");
        }
        if (userRepository.existsByUsername(request.username)) {
            throw new UserAlreadyExistsException("This username is taken");
        }
        return userRepository.save(new User(request.email, request.username, request.password));
    }
}
