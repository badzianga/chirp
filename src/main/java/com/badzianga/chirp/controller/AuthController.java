package com.badzianga.chirp.controller;

import com.badzianga.chirp.exception.UserAlreadyExistsException;
import com.badzianga.chirp.model.User;
import com.badzianga.chirp.request.CreateUserRequest;
import com.badzianga.chirp.request.LoginRequest;
import com.badzianga.chirp.response.ApiResponse;
import com.badzianga.chirp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody CreateUserRequest request) {
        try {
            User user = userService.addUser(request);
            return ResponseEntity.ok(new ApiResponse("success", user));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody LoginRequest request) {
        // TODO: use try/catch here
        if (userService.verify(request)) {
            return ResponseEntity.ok(new ApiResponse("success", null));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("unauthorized", null));
    }
}
