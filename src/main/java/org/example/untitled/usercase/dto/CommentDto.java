package org.example.untitled.usercase.dto;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String text,
        Long authorId,
        String authorUsername,
        Long caseId,
        LocalDateTime createdAt
){}
