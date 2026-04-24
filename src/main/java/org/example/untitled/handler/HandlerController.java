package org.example.untitled.handler;

import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.service.CaseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Controller for the handler dashboard, accessible to users with roles
 * HANDLER, SUPERVISOR, or ADMIN. Provides functionality for viewing,
 * assigning, and updating the status of support tickets.
 */
@Controller
public class HandlerController {

    private final CaseService caseService;

    public HandlerController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping("/handler")
    @PreAuthorize("hasAnyRole('HANDLER', 'SUPERVISOR', 'ADMIN')")
    public String handlerDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("tickets", caseService.getAllTickets());
        model.addAttribute("statuses", CaseStatus.values());
        model.addAttribute("currentUser", userDetails.getUsername());
        return "handlerpage";
    }


}
