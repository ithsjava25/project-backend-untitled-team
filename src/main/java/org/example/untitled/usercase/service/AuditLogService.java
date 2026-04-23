package org.example.untitled.usercase.service;

import org.example.untitled.usercase.AuditAction;
import org.example.untitled.usercase.AuditLog;
import org.example.untitled.usercase.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service for logging audit events in the system.
 */
@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(AuditAction action, Long userId, Long caseId) {
        auditLogRepository.save(new AuditLog(action, userId, caseId));
    }

    public List<AuditLog> getLogsForCase(Long caseId) {
        return auditLogRepository.findByCaseId(caseId);
    }
}
