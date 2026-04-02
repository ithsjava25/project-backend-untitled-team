package org.example.untitled.usercase;

import jakarta.persistence.*;
import org.example.untitled.User.User;

import java.time.LocalDateTime;

@Entity
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private CaseEntity caseEntity;

    public AuditLog() {
        // Empty constructor for JPA
    }

    public AuditLog(AuditAction action, User user, CaseEntity caseEntity) {
        this.action = action;
        this.user = user;
        this.caseEntity = caseEntity;
    }

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public AuditAction getAction() {
        return action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public User getUser() {
        return user;
    }

    public CaseEntity getCaseEntity() {
        return caseEntity;
    }
}
