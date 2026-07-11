package com.scalebook.scalebook_backend.controller;

import com.scalebook.scalebook_backend.entity.Booking;
import com.scalebook.scalebook_backend.entity.Resource;
import com.scalebook.scalebook_backend.repository.BookingRepository;
import com.scalebook.scalebook_backend.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // every method in this controller requires ADMIN role
public class AdminController {

    private final BookingRepository bookingRepository;
    private final ResourceService resourceService;

    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @PostMapping("/resources")
    public Resource createResource(@RequestBody Resource resource) {
        return resourceService.createResource(resource);
    }
}
