package com.scalebook.scalebook_backend.controller;

import com.scalebook.scalebook_backend.dto.request.BookingRequest;
import com.scalebook.scalebook_backend.entity.Booking;
import com.scalebook.scalebook_backend.entity.User;
import com.scalebook.scalebook_backend.repository.UserRepository;
import com.scalebook.scalebook_backend.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    // (in a real setup, we'd resolve userId from the Authentication object's email
    //  via a lookup - keeping that lookup logic in AuthService/UserService, not shown
    //  here to keep this phase focused on the locking logic itself)

    @PostMapping
    public Booking createBooking(@Valid @RequestBody BookingRequest request,
                                 Authentication authentication) {
        Long userId = resolveUserIdFromAuth(authentication); // helper, looks up user by email from token
        return bookingService.createBooking(
                userId, request.getResourceId(), request.getStartTime(), request.getEndTime()
        );
    }

    @Autowired
    private UserRepository userRepository;

    private Long resolveUserIdFromAuth(Authentication authentication) {

        if (authentication == null) {
            throw new RuntimeException("Unauthenticated");
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return user.getId();
    }

    @GetMapping("/my")
    public java.util.List<Booking> getMyBookings(
            Authentication authentication
    ) {

        Long userId = resolveUserIdFromAuth(authentication);

        return bookingService.getBookingsByUser(userId);
    }

    @PatchMapping("/{bookingId}/cancel")
    public Booking cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication
    ) {

        Long userId = resolveUserIdFromAuth(authentication);

        return bookingService.cancelBooking(
                bookingId,
                userId
        );
    }
}
