package org.example.untitled.usercase.repository;

import org.example.untitled.usercase.CaseEntity;
import org.example.untitled.usercase.Comment;
import org.example.untitled.usercase.dto.CommentDto;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface CommentRepository extends ListCrudRepository<Comment, Long> {
    List<CommentDto> findCommentsByCaseEntity(CaseEntity caseEntity);
}
