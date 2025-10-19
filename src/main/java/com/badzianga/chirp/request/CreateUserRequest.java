package com.badzianga.chirp.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreateUserRequest {
    private String email;
    private String username;
    private String password;
}
