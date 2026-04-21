package org.example.untitled.usercase.dto;

import java.time.Instant;

public record UploadedFileDto(
        Long id,
        String filename,
        Long uploadedById,
        String uploadedByUsername,
        Long caseId,
        Instant uploadedAt
    ){}
