package org.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String s3key;

    private LocalDateTime uploadedAt;

    @ManyToOne
    private User uploadedBy;

    @ManyToOne
    private Case associatedCase;
}
