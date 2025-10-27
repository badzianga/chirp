package com.badzianga.chirp.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreatePostRequest {
    String content;
    Long userId;
}
