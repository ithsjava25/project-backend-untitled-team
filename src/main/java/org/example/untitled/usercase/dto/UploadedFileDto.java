package org.example.untitled.usercase.dto;

import java.time.LocalDateTime;

public record UploadedFileDto(
        Long id,
        String filename,
        Long uploadedById,
        String uploadedByUsername,
        Long caseId,
        LocalDateTime uploadedAt
    ){}
