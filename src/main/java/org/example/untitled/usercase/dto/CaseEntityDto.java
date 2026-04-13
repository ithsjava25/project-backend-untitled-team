package org.example.untitled.usercase.dto;

import org.example.untitled.usercase.CaseStatus;

import java.time.LocalDateTime;

public record CaseEntityDto(
        Long id,
        String title,
        String description,
        CaseStatus status,
        Long ownerId,
        String ownerUsername,
        Long assignedToId,
        String assignedToUsername,
        LocalDateTime createdAt
){
}
