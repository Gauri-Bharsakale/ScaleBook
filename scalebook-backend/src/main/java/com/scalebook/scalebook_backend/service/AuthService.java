package com.scalebook.scalebook_backend.service;

import com.scalebook.scalebook_backend.dto.request.LoginRequest;
import com.scalebook.scalebook_backend.dto.request.RegisterRequest;
import com.scalebook.scalebook_backend.dto.response.AuthResponse;
import com.scalebook.scalebook_backend.entity.Role;
import com.scalebook.scalebook_backend.entity.User;
import com.scalebook.scalebook_backend.repository.RoleRepository;
import com.scalebook.scalebook_backend.repository.UserRepository;
import com.scalebook.scalebook_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered");
        }

        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Default role not seeded in DB"));

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); // hash here, never store raw
        user.setRoles(Set.of(defaultRole));

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), "USER");
        return new AuthResponse(token, user.getEmail(), "USER");
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalStateException("Invalid email or password");
        }

        String role = user.getRoles().iterator().next().getName();
        String token = jwtUtil.generateToken(user.getEmail(), role);

        return new AuthResponse(token, user.getEmail(), role);
    }
}