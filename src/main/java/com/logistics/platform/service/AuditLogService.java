package com.logistics.platform.service;
import com.logistics.platform.entity.AuditLog;
import com.logistics.platform.entity.User;
import com.logistics.platform.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(User user, String action, String entityType, UUID entityId, String notes, String ipAddress) {
        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .notes(notes)
                .ipAddress(ipAddress)
                .build();
        auditLogRepository.save(log);
    }
}


