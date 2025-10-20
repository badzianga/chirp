package com.badzianga.chirp.controller;

import com.badzianga.chirp.model.User;
import com.badzianga.chirp.response.ApiResponse;
import com.badzianga.chirp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final UserService service;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<User> users = service.getAllUsers();
        return ResponseEntity.ok(new ApiResponse("success", users));
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<ApiResponse> findUsersWithSimilarUsername(@PathVariable String username) {
        List<User> users = service.findUsersWithSimilarUsername(username);
        return ResponseEntity.ok(new ApiResponse("success", users));
    }
}
