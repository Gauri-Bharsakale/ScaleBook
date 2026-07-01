package com.scalebook.scalebook_backend.repository;


import com.scalebook.scalebook_backend.entity.Booking;
import com.scalebook.scalebook_backend.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);
    // find bookings for one resource that overlap a time range and are still confirmed
    List<Booking> findByResourceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Long resourceId, BookingStatus status, LocalDateTime end, LocalDateTime start
    );
}
