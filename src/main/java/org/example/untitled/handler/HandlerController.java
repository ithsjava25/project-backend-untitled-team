package org.example.untitled.handler;

import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.service.CaseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
        return "handlerpage";
    }
}
