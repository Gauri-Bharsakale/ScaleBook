package com.scalebook.scalebook_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actorEmail;     // who did it
    private String action;          // e.g. "BOOKING_CREATED", "BOOKING_CANCELLED"
    private String entityType;      // e.g. "Booking"
    private Long entityId;          // which booking/resource
    private String details;         // free-text extra info

    private LocalDateTime timestamp = LocalDateTime.now();
}
