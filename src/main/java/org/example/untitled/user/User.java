package org.example.untitled.user;

import jakarta.persistence.*;
import org.example.untitled.exception.UserHasActiveCasesException;
import org.example.untitled.usercase.*;

import java.time.Instant;
import java.util.ArrayList;
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
    private List<CaseEntity> ownedCases = new ArrayList<>();

    @OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY)
    private List<CaseEntity> assignedCases = new ArrayList<>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "uploadedBy", fetch = FetchType.LAZY)
    private List<UploadedFile> uploadedFiles = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public User() {
        // Empty constructor for JPA
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    @PreRemove
    protected void onRemove() {
        boolean hasActiveCases = ownedCases.stream()
                .anyMatch(c -> c.getStatus() == CaseStatus.OPEN
                        || c.getStatus() == CaseStatus.IN_PROGRESS);
        if (hasActiveCases) {
            throw new UserHasActiveCasesException();
        }
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
        this.ownedCases = ownedCases != null ? ownedCases : new ArrayList<>();
    }

    public List<CaseEntity> getAssignedCases() {
        return assignedCases;
    }

    public void setAssignedCases(List<CaseEntity> assignedCases) {
        this.assignedCases = assignedCases != null ? assignedCases : new ArrayList<>();
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments != null ? comments : new ArrayList<>();
    }

    public List<UploadedFile> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(List<UploadedFile> uploadedFiles) {
        this.uploadedFiles = uploadedFiles != null ? uploadedFiles : new ArrayList<>();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
