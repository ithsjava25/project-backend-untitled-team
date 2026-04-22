package org.example.untitled.usercase.repository;

import org.example.untitled.usercase.AuditLog;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends ListCrudRepository<AuditLog, Long> {
    List<AuditLog> findByCaseId(Long caseId);
}
