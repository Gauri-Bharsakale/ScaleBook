package com.scalebook.scalebook_backend.repository;


import com.scalebook.scalebook_backend.entity.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {
    List<Waitlist> findByResourceIdOrderByPositionAsc(Long resourceId);
}