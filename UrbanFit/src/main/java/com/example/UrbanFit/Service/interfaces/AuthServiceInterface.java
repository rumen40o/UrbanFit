package com.example.UrbanFit.Service.interfaces;

import com.example.UrbanFit.models.DTO.auth.AuthRequest;
import com.example.UrbanFit.models.DTO.auth.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthServiceInterface {
    ResponseEntity<String> register(RegisterRequest request);
    ResponseEntity<String> login(AuthRequest request);
}
