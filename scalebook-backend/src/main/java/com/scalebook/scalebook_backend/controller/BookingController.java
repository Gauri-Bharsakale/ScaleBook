package com.scalebook.scalebook_backend.controller;

import com.scalebook.scalebook_backend.dto.request.BookingRequest;
import com.scalebook.scalebook_backend.entity.Booking;
import com.scalebook.scalebook_backend.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    private Long resolveUserIdFromAuth(Authentication authentication) {
        // placeholder - real implementation calls UserRepository.findByEmail(authentication.getName())
        throw new UnsupportedOperationException("Implement using UserRepository lookup by email");
    }
}
