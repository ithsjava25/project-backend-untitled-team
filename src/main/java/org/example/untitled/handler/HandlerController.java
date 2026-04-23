package org.example.untitled.handler;

import org.example.untitled.user.User;
import org.example.untitled.user.repository.UserRepository;
import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.service.CaseService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;


/**
 * Controller for the handler dashboard, accessible to users with roles
 * HANDLER, SUPERVISOR, or ADMIN. Provides functionality for viewing,
 * assigning, and updating the status of support tickets.
 */
@Controller
public class HandlerController {

    private final CaseService caseService;
    private final UserRepository userRepository;

    public HandlerController(CaseService caseService, UserRepository userRepository) {
        this.caseService = caseService;
        this.userRepository = userRepository;
    }

    @GetMapping("/handler")
    @PreAuthorize("hasAnyRole('HANDLER', 'SUPERVISOR', 'ADMIN')")
    public String handlerDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("tickets", caseService.getAllTickets());
        model.addAttribute("statuses", CaseStatus.values());
        return "handlerpage";
    }

    @PostMapping("/handler/tickets/{id}/assign")
    @PreAuthorize("hasAnyRole('HANDLER', 'SUPERVISOR', 'ADMIN')")
    public String assignTicket(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        caseService.assignTicket(id, userDetails.getUsername());
        return "redirect:/handler";
    }

    @PostMapping("/handler/tickets/{id}/status")
    @PreAuthorize("hasAnyRole('HANDLER', 'SUPERVISOR', 'ADMIN')")
    public String updateStatus(@PathVariable Long id, @RequestParam CaseStatus status,
                               @AuthenticationPrincipal UserDetails userDetails) {
        caseService.updateStatus(id, status, userDetails.getUsername());
        return "redirect:/handler";
    }
}
