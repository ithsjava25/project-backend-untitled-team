package org.example.untitled.usercase.mapper;

import org.example.untitled.user.User;
import org.example.untitled.usercase.CaseEntity;
import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.Comment;
import org.example.untitled.usercase.UploadedFile;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CommentDto;
import org.example.untitled.usercase.dto.UploadedFileDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CaseMapperTest {

    private User makeUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    @Test
    void toDto_caseEntity_shouldMapOwner() {
        CaseEntity entity = new CaseEntity();
        entity.setTitle("Bug report");
        entity.setStatus(CaseStatus.OPEN);
        entity.setOwner(makeUser(1L, "alice"));

        CaseEntityDto dto = CaseMapper.toDto(entity);

        assertEquals(1L, dto.ownerId());
        assertEquals("alice", dto.ownerUsername());
    }

    @Test
    void toDto_caseEntity_shouldLeaveAssignedToNull_whenNotAssigned() {
        CaseEntity entity = new CaseEntity();
        entity.setTitle("Bug report");
        entity.setStatus(CaseStatus.OPEN);
        entity.setOwner(makeUser(1L, "alice"));

        CaseEntityDto dto = CaseMapper.toDto(entity);

        assertNull(dto.assignedToId());
        assertNull(dto.assignedToUsername());
    }

    @Test
    void toDto_comment_shouldMapFields() {
        CaseEntity caseEntity = new CaseEntity();
        caseEntity.setId(10L);

        Comment comment = new Comment();
        comment.setText("This is a comment");
        comment.setAuthor(makeUser(1L, "alice"));
        comment.setCaseEntity(caseEntity);

        CommentDto dto = CaseMapper.toDto(comment);

        assertEquals("This is a comment", dto.text());
        assertEquals(1L, dto.authorId());
        assertEquals("alice", dto.authorUsername());
        assertEquals(10L, dto.caseId());
    }

    @Test
    void toDto_uploadedFile_shouldMapFields() {
        CaseEntity caseEntity = new CaseEntity();
        caseEntity.setId(10L);

        UploadedFile file = new UploadedFile();
        file.setFilename("report.pdf");
        file.setUploadedBy(makeUser(1L, "alice"));
        file.setAssociatedCase(caseEntity);

        UploadedFileDto dto = CaseMapper.toDto(file);

        assertEquals("report.pdf", dto.filename());
        assertEquals(1L, dto.uploadedById());
        assertEquals("alice", dto.uploadedByUsername());
        assertEquals(10L, dto.caseId());
    }
}
