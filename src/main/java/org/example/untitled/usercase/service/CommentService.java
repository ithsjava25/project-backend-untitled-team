package org.example.untitled.usercase.service;

import org.example.untitled.user.repository.UserRepository;
import org.example.untitled.usercase.dto.CommentDto;
import org.example.untitled.usercase.AuditAction;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CreateCommentRequest;
import org.example.untitled.usercase.mapper.CommentMapper;
import org.example.untitled.usercase.repository.CaseRepository;
import org.example.untitled.usercase.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CaseRepository caseRepository;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, CaseRepository caseRepository,
                          AuditLogService auditLogService, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.caseRepository = caseRepository;
        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createComment(CreateCommentRequest comment, CaseEntityDto ticket, String username) {
        if (comment == null)
            throw new IllegalArgumentException("CreateCommentRequest can't be null");
        if (ticket == null)
            throw new IllegalArgumentException("CaseEntityDTO can't be null");
        var caseEntity = caseRepository.findById(ticket.id())
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticket.id()));
        var author = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        var entity = CommentMapper.toEntity(comment);
        entity.setAuthor(author);
        entity.setCaseEntity(caseEntity);
        commentRepository.save(entity);
        auditLogService.log(AuditAction.COMMENT_ADDED, author.getId(), caseEntity.getId());
    }

    public List<CommentDto> getCommentsByTicketId(Long id) {
        return commentRepository.findCommentsByCaseEntity(
                caseRepository.findCaseEntityById(id));
    }
}
