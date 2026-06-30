package com.scalebook.scalebook_backend.service;

import com.scalebook.scalebook_backend.entity.*;
import com.scalebook.scalebook_backend.exception.ResourceAlreadyBookedException;
import com.scalebook.scalebook_backend.exception.ResourceNotFoundException;
import com.scalebook.scalebook_backend.repository.BookingRepository;
import com.scalebook.scalebook_backend.repository.ResourceRepository;
import com.scalebook.scalebook_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public Booking createBooking(Long userId, Long resourceId, LocalDateTime start, LocalDateTime end) {

        // Lock key is specific to THIS resource - bookings on different resources don't block each other
        String lockKey = "booking-lock:resource:" + resourceId;
        RLock lock = redissonClient.getLock(lockKey);

        boolean acquired = false;
        try {
            // Try to get the lock: wait up to 5 seconds, hold it for max 10 seconds
            acquired = lock.tryLock(5, 10, TimeUnit.SECONDS);

            if (!acquired) {
                throw new IllegalStateException("System is busy processing this resource, please try again");
            }

            // Only ONE thread, across ALL servers, reaches this point at a time for this resourceId
            return createBookingInternal(userId, resourceId, start, end);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Booking interrupted, please try again");
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock(); // ALWAYS release the lock, even if something failed
            }
        }
    }

    @Transactional
    protected Booking createBookingInternal(Long userId, Long resourceId, LocalDateTime start, LocalDateTime end) {

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Booking> conflicts = bookingRepository
                .findByResourceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        resourceId, BookingStatus.CONFIRMED, end, start
                );

        if (!conflicts.isEmpty()) {
            throw new ResourceAlreadyBookedException("This resource is already booked for the requested time");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setResource(resource);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        notificationService.queueBookingConfirmation(
                booking.getId(), user.getEmail(), resource.getName()
        );

        auditService.log(user.getEmail(), "BOOKING_CREATED", "Booking", booking.getId(),
                "Resource: " + resource.getName() + ", Time: " + start + " to " + end);


        return savedBooking;
    }
}