package org.example.untitled.usercase.service;

import org.example.untitled.usercase.AuditAction;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CreateCommentRequest;
import org.example.untitled.usercase.mapper.CommentMapper;
import org.example.untitled.usercase.repository.CaseRepository;
import org.example.untitled.usercase.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CaseRepository caseRepository;
    private final AuditLogService auditLogService;


    public CommentService(CommentRepository commentRepository, CaseRepository caseRepository, AuditLogService auditLogService) {
        this.commentRepository = commentRepository;
        this.caseRepository = caseRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public void createComment(CreateCommentRequest comment, CaseEntityDto ticket) {
        if (comment == null)
            throw new IllegalArgumentException("CreateCommentRequest can't be null");
        if (ticket == null)
            throw new IllegalArgumentException("CaseEntityDTO can't be null");
        var caseEntity = caseRepository.findById(ticket.id())
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticket.id()));
        var entity = CommentMapper.toEntity(comment);
        entity.setAuthor(caseEntity.getOwner());
        entity.setCaseEntity(caseEntity);
        commentRepository.save(entity);
        auditLogService.log(AuditAction.COMMENT_ADDED, entity.getAuthor().getId(), caseEntity.getId()); // lägg till
    }
}
