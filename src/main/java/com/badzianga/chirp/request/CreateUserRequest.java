package com.badzianga.chirp.request;

public class CreateUserRequest {
    public String email;
    public String username;
    public String password;

    public CreateUserRequest(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
