package org.example.untitled.usercase;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Column
    private Long userId;

    @Column
    private Long caseId;

    public AuditLog() {}

    public AuditLog(AuditAction action, Long userId, Long caseId) {
        this.action = action;
        this.userId = userId;
        this.caseId = caseId;
    }

    @PrePersist
    protected void onCreate() {
        this.timestamp = Instant.now();
    }

    public Long getId() { return id; }
    public AuditAction getAction() { return action; }
    public Instant getTimestamp() { return timestamp; }
    public Long getUserId() { return userId; }
    public Long getCaseId() { return caseId; }
}
