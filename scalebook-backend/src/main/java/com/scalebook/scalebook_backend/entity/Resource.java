package com.scalebook.scalebook_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "resources")
@Data
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer capacity;

    private Boolean isActive = true; // soft-delete flag, explained in Phase 2
}