package org.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<CaseEntity> ownedCases;

    @OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY)
    private List<CaseEntity> assignedCases;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToMany(mappedBy = "uploadedBy", fetch = FetchType.LAZY)
    private List<UploadedFile> uploadedFiles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<AuditLog> auditLogs;

    private LocalDateTime createdAt;

    public User() {
        // Empty constructor for JPA
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<CaseEntity> getOwnedCases() {
        return ownedCases;
    }

    public void setOwnedCases(List<CaseEntity> ownedCases) {
        this.ownedCases = ownedCases;
    }

    public List<CaseEntity> getAssignedCases() {
        return assignedCases;
    }

    public void setAssignedCases(List<CaseEntity> assignedCases) {
        this.assignedCases = assignedCases;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<UploadedFile> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(List<UploadedFile> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public List<AuditLog> getAuditLogs() {
        return auditLogs;
    }

    public void setAuditLogs(List<AuditLog> auditLogs) {
        this.auditLogs = auditLogs;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
