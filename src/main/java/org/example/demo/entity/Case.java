package org.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.ORDINAL)
    private CaseStatus status;

    @ManyToOne
    private User owner;

    @ManyToOne
    private User assignedTo;

    private LocalDateTime createdAt;

}
