package org.example.untitled.usercase.controller;

import jakarta.validation.Valid;
import org.example.untitled.user.User;
import org.example.untitled.user.repository.UserRepository;
import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CreateCaseRequest;
import org.example.untitled.usercase.dto.CreateCommentRequest;
import org.example.untitled.usercase.service.CaseService;
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

import java.util.List;

@Controller
@RequestMapping("/tickets")
public class CaseController {

    private final CaseService caseService;
    private final UserRepository userRepository;

    public CaseController(CaseService caseService, UserRepository userRepository) {
        this.caseService = caseService;
        this.userRepository = userRepository;
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
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return ResponseEntity.ok(caseService.updateStatus(id, status, user.getId()));
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
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CaseEntityDto ticket = caseService.getTicketByID(id);
        if (caseService.isNotOwner(ticket, userDetails.getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this ticket");
        model.addAttribute("ticket", caseService.getTicketByID(id));
        model.addAttribute("comment", new CreateCommentRequest());
        return "close_ticket";
    }

    @PostMapping("/{id}/close")
    public String processCloseTicket(
            @PathVariable long id,
            @ModelAttribute("comment") @Valid CreateCommentRequest comment,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        CaseEntityDto ticket = caseService.getTicketByID(id);
        if (caseService.isNotOwner(ticket, userDetails.getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this ticket");
        if (bindingResult.hasErrors()) {
            model.addAttribute("ticket", ticket);
            return "close_ticket";
        }
        comment.setCaseId(id);
        try {
            caseService.closeTicket(ticket, comment);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("text", "error.createCommentRequest", e.getMessage());
            model.addAttribute("ticket", ticket);
            return "close_ticket";
        }

        return "redirect:/user";
    }
}
