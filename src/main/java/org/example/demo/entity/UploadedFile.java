package org.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String s3key;

    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    private CaseEntity associatedCaseEntity;

    public UploadedFile() {
        // Empty constructor for JPA
    }

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
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

    public String getS3key() {
        return s3key;
    }

    public void setS3key(String s3key) {
        this.s3key = s3key;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
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
}
