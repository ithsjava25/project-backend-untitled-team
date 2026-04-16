package org.example.untitled.usercase.dto;

import java.time.Instant;

public record CommentDto(
        Long id,
        String text,
        Long authorId,
        String authorUsername,
        Long caseId,
        Instant createdAt
){}
