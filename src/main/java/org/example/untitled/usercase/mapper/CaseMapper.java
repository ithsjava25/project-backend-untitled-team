package org.example.untitled.usercase.mapper;

import org.example.untitled.user.User;
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

        User owner = entity.getOwner();
        if (owner != null) {
            dto.setOwnerId(owner.getId());
            dto.setOwnerUsername(owner.getUsername());
        }

        User assigned = entity.getAssignedTo();
        if (assigned != null) {
            dto.setAssignedToId(assigned.getId());
            dto.setAssignedToUsername(assigned.getUsername());
        }

        return dto;
    }

    public CommentDto toDto(Comment comment) {
        if (comment == null) return null;
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreatedAt(comment.getCreatedAt());

        User author = comment.getAuthor();
        if (author != null) {
            dto.setAuthorId(author.getId());
            dto.setAuthorUsername(author.getUsername());
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

        User uploadedBy = file.getUploadedBy();
        if (uploadedBy != null) {
            dto.setUploadedById(uploadedBy.getId());
            dto.setUploadedByUsername(uploadedBy.getUsername());
        }

        if (file.getAssociatedCase() != null) {
            dto.setCaseId(file.getAssociatedCase().getId());
        }

        return dto;
    }
}
