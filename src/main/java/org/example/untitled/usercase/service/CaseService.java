package org.example.untitled.usercase.service;

import org.example.untitled.s3.S3Service;
import java.util.List;
import org.example.untitled.user.Role;
import org.example.untitled.user.User;
import org.example.untitled.user.repository.UserRepository;
import org.example.untitled.usercase.AuditAction;
import org.example.untitled.usercase.CaseEntity;
import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CreateCaseRequest;
import org.example.untitled.usercase.dto.CreateCommentRequest;
import org.example.untitled.usercase.mapper.CaseMapper;
import org.example.untitled.usercase.repository.CaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service for managing support tickets (cases) in the system.
 * Handles creation, updates, assignment, and status changes of tickets,
 * and logs all significant actions via {@link AuditLogService}.
 */
@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final CommentService commentService;
    private final S3Service s3Service;

    public CaseService(
            CaseRepository caseRepository,
            UserRepository userRepository,
            CommentService commentService,
            S3Service s3Service,
            AuditLogService auditLogService) {
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
        this.commentService = commentService;
        this.auditLogService = auditLogService;
        this.s3Service = s3Service;
    }

    @Transactional
    public CaseEntityDto createTicket(CreateCaseRequest request, String username, String fileName) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (caseRepository.existsByTitleAndOwner(request.getTitle(), owner)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "A ticket for this issue is already in the system");
        }
        CaseEntity caseEntity = CaseMapper.toEntity(request);
        caseEntity.setOwner(owner);
        caseEntity.setStatus(CaseStatus.OPEN);
        if(fileName != null && !fileName.isBlank()){
            caseEntity.getFiles().addAll(s3Service.createFile(caseEntity, fileName));
        }
        CaseEntity saved = caseRepository.save(caseEntity);
        auditLogService.log(AuditAction.CASE_CREATED, owner.getId(), saved.getId());
        return CaseMapper.toDto(saved);
    }

    public List<CaseEntityDto> getMyTickets(String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return caseRepository.findByOwner(owner).stream()
                .map(CaseMapper::toDto)
                .toList();
    }

    @Transactional
    public CaseEntityDto updateTicket(Long id, CreateCaseRequest request, String username, String fileName) {
        CaseEntity caseEntity = caseRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + id));
        if (!caseEntity.getOwner().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this ticket");
        }
        if (caseRepository.existsByTitleAndOwnerAndIdNot(request.getTitle(), caseEntity.getOwner(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A ticket with this title already exists");
        }
        caseEntity.setTitle(request.getTitle());
        caseEntity.setDescription(request.getDescription());
        if(fileName != null && !fileName.isBlank())
            caseEntity.getFiles().addAll(s3Service.createFile(caseEntity, fileName));
        CaseEntity saved = caseRepository.save(caseEntity);
        auditLogService.log(AuditAction.CASE_UPDATED, caseEntity.getOwner().getId(), saved.getId());
        return CaseMapper.toDto(saved);
    }

    @Transactional
    public void closeTicket(CaseEntityDto ticket, CreateCommentRequest comment) {
        if (comment == null)
            throw new IllegalArgumentException("Comment Cant be null");
        if (ticket == null)
            throw new IllegalArgumentException("Ticket Cant be null");
        updateStatus(ticket.id(), CaseStatus.CLOSED, ticket.ownerUsername());
        commentService.createComment(comment, ticket);
    }

    public List<CaseEntityDto> getAllTickets() {
        return caseRepository.findAll().stream()
                .map(CaseMapper::toDto)
                .toList();
    }

    public List<CaseEntityDto> getTicketsAssignedTo(User user) {
        return caseRepository.findByAssignedTo(user).stream()
                .map(CaseMapper::toDto)
                .toList();
    }

    @Transactional
    public CaseEntityDto updateStatus(Long id, CaseStatus newStatus, String username) {
        User actor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        CaseEntity caseEntity = caseRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + id));
        caseEntity.setStatus(newStatus);
        CaseEntity saved = caseRepository.save(caseEntity);
        auditLogService.log(AuditAction.CASE_STATUS_CHANGED, actor.getId(), saved.getId());
        return CaseMapper.toDto(saved);
    }

    @Transactional
    public CaseEntityDto assignTicket(Long id, String username) {
        CaseEntity caseEntity = caseRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + id));
        User handler = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (handler.getRole() != Role.HANDLER && handler.getRole() != Role.SUPERVISOR && handler.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a handler, supervisor or admin");
        }
        caseEntity.setAssignedTo(handler);
        CaseEntity saved = caseRepository.save(caseEntity);
        auditLogService.log(AuditAction.CASE_ASSIGNED, handler.getId(), saved.getId());
        return CaseMapper.toDto(saved);
    }

    public CaseEntityDto getTicketByID(long id) {
        CaseEntity caseEntity = caseRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + id));
        return CaseMapper.toDto(caseEntity);
    }
    public User findOwnerById(long id) {
        return caseRepository.findOwnerById(id);
    }

    public boolean isNotOwner(CaseEntityDto ticket, String username) {
        return !ticket.ownerUsername().equals(username);
    }
}
