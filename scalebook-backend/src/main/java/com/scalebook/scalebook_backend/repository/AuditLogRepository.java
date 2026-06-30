package com.scalebook.scalebook_backend.repository;

import com.scalebook.scalebook_backend.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
