package org.example.untitled.usercase.controller;

import jakarta.validation.Valid;
import org.example.untitled.user.Role;
import org.example.untitled.user.service.UserService;
import org.example.untitled.usercase.AuditLog;
import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.UploadedFile;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CommentDto;
import org.example.untitled.usercase.dto.CreateCaseRequest;
import org.example.untitled.usercase.dto.CreateCommentRequest;
import org.example.untitled.usercase.service.AuditLogService;
import org.example.untitled.usercase.service.CaseService;
import org.example.untitled.usercase.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tickets")
public class CaseController {

    private final CaseService caseService;
    private final CommentService commentService;
    private final AuditLogService auditLogService;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(CaseController.class);

    public CaseController(CaseService caseService, CommentService commentService, AuditLogService auditLogService, UserService userService) {
        this.caseService = caseService;
        this.commentService = commentService;
        this.auditLogService = auditLogService;
        this.userService = userService;
    }

    /**
     * Encapsulates permission checks for a given ticket and user so the
     * same logic is not duplicated across multiple handler methods.
     */
    private record TicketPermissions(
            boolean isHandler,
            boolean isOwner,
            boolean isAssigned,
            boolean canClose,
            boolean canComment
    ) {
        static TicketPermissions of(CaseEntityDto ticket, UserDetails userDetails,
                                    boolean isHandler) {
            boolean isOwner = ticket.ownerUsername().equals(userDetails.getUsername());
            boolean isAssigned = userDetails.getUsername().equals(ticket.assignedToUsername());
            boolean isTicketOpen = ticket.status() != CaseStatus.CLOSED
                    && ticket.status() != CaseStatus.SOLVED;
            boolean canClose = isTicketOpen && (isOwner || isAssigned || isHandler);
            boolean canComment = isOwner || isAssigned || isHandler;
            return new TicketPermissions(isHandler, isOwner, isAssigned, canClose, canComment);
        }
    }

