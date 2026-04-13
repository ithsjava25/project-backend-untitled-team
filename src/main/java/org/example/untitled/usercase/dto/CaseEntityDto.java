package org.example.untitled.usercase.dto;

import org.example.untitled.usercase.CaseStatus;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public record CaseEntityDto(
        Long id,
        String title,
        String description,
        CaseStatus status,
        Long ownerId,
        String ownerUsername,
        @Value("")
        Long assignedToId,
        @Value("")
        String assignedToUsername,
        LocalDateTime createdAt
){
}
