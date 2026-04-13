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
        User owner = entity.getOwner();
        User assigned = entity.getAssignedTo();
        return new CaseEntityDto(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                owner.getId(),
                owner.getUsername(),
                assigned.getId(),
                assigned.getUsername(),
                entity.getCreatedAt()
        );
    }

    public CommentDto toDto(Comment comment) {
        if (comment == null) return null;
        User author = comment.getAuthor();

        return new CommentDto(
                comment.getId(),
                comment.getText(),
                author.getId(),
                author.getUsername(),
                comment.getCaseEntity().getId(),
                comment.getCreatedAt()

        );
    }

    public UploadedFileDto toDto(UploadedFile file) {
        if (file == null) return null;
        User uploadedBy = file.getUploadedBy();
        return new UploadedFileDto(
            file.getId(),
            file.getFilename(),
            uploadedBy.getId(),
                uploadedBy.getUsername(),
                file.getAssociatedCase().getId(),
                file.getUploadedAt()
        );
    }
}
