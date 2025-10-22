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
@RequestMapping("${api.prefix}/search")
public class SearchController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse> findUsersWithSimilarUsername(@RequestParam String query) {
        List<User> users = userService.findUsersWithSimilarUsername(query);
        return ResponseEntity.ok(new ApiResponse("success", users));
    }
}
