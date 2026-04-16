package org.example.untitled.usercase.controller;

import jakarta.validation.Valid;
import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CreateCaseRequest;
import org.example.untitled.usercase.dto.CreateCommentRequest;
import org.example.untitled.usercase.service.CaseService;
import org.example.untitled.usercase.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tickets")
public class CaseController {

    private final CaseService caseService;
    private final CommentService commentService;

    public CaseController(CaseService caseService, CommentService commentService) {
        this.caseService = caseService;
        this.commentService = commentService;
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

    @PutMapping("/{id}")
    public ResponseEntity<CaseEntityDto> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody CreateCaseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(caseService.updateTicket(id, request, userDetails.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HANDLER', 'ADMIN')")
    public ResponseEntity<List<CaseEntityDto>> getAllTickets() {
        return ResponseEntity.ok(caseService.getAllTickets());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HANDLER', 'ADMIN')")
    public ResponseEntity<CaseEntityDto> updateStatus(
            @PathVariable Long id, @RequestParam CaseStatus status) {
        return ResponseEntity.ok(caseService.updateStatus(id, status));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('HANDLER', 'ADMIN')")
    public ResponseEntity<CaseEntityDto> assignToSelf(
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(caseService.assignTicket(id, userDetails.getUsername()));
    }

    @GetMapping("/{id}/close")
    public String closeTicket(
            Model model, @PathVariable long id) {
        model.addAttribute("ticket", caseService.getTicketByID(id));
        model.addAttribute("comment", new CreateCaseRequest());
        return "close_ticket";
    }

    @PostMapping("/close")
    public String processCloseTicket(
            @ModelAttribute("comment") @Valid CreateCommentRequest comment,
            @ModelAttribute("ticket") CaseEntityDto ticket,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "close_ticket";
        }
        try {
            caseService.updateStatus(ticket.id(), CaseStatus.CLOSED);
            commentService.createComment(comment, ticket);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("text", "error.createCommentRequest", e.getMessage());
            return "close_ticket";
        }

        return "redirect:/user";
    }
}
