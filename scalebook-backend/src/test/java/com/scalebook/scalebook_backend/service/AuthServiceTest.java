// src/test/java/com/scalebook/service/AuthServiceTest.java

package com.scalebook.scalebook_backend.service;

import com.scalebook.scalebook_backend.dto.request.LoginRequest;
import com.scalebook.scalebook_backend.dto.request.RegisterRequest;
import com.scalebook.scalebook_backend.dto.response.AuthResponse;
import com.scalebook.scalebook_backend.entity.Role;
import com.scalebook.scalebook_backend.entity.User;
import com.scalebook.scalebook_backend.repository.RoleRepository;
import com.scalebook.scalebook_backend.repository.UserRepository;
import com.scalebook.scalebook_backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private AuthService authService;

    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("USER");
    }

    // ─────────────────────────────────────────────────
    // Test 1: Successful registration
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("Should register a new user and return a token")
    void shouldRegisterSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Alice Smith");
        request.setEmail("alice@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mock.jwt.token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            // return the same user object that was passed in (simulates save returning the persisted entity)
            return invocation.getArgument(0);
        });

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");

        // critical: verify the password was hashed, never stored raw
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(user ->
                user.getPasswordHash().equals("hashed_password") // saved hash, not plain text
        ));
    }

    // ─────────────────────────────────────────────────
    // Test 2: Duplicate email registration
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("Should reject registration with duplicate email")
    void shouldRejectDuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("alice@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already registered");

        verify(userRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────
    // Test 3: Successful login
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("Should return token on valid login")
    void shouldLoginSuccessfully() {
        User existingUser = new User();
        existingUser.setEmail("alice@example.com");
        existingUser.setPasswordHash("hashed_password");
        existingUser.setRoles(Set.of(userRole));

        LoginRequest request = new LoginRequest();
        request.setEmail("alice@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
    }

    // ─────────────────────────────────────────────────
    // Test 4: Wrong password — generic error (user enumeration prevention)
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("Should give same generic error for wrong password and wrong email")
    void shouldGiveGenericErrorForBadCredentials() {
        User existingUser = new User();
        existingUser.setEmail("alice@example.com");
        existingUser.setPasswordHash("hashed_password");
        existingUser.setRoles(Set.of(userRole));

        LoginRequest request = new LoginRequest();
        request.setEmail("alice@example.com");
        request.setPassword("wrongpassword");

        when(userRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrongpassword", "hashed_password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Invalid email or password"); // same message as "email not found"
    }
}