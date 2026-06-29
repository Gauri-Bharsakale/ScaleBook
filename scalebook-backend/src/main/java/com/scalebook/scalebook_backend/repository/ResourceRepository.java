package com.scalebook.scalebook_backend.repository;


import com.scalebook.scalebook_backend.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    // basic save/find/delete are already included automatically
}
