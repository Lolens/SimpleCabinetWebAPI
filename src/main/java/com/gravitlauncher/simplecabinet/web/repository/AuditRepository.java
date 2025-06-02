package com.gravitlauncher.simplecabinet.web.repository;

import com.gravitlauncher.simplecabinet.web.model.AuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditEntity, Long> {
}
