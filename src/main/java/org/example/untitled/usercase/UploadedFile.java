package org.example.untitled.usercase;

import jakarta.persistence.*;
import org.example.untitled.user.User;

import java.time.Instant;

@Entity
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(name = "s3key")
    private String s3Key;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User uploadedBy;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private CaseEntity associatedCaseEntity;

    public UploadedFile() {
        // Empty constructor for JPA
    }

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public CaseEntity getAssociatedCase() {
        return associatedCaseEntity;
    }

    public void setAssociatedCase(CaseEntity associatedCaseEntity) {
        this.associatedCaseEntity = associatedCaseEntity;
    }
    public String getS3Key() {
        return s3Key;
    }
    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }
}
