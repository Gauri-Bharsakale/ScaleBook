// src/test/java/com/scalebook/controller/BookingControllerIntegrationTest.java

package com.scalebook.scalebook_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalebook.scalebook_backend.dto.request.BookingRequest;
import com.scalebook.scalebook_backend.entity.*;
import com.scalebook.scalebook_backend.repository.*;
import com.scalebook.scalebook_backend.service.AuditService;
import com.scalebook.scalebook_backend.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest                 // spins up the full Spring application context
@AutoConfigureMockMvc           // gives us MockMvc to make fake HTTP requests without a real server
@ActiveProfiles("test")         // uses application-test.yml config above
class BookingControllerIntegrationTest {

    @MockitoBean private NotificationService notificationService;

    @MockitoBean private AuditService auditService;

    @MockitoBean private RedissonClient redissonClient;

    @Mock private RLock rLock;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private ResourceRepository resourceRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String jwtToken;
    private Resource testResource;

    @BeforeEach
    void setUp() throws Exception {
        // clear everything before each test — tests must be independent
        bookingRepository.deleteAll();
        resourceRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // seed a role
        Role role = new Role();
        role.setName("USER");
        roleRepository.save(role);

        when(redissonClient.getLock(anyString()))
                .thenReturn(rLock);

        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);

        when(rLock.isHeldByCurrentThread())
                .thenReturn(true);

        // seed a user
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setRoles(Set.of(role));
        userRepository.save(user);

        // seed a resource
        testResource = new Resource();
        testResource.setName("Room A");
        testResource.setIsActive(true);
        resourceRepository.save(testResource);

        // log in to get a real JWT token
        String loginBody = """
            { "email": "test@example.com", "password": "password123" }
        """;

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        jwtToken = objectMapper.readTree(response).get("token").asText();


    }

    // ─────────────────────────────────────────────────
    // Test 1: Create booking successfully
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("Should create booking and return 200")
    void shouldCreateBookingSuccessfully() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setResourceId(testResource.getId());
        request.setStartTime(LocalDateTime.now().plusHours(2));
        request.setEndTime(LocalDateTime.now().plusHours(3));

//        mockMvc.perform(post("/api/auth/login"));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.resource.name").value("Room A"));
    }

    // ─────────────────────────────────────────────────
    // Test 2: Double booking same slot — must get 409
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("Should return 409 when slot is already booked")
    void shouldReturn409OnDoubleBooking() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end   = LocalDateTime.now().plusHours(3);

        BookingRequest request = new BookingRequest();
        request.setResourceId(testResource.getId());
        request.setStartTime(start);
        request.setEndTime(end);

        String body = objectMapper.writeValueAsString(request);

        // First booking — should succeed
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(body))
                .andExpect(status().isOk());

        // Second booking — same slot, same resource — must be rejected
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(body))
                .andExpect(status().isConflict())           // HTTP 409
                .andExpect(jsonPath("$.message").value(containsString("already booked")));
    }

    // ─────────────────────────────────────────────────
    // Test 3: Unauthenticated request — must get 401
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("Should reject request with no JWT token")
    void shouldRejectUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized()); // 401
    }

    // ─────────────────────────────────────────────────
    // Test 4: Invalid request body — must get 400
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("Should return 400 for missing required fields")
    void shouldReturn400ForInvalidBody() throws Exception {
        // no resourceId, no times — should fail validation
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content("{}"))
                .andExpect(status().isBadRequest()); // 400
    }
}
