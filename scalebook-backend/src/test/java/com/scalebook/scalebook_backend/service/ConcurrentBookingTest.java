// src/test/java/com/scalebook/service/ConcurrentBookingTest.java

package com.scalebook.scalebook_backend.service;

import com.scalebook.scalebook_backend.entity.*;
import com.scalebook.scalebook_backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ConcurrentBookingTest {

    @Autowired private BookingService bookingService;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private ResourceRepository resourceRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Resource testResource;
    private List<Long> userIds;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        resourceRepository.deleteAll();

        Role role = new Role();
        role.setName("USER");
        roleRepository.save(role);

        // create 10 different users (simulating 10 people trying to book simultaneously)
        userIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setEmail("user" + i + "@example.com");
            user.setFullName("User " + i);
            user.setPasswordHash(passwordEncoder.encode("password"));
            user.setRoles(Set.of(role));
            User saved = userRepository.save(user);
            userIds.add(saved.getId());
        }

        testResource = new Resource();
        testResource.setName("Room A");
        testResource.setIsActive(true);
        resourceRepository.save(testResource);
    }

    @Test
    @DisplayName("Only ONE booking should succeed when 10 users book the same slot simultaneously")
    void onlyOneBookingShouldSucceedUnderConcurrency() throws InterruptedException {
        int numberOfThreads = 10;
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end   = LocalDateTime.now().plusHours(2);

        // CountDownLatch makes all threads start at the exact same moment
        // (without this, threads start one-by-one, not truly concurrent)
        CountDownLatch startSignal = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            final Long userId = userIds.get(i);

            futures.add(executor.submit(() -> {
                try {
                    startSignal.await(); // all threads wait here until we release the signal below
                    bookingService.createBooking(userId, testResource.getId(), start, end);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
            }));
        }

        // Release all 10 threads at once — true concurrent requests
        startSignal.countDown();

        // wait for all threads to finish
        for (Future<?> future : futures) {
            try { future.get(15, TimeUnit.SECONDS); }
            catch (Exception ignored) {}
        }
        executor.shutdown();

        // Check the database — how many confirmed bookings actually exist?
        List<Booking> confirmedBookings = bookingRepository
                .findByResourceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        testResource.getId(), BookingStatus.CONFIRMED, end, start
                );

        System.out.println("Threads that succeeded: " + successCount.get());
        System.out.println("Threads that were rejected: " + failureCount.get());
        System.out.println("Confirmed bookings in DB: " + confirmedBookings.size());

        // THE CRITICAL ASSERTION:
        assertThat(confirmedBookings.size())
                .as("Only ONE booking should exist, regardless of how many simultaneous requests arrived")
                .isEqualTo(1);

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(9);
    }
}
