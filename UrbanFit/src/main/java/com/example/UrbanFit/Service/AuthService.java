package com.example.UrbanFit.Service;

import com.example.UrbanFit.Service.interfaces.AuthServiceInterface;
import com.example.UrbanFit.models.DTO.AuthRequest;
import com.example.UrbanFit.models.DTO.RegisterRequest;
import com.example.UrbanFit.models.Users;
import com.example.UrbanFit.models.enums.Roles;
import com.example.UrbanFit.repository.UserRepo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements AuthServiceInterface {
    private final UserRepo userRepository;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepo userRepository, JWTService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<String> register(RegisterRequest request) {
        try {
            if (userRepository.findByUsername(request.getUsername()).isPresent() ||
                    userRepository.findByEmail(request.getEmail()).isPresent()) {
                return new ResponseEntity<>("Username or Email is already taken!", HttpStatus.BAD_REQUEST);
            }

            Roles role = (request.getRole() != null) ? request.getRole() : Roles.USER;

            Users newUser = new Users();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setPhoneNumber(request.getPhoneNumber());
            newUser.setRole(role);

            userRepository.save(newUser);

            return ResponseEntity.ok("User registered successfully with role: " + role);
        } catch (DataIntegrityViolationException exp) {
            return new ResponseEntity<>("Username or Email is already taken!", HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> login(AuthRequest request) {
        try {
            Optional<Users> userOpt = userRepository.findByUsername(request.getIdentifier())
                    .or(() -> userRepository.findByEmail(request.getIdentifier()));

            if (userOpt.isEmpty()) {
                return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
            }

            Users user = userOpt.get();

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
            );

            String jwtToken = jwtService.generateToken(user);

            return ResponseEntity.ok(jwtToken);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Login failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
