package org.example.untitled.usercase.service;

import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CreateCommentRequest;
import org.example.untitled.usercase.mapper.CommentMapper;
import org.example.untitled.usercase.repository.CaseRepository;
import org.example.untitled.usercase.repository.CommentRepository;

public class CommentService {

    private CommentRepository commentRepository;
    private CaseRepository caseRepository;


    public CommentService(CommentRepository commentRepository, CaseRepository caseRepository) {
        this.commentRepository = commentRepository;
        this.caseRepository = caseRepository;
    }

    public void createComment(CreateCommentRequest comment, CaseEntityDto ticket) {
        if (comment == null)
            throw new IllegalArgumentException("CreateCommentRequest can't be null");
        if (ticket == null)
            throw new IllegalArgumentException("CaseEntityDTO can't be null");
        var entity = CommentMapper.toEntity(comment);
        entity.setAuthor(caseRepository.findOwnerById(ticket.id()));
        entity.setCaseEntity(caseRepository.findCaseEntityById(ticket.id()));
        commentRepository.save(entity);
    }
}
