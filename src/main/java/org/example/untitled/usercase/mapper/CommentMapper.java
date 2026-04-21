package org.example.untitled.usercase.mapper;

import org.example.untitled.usercase.Comment;
import org.example.untitled.usercase.dto.CreateCommentRequest;

public class CommentMapper {

    private CommentMapper() {
    }

    public static Comment toEntity(CreateCommentRequest dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        return comment;
    }
}
