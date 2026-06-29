package com.scalebook.scalebook_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "waitlist")
@Data
public class Waitlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    private LocalDateTime requestedStart;
    private LocalDateTime requestedEnd;

    private Integer position;

    private LocalDateTime createdAt = LocalDateTime.now();
}
