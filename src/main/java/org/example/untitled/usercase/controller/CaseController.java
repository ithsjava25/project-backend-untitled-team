package org.example.untitled.usercase.controller;

import org.example.untitled.user.User;
import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CreateCaseRequest;
import org.example.untitled.usercase.service.CaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tickets")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }


    @GetMapping("/tickets/create")
    public String createTicketForm(Model model) {
        model.addAttribute("ticketForm", new CreateCaseRequest());
        return "create_ticket";
    }

    @PostMapping("/tickets/create")
    public String processCreateTicket(@ModelAttribute("ticketForm") CreateCaseRequest ticketForm, User user) {
        caseService.saveTicket(ticketForm, user);
        return "redirect:/userpage";
    }

    @GetMapping
    @ResponseBody
    @PreAuthorize("hasAnyRole('HANDLER', 'ADMIN')")
    public ResponseEntity<List<CaseEntityDto>> getAllTickets() {
        return ResponseEntity.ok(caseService.getAllTickets());
    }

    @PutMapping("/{id}/status")
    @ResponseBody
    @PreAuthorize("hasAnyRole('HANDLER', 'ADMIN')")
    public ResponseEntity<CaseEntityDto> updateStatus(
            @PathVariable Long id, @RequestParam CaseStatus status) {
        return ResponseEntity.ok(caseService.updateStatus(id, status));
    }

    @PutMapping("/{id}/assign")
    @ResponseBody
    @PreAuthorize("hasAnyRole('HANDLER', 'ADMIN')")
    public ResponseEntity<CaseEntityDto> assignToSelf(
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(caseService.assignTicket(id, userDetails.getUsername()));
    }
}
