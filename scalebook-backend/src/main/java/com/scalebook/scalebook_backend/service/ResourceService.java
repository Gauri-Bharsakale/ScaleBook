package com.scalebook.scalebook_backend.service;

import com.scalebook.scalebook_backend.entity.Resource;
import com.scalebook.scalebook_backend.exception.ResourceNotFoundException;
import com.scalebook.scalebook_backend.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor // Lombok: auto-generates a constructor for final fields below
public class ResourceService {

    private final ResourceRepository resourceRepository;

    @Cacheable(value = "resources", key = "'all-active'")
    public List<Resource> getAllActiveResources() {
        System.out.println("Hitting the database..."); // you'll only see this print on a cache MISS
        return resourceRepository.findAll()
                .stream()
                .filter(Resource::getIsActive)
                .toList();
    }

    @CacheEvict(value = "resources", key = "'all-active'")
    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    public Resource getResourceById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
    }

    public Resource updateResource(Long id, Resource updatedResource) {

        Resource existing = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        existing.setName(updatedResource.getName());
        existing.setDescription(updatedResource.getDescription());
        existing.setCapacity(updatedResource.getCapacity());
        existing.setIsActive(updatedResource.getIsActive());

        return resourceRepository.save(existing);
    }

    public void deleteResource(Long id) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        // Soft delete
        resource.setIsActive(false);

        resourceRepository.save(resource);
    }
}