    @PostMapping
    public ResponseEntity<CaseEntityDto> createTicket(
            @Valid @RequestBody CreateCaseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(caseService.createTicket(request, userDetails.getUsername()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<CaseEntityDto>> getMyTickets(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(caseService.getMyTickets(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showTicketDetails(
            @PathVariable long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        CaseEntityDto ticket = caseService.getTicketByID(id);
        TicketPermissions perms = TicketPermissions.of(ticket, userDetails, isHandlerOrAbove(userDetails));

        if (!perms.isHandler() && caseService.isNotOwner(ticket, userDetails.getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this ticket");

        List<CommentDto> comments = commentService.getCommentsByTicketId(id);
        List<UploadedFile> files = caseService.getTicketFiles(id);
        List<AuditLog> auditLogs = auditLogService.getLogsForCase(id);
        Map<Long, String> auditUserMap = buildAuditUserMap(auditLogs);

        model.addAttribute("ticket", ticket);
        model.addAttribute("comments", comments);
        model.addAttribute("comment", new CreateCommentRequest());
        model.addAttribute("canClose", perms.canClose());
        model.addAttribute("canComment", perms.canComment());
        model.addAttribute("auditLogs", auditLogs);
        model.addAttribute("auditUserMap", auditUserMap);
        model.addAttribute("files", files);
        return "ticket";
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaseEntityDto> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody CreateCaseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(caseService.updateTicket(id, request, userDetails.getUsername()));
    }

    @GetMapping
    @ResponseBody
    @PreAuthorize("hasAnyRole('HANDLER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<List<CaseEntityDto>> getAllTickets() {
        return ResponseEntity.ok(caseService.getAllTickets());
    }

    @PutMapping("/{id}/status")
    @ResponseBody
    @PreAuthorize("hasAnyRole('HANDLER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<CaseEntityDto> updateStatus(
            @PathVariable Long id,
            @RequestParam CaseStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(caseService.updateStatus(id, status, userDetails.getUsername()));
    }

    @PutMapping("/{id}/assign")
    @ResponseBody
    @PreAuthorize("hasAnyRole('HANDLER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<CaseEntityDto> assignToSelf(
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(caseService.assignTicket(id, userDetails.getUsername()));
    }

    @GetMapping("/{id}/close")
    @PreAuthorize("isAuthenticated()")
    public String closeTicket(
            Model model,
            @PathVariable long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        CaseEntityDto ticket = caseService.getTicketByID(id);
        TicketPermissions perms = TicketPermissions.of(ticket, userDetails, isHandlerOrAbove(userDetails));

        if (!perms.isHandler() && !perms.isAssigned() && caseService.isNotOwner(ticket, userDetails.getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to close this ticket");

        model.addAttribute("ticket", ticket);
        model.addAttribute("comment", new CreateCommentRequest());
        return "close_ticket";
    }

    @PostMapping("/{id}/close")
    public String processCloseTicket(
            @PathVariable long id,
            @ModelAttribute("comment") @Valid CreateCommentRequest comment,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        CaseEntityDto ticket = caseService.getTicketByID(id);
        TicketPermissions perms = TicketPermissions.of(ticket, userDetails, isHandlerOrAbove(userDetails));

        if (!perms.isHandler() && !perms.isAssigned() && caseService.isNotOwner(ticket, userDetails.getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to close this ticket");

        if (bindingResult.hasErrors()) {
            model.addAttribute("ticket", ticket);
            return "close_ticket";
        }
        comment.setCaseId(id);
        try {
            caseService.closeTicket(ticket, comment, userDetails.getUsername());
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("text", "error.createCommentRequest", e.getMessage());
            model.addAttribute("ticket", ticket);
            return "close_ticket";
        }

        return perms.isHandler() ? "redirect:/handler" : "redirect:/user";
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('HANDLER', 'SUPERVISOR')")
    public String assignTicketForm(
            @PathVariable Long id,
            @RequestParam(required = false) String username,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            String assignTo;

            boolean isSupervisor = userDetails.getAuthorities().stream()
                    .map(a -> Role.fromAuthority(a.getAuthority()))
                    .flatMap(Optional::stream)
                    .anyMatch(r -> r == Role.SUPERVISOR);

            if (username != null && !username.isBlank() && !username.equals(userDetails.getUsername())) {
                if (!isSupervisor) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to assign to another user");
                }
                assignTo = username;
            } else {
                assignTo = userDetails.getUsername();
            }

            caseService.assignTicket(id, assignTo);
            redirectAttributes.addFlashAttribute("success", "Ticket assigned to " + assignTo);
        } catch (ResponseStatusException e) {
            log.warn("Assign failed for ticket {}.", id, e);
            redirectAttributes.addFlashAttribute("error",
                    e.getReason() != null ? e.getReason() : "Could not assign ticket");
        } catch (RuntimeException e) {
            log.error("Unexpected error assigning ticket {}.", id, e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred");
        }
        return "redirect:/handler";
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HANDLER', 'SUPERVISOR', 'ADMIN')")
    public String updateStatusForm(
            @PathVariable Long id,
            @RequestParam String statusParam,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            CaseStatus status = CaseStatus.valueOf(statusParam);
            caseService.updateStatus(id, status, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success", "Status updated successfully");
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status value '{}' for ticket {}.", statusParam, id, e);
            redirectAttributes.addFlashAttribute("error", "Invalid status value");
        } catch (ResponseStatusException e) {
            log.warn("Status update failed for ticket {}.", id, e);
            redirectAttributes.addFlashAttribute("error",
                    e.getReason() != null ? e.getReason() : "Could not update status");
        } catch (RuntimeException e) {
            log.error("Unexpected error updating status for ticket {}.", id, e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred");
        }
        return "redirect:/handler";
    }

    @PostMapping("/{id}/comments")
    @PreAuthorize("isAuthenticated()")
    public String addComment(
            @PathVariable long id,
            @Valid @ModelAttribute("comment") CreateCommentRequest comment,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        CaseEntityDto ticket = caseService.getTicketByID(id);
        TicketPermissions perms = TicketPermissions.of(ticket, userDetails, isHandlerOrAbove(userDetails));

        if (!perms.canComment()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to comment on this ticket");
        }

        if (bindingResult.hasErrors()) {
            List<AuditLog> auditLogs = auditLogService.getLogsForCase(id);
            Map<Long, String> auditUserMap = buildAuditUserMap(auditLogs);
            model.addAttribute("ticket", ticket);
            model.addAttribute("comments", commentService.getCommentsByTicketId(id));
            model.addAttribute("canClose", perms.canClose());
            model.addAttribute("canComment", perms.canComment());
            model.addAttribute("auditLogs", auditLogs);
            model.addAttribute("auditUserMap", auditUserMap);
            return "ticket";
        }

        try {
            comment.setCaseId(id);
            commentService.createComment(comment, ticket, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success", "Comment added successfully");
        } catch (IllegalArgumentException e) {
            log.warn("Comment creation failed for ticket {} by user {}: {}", id, userDetails.getUsername(), e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (DataAccessException e) {
            log.error("Data access error adding comment to ticket {} by user {}.", id, userDetails.getUsername(), e);
            redirectAttributes.addFlashAttribute("error", "Could not save comment, please try again");
        } catch (ResponseStatusException e) {
            log.warn("Comment creation rejected for ticket {} by user {}: {}", id, userDetails.getUsername(), e.getReason(), e);
            redirectAttributes.addFlashAttribute("error",
                    e.getReason() != null ? e.getReason() : "Could not add comment");
        } catch (Exception e) {
            log.error("Unexpected error adding comment to ticket {} by user {}.", id, userDetails.getUsername(), e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred");
        }
        return "redirect:/tickets/" + id;
    }

    // --- Private helpers ---

    private boolean isHandlerOrAbove(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(a -> Role.fromAuthority(a.getAuthority()))
                .flatMap(Optional::stream)
                .anyMatch(r -> r == Role.HANDLER || r == Role.SUPERVISOR || r == Role.ADMIN);
    }

    private Map<Long, String> buildAuditUserMap(List<AuditLog> auditLogs) {
        Set<Long> userIds = auditLogs.stream()
                .map(AuditLog::getUserId)
                .filter(uid -> uid != null)
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userService.findAllByIds(userIds);
    }
}
