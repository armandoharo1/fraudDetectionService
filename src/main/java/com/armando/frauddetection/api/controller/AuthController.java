package com.armando.frauddetection.api.controller;

import com.armando.frauddetection.api.controller.dto.LoginRequest;
import com.armando.frauddetection.api.controller.dto.LoginResponse;
import com.armando.frauddetection.api.error.AuthenticationException;
import com.armando.frauddetection.domain.repository.UserRepository;
import com.armando.frauddetection.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new AuthenticationException("User not found"));

        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        return ResponseEntity.ok(
                new LoginResponse(token, user.getUsername(), user.getRole().name())
        );
    }
}
