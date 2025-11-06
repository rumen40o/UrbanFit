package com.example.UrbanFit.controller;

import com.example.UrbanFit.Service.interfaces.AuthServiceInterface;
import com.example.UrbanFit.models.DTO.AuthRequest;
import com.example.UrbanFit.models.DTO.RegisterRequest;
import com.example.UrbanFit.models.Users;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins ="http://localhost:3000")
public class AuthController {

    private final AuthServiceInterface authenticationService;

    public AuthController(AuthServiceInterface authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        ResponseEntity<String> responseEntity = authenticationService.login(request);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String jwtToken = responseEntity.getBody();

            Cookie jwtCookie = new Cookie("jwt_token", jwtToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(60 * 60); // 1 час

            response.addCookie(jwtCookie);
        }

        return responseEntity;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {

        Cookie jwtCookie = new Cookie("jwt_token", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);

        response.addCookie(jwtCookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/logged/user")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Users user) {
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return ResponseEntity.ok(user);
    }
}
