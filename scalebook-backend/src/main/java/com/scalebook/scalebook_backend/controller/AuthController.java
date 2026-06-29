package com.scalebook.scalebook_backend.controller;

import com.scalebook.scalebook_backend.dto.request.LoginRequest;
import com.scalebook.scalebook_backend.dto.request.RegisterRequest;
import com.scalebook.scalebook_backend.dto.response.AuthResponse;
import com.scalebook.scalebook_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
