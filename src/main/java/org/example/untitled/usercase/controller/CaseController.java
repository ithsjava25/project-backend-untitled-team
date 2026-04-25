package org.example.untitled.usercase.controller;

import jakarta.validation.Valid;
import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CommentDto;
import org.example.untitled.usercase.dto.CreateCaseRequest;
import org.example.untitled.usercase.dto.CreateCommentRequest;
import org.example.untitled.usercase.service.CaseService;
import org.example.untitled.usercase.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Controller
@RequestMapping("/tickets")
public class CaseController {

    private final CaseService caseService;
    private final CommentService commentService;
    private static final Logger log = LoggerFactory.getLogger(CaseController.class);


    public CaseController(CaseService caseService, CommentService commentService) {
        this.caseService = caseService;
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CaseEntityDto> createTicket(
            @Valid @RequestBody CreateCaseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(caseService.createTicket(request, userDetails.getUsername(), request.getFileName()));
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

        boolean isHandler = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HANDLER")
                        || a.getAuthority().equals("ROLE_SUPERVISOR")
                        || a.getAuthority().equals("ROLE_ADMIN"));

        if (!isHandler && caseService.isNotOwner(ticket, userDetails.getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this ticket");

        List<CommentDto> comments = commentService.getCommentsByTicketId(id);
        model.addAttribute("ticket", ticket);
        model.addAttribute("comments", comments);
        return "ticket";
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaseEntityDto> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody CreateCaseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(caseService.updateTicket(id, request, userDetails.getUsername(), request.getFileName()));
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

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('HANDLER', 'SUPERVISOR', 'ADMIN')")
    public String assignTicketForm(
            @PathVariable Long id,
            @RequestParam(required = false) String username,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            String assignTo = (username != null && !username.isBlank())
                    ? username
                    : userDetails.getUsername();
            caseService.assignTicket(id, assignTo);
            redirectAttributes.addFlashAttribute("success", "Ticket assigned successfully");
        } catch (ResponseStatusException e) {
            log.warn("Assign failed for ticket {}.", id, e);
            redirectAttributes.addFlashAttribute("error", e.getReason());
        } catch (Exception e) {
            log.error("Unexpected error assigning ticket {}.", id, e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred");
        }
        return "redirect:/handler";
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HANDLER', 'SUPERVISOR', 'ADMIN')")
    public String updateStatusForm(
            @PathVariable Long id,
            @RequestParam CaseStatus status,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            caseService.updateStatus(id, status, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success", "Status updated successfully");
        } catch (ResponseStatusException e) {
            log.warn("Status update failed for ticket {}.", id, e);
            redirectAttributes.addFlashAttribute("error", e.getReason());
        } catch (Exception e) {
            log.error("Unexpected error updating status for ticket {}.", id, e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred");
        }
        return "redirect:/handler";
    }
}
