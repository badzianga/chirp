package com.badzianga.chirp.service;

import com.badzianga.chirp.exception.ResourceNotFoundException;
import com.badzianga.chirp.exception.UserAlreadyExistsException;
import com.badzianga.chirp.model.User;
import com.badzianga.chirp.repository.UserRepository;
import com.badzianga.chirp.request.CreateUserRequest;
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

    public User addUser(CreateUserRequest request) throws UserAlreadyExistsException {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new UserAlreadyExistsException("User with this email is already registered");
        }
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new UserAlreadyExistsException("This username is taken");
        }
        return userRepository.save(new User(request.getEmail(), request.getUsername(), request.getPassword()));
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
