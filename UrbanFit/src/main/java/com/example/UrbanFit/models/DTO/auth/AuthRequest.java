package com.example.UrbanFit.models.DTO.auth;

import jakarta.validation.constraints.NotBlank;

public class AuthRequest {
    @NotBlank(message = "Username or email cannot be empty")
    private String identifier;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    public AuthRequest() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
