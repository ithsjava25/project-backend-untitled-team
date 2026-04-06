package org.example.untitled.usercase.mapper;

import org.example.untitled.usercase.CaseEntity;
import org.example.untitled.usercase.Comment;
import org.example.untitled.usercase.UploadedFile;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CommentDto;
import org.example.untitled.usercase.dto.UploadedFileDto;
import org.springframework.stereotype.Component;

@Component
public class CaseMapper {

    public CaseEntityDto toDto(CaseEntity entity) {
        if (entity == null) return null;
        CaseEntityDto dto = new CaseEntityDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        if (entity.getOwner() != null) {
            dto.setOwnerId(entity.getOwner().getId());
            dto.setOwnerUsername(entity.getOwner().getUsername());
        }
        if (entity.getAssignedTo() != null) {
            dto.setAssignedToId(entity.getAssignedTo().getId());
            dto.setAssignedToUsername(entity.getAssignedTo().getUsername());
        }
        return dto;
    }

    public CommentDto toDto(Comment comment) {
        if (comment == null) return null;
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreatedAt(comment.getCreatedAt());
        if (comment.getAuthor() != null) {
            dto.setAuthorId(comment.getAuthor().getId());
            dto.setAuthorUsername(comment.getAuthor().getUsername());
        }
        if (comment.getCaseEntity() != null) {
            dto.setCaseId(comment.getCaseEntity().getId());
        }
        return dto;
    }

    public UploadedFileDto toDto(UploadedFile file) {
        if (file == null) return null;
        UploadedFileDto dto = new UploadedFileDto();
        dto.setId(file.getId());
        dto.setFilename(file.getFilename());
        dto.setUploadedAt(file.getUploadedAt());
        if (file.getUploadedBy() != null) {
            dto.setUploadedById(file.getUploadedBy().getId());
            dto.setUploadedByUsername(file.getUploadedBy().getUsername());
        }
        if (file.getAssociatedCase() != null) {
            dto.setCaseId(file.getAssociatedCase().getId());
        }
        return dto;
    }
}
