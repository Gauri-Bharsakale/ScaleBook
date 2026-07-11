package com.scalebook.scalebook_backend.repository;


import com.scalebook.scalebook_backend.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByIsActiveTrue();
}
