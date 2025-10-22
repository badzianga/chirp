package com.badzianga.chirp.controller;

import com.badzianga.chirp.exception.ResourceNotFoundException;
import com.badzianga.chirp.model.User;
import com.badzianga.chirp.response.ApiResponse;
import com.badzianga.chirp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final UserService service;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<User> users = service.getAllUsers();
        return ResponseEntity.ok(new ApiResponse("success", users));
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable String username) {
        try {
            User user = userService.findUserByUsername(username);
            return ResponseEntity.ok(new ApiResponse("success", user));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
