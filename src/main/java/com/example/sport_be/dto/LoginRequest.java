package com.example.sport_be.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String identifier;
    private String password;
}
