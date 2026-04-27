package org.example.untitled.usercase.repository;

import org.example.untitled.usercase.CaseEntity;
import org.example.untitled.usercase.Comment;
import org.example.untitled.usercase.dto.CommentDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for managing {@link Comment} entities.
 * Provides a custom query for retrieving comments associated with a specific ticket,
 * ordered chronologically.
 */
public interface CommentRepository extends ListCrudRepository<Comment, Long> {

    @Query("SELECT new org.example.untitled.usercase.dto.CommentDto(" +
            "c.id, c.text, c.author.id, c.author.username, c.caseEntity.id, c.createdAt) " +
            "FROM Comment c WHERE c.caseEntity = :caseEntity " +
            "ORDER BY c.createdAt ASC")
    List<CommentDto> findCommentsByCaseEntity(@Param("caseEntity") CaseEntity caseEntity);
}
