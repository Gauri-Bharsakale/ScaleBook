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

}