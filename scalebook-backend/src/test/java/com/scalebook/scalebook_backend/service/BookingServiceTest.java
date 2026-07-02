// src/test/java/com/scalebook/service/BookingServiceTest.java

package com.scalebook.scalebook_backend.service;

import com.scalebook.scalebook_backend.entity.*;
import com.scalebook.scalebook_backend.exception.ResourceAlreadyBookedException;
import com.scalebook.scalebook_backend.exception.ResourceNotFoundException;
import com.scalebook.scalebook_backend.repository.BookingRepository;
import com.scalebook.scalebook_backend.repository.ResourceRepository;
import com.scalebook.scalebook_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.redisson.api.RedissonClient;
import org.redisson.api.RLock;



@ExtendWith(MockitoExtension.class)          // tells JUnit to use Mockito
class BookingServiceTest {

    // @Mock creates a fake version of these — no real DB calls happen
    @Mock private BookingRepository bookingRepository;
    @Mock private ResourceRepository resourceRepository;
    @Mock private UserRepository userRepository;
    @Mock private AuditService auditService;
    @Mock private RedissonClient redissonClient;
    @Mock private RLock rLock;
    @Mock private NotificationService notificationService;

    // @InjectMocks creates the real BookingService and injects the mocks above into it
    @InjectMocks private BookingService bookingService;

    // test data we'll reuse across tests
    private User testUser;
    private Resource testResource;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("alice@example.com");

        testResource = new Resource();
        testResource.setId(1L);
        testResource.setName("Room A");
        testResource.setIsActive(true);

        start = LocalDateTime.now().plusHours(1);
        end   = LocalDateTime.now().plusHours(2);
    }

    // ─────────────────────────────────────────────────
    // Test 1: Happy path — no conflicts, booking succeeds
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("Should create booking when no conflict exists")
    void shouldCreateBookingWhenNoConflict() {

        // ARRANGE
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(testResource));

        when(bookingRepository
                .findByResourceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        anyLong(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        Booking savedBooking = new Booking();
        savedBooking.setId(42L);
        savedBooking.setUser(testUser);
        savedBooking.setResource(testResource);
        savedBooking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(savedBooking);

        doNothing().when(notificationService)
                .queueBookingConfirmation(anyLong(), anyString(), anyString());

        doNothing().when(auditService)
                .log(anyString(), anyString(), anyString(), anyLong(), anyString());

        // ACT
        Booking result =
                bookingService.createBookingInternal(1L, 1L, start, end);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);

        verify(bookingRepository).save(any(Booking.class));
    }

    // ─────────────────────────────────────────────────
    // Test 2: Conflict exists — must be rejected
    // ─────────────────────────────────────────────────
    @Test
    @DisplayName("Should throw exception when slot is already booked")
    void shouldThrowWhenSlotAlreadyBooked() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));

        // this time, the fake returns an existing conflicting booking
        Booking existingBooking = new Booking();
        existingBooking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository
                .findByResourceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        anyLong(), any(), any(), any()))
                .thenReturn(List.of(existingBooking));

        // ACT + ASSERT — expect this specific exception to be thrown
        assertThatThrownBy(() ->
                bookingService.createBookingInternal(1L, 1L, start, end))
                .isInstanceOf(ResourceAlreadyBookedException.class)
                .hasMessageContaining("already booked");

        // also verify we never tried to save anything
        verify(bookingRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────
    // Test 3: Resource doesn't exist
    // ─────────────────────────────────────────────────
    @Test
    void shouldThrowWhenResourceNotFound() {

//        when(userRepository.findById(1L))
//                .thenReturn(Optional.of(testUser));

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                bookingService.createBookingInternal(1L,1L,start,end))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(bookingRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────
    // Test 4: User doesn't exist
    // ─────────────────────────────────────────────────
    @Test
    void shouldThrowWhenUserNotFound() {

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(testResource));

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                bookingService.createBookingInternal(1L,1L,start,end))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(bookingRepository, never()).save(any());
    }
}
