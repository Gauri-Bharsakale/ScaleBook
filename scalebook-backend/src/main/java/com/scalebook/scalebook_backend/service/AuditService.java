package com.scalebook.scalebook_backend.service;

import com.scalebook.scalebook_backend.entity.AuditLog;
import com.scalebook.scalebook_backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(String actorEmail, String action, String entityType, Long entityId, String details) {
        AuditLog entry = new AuditLog();
        entry.setActorEmail(actorEmail);
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setDetails(details);
        auditLogRepository.save(entry);
    }
}
