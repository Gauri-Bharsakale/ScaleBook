package com.scalebook.scalebook_backend;

import com.scalebook.scalebook_backend.entity.*;
import com.scalebook.scalebook_backend.repository.BookingRepository;
import com.scalebook.scalebook_backend.repository.ResourceRepository;
import com.scalebook.scalebook_backend.repository.UserRepository;
import com.scalebook.scalebook_backend.service.BookingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookingServiceConcurrencyTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserRepository userRepository;

    private Long userId;
    private Long resourceId;

    @BeforeEach
    void setup() {

        // clean old data
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        resourceRepository.deleteAll();

        // create user
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test123@test.com");
        user.setPasswordHash("abc");

        user = userRepository.save(user);
        userId = user.getId();

        // create resource
        Resource resource = new Resource();
        resource.setName("Meeting Room");
        resource.setDescription("Test room");
        resource.setCapacity(5);
        resource.setIsActive(true);

        resource = resourceRepository.save(resource);
        resourceId = resource.getId();
    }

    @Test
    void shouldPreventDoubleBookingUnderConcurrency()
            throws InterruptedException {

        LocalDateTime start =
                LocalDateTime.now().plusHours(1);

        LocalDateTime end =
                start.plusHours(1);

        Runnable bookingTask = () -> {
            try {

                bookingService.createBooking(
                        userId,
                        resourceId,
                        start,
                        end
                );

            } catch (Exception e) {
                System.out.println(
                        "Booking failed: "
                                + e.getMessage()
                );
            }
        };

        Thread t1 = new Thread(bookingTask);
        Thread t2 = new Thread(bookingTask);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        List<Booking> bookings =
                bookingRepository
                        .findByResourceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                                resourceId,
                                BookingStatus.CONFIRMED,
                                end,
                                start
                        );

        System.out.println(
                "Bookings created = "
                        + bookings.size()
        );

        assertEquals(
                1,
                bookings.size(),
                "Only one booking should exist"
        );
    }
}