package org.example.untitled.usercase.repository;

import org.example.untitled.usercase.Comment;
import org.springframework.data.repository.ListCrudRepository;

public interface CommentRepository extends ListCrudRepository<Comment, Long> {

}
