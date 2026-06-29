package com.scalebook.scalebook_backend.service;

import com.scalebook.scalebook_backend.entity.Resource;
import com.scalebook.scalebook_backend.exception.ResourceNotFoundException;
import com.scalebook.scalebook_backend.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor // Lombok: auto-generates a constructor for final fields below
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public List<Resource> getAllActiveResources() {
        return resourceRepository.findAll()
                .stream()
                .filter(Resource::getIsActive)
                .toList();
    }

    public Resource getResourceById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
    }
}