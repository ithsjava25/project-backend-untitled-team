package org.example.untitled.usercase.mapper;

import org.example.untitled.user.User;
import org.example.untitled.usercase.CaseEntity;
import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.Comment;
import org.example.untitled.usercase.UploadedFile;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CommentDto;
import org.example.untitled.usercase.dto.UploadedFileDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CaseMapperTest {

    private CaseMapper caseMapper;

    private User makeUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    @BeforeEach
    void setUp() {
        caseMapper = new CaseMapper();
    }

    @Test
    void toDto_caseEntity_shouldMapOwner() {
        CaseEntity entity = new CaseEntity();
        entity.setTitle("Bug report");
        entity.setStatus(CaseStatus.OPEN);
        entity.setOwner(makeUser(1L, "alice"));

        CaseEntityDto dto = caseMapper.toDto(entity);

        assertEquals(1L, dto.getOwnerId());
        assertEquals("alice", dto.getOwnerUsername());
    }

    @Test
    void toDto_caseEntity_shouldLeaveAssignedToNull_whenNotAssigned() {
        CaseEntity entity = new CaseEntity();
        entity.setTitle("Bug report");
        entity.setStatus(CaseStatus.OPEN);
        entity.setOwner(makeUser(1L, "alice"));

        CaseEntityDto dto = caseMapper.toDto(entity);

        assertNull(dto.getAssignedToId());
        assertNull(dto.getAssignedToUsername());
    }

    @Test
    void toDto_comment_shouldMapFields() {
        CaseEntity caseEntity = new CaseEntity();
        caseEntity.setId(10L);

        Comment comment = new Comment();
        comment.setText("This is a comment");
        comment.setAuthor(makeUser(1L, "alice"));
        comment.setCaseEntity(caseEntity);

        CommentDto dto = caseMapper.toDto(comment);

        assertEquals("This is a comment", dto.getText());
        assertEquals(1L, dto.getAuthorId());
        assertEquals("alice", dto.getAuthorUsername());
        assertEquals(10L, dto.getCaseId());
    }

    @Test
    void toDto_uploadedFile_shouldMapFields() {
        CaseEntity caseEntity = new CaseEntity();
        caseEntity.setId(10L);

        UploadedFile file = new UploadedFile();
        file.setFilename("report.pdf");
        file.setUploadedBy(makeUser(1L, "alice"));
        file.setAssociatedCase(caseEntity);

        UploadedFileDto dto = caseMapper.toDto(file);

        assertEquals("report.pdf", dto.getFilename());
        assertEquals(1L, dto.getUploadedById());
        assertEquals("alice", dto.getUploadedByUsername());
        assertEquals(10L, dto.getCaseId());
    }
}
