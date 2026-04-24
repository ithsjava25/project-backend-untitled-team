package org.example.untitled.handler;

import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.service.CaseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;


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
    public String handlerDashboard(Model model,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam(required = false) String filter) {
        var tickets = caseService.getAllTickets();

        if ("active".equals(filter)) {
            tickets = tickets.stream()
                    .filter(t -> !t.status().name().equals("CLOSED")
                            && !t.status().name().equals("SOLVED"))
                    .toList();
        } else if ("closed".equals(filter)) {
            tickets = tickets.stream()
                    .filter(t -> t.status().name().equals("CLOSED")
                            || t.status().name().equals("SOLVED"))
                    .toList();
        }

        String currentUser = userDetails.getUsername();

        tickets = tickets.stream()
                .sorted(Comparator
                        .<CaseEntityDto>comparingInt(t -> {
                            if (t.assignedToUsername() != null && t.assignedToUsername().equals(currentUser)
                                    && t.status() == CaseStatus.IN_PROGRESS) return 0;
                            if (t.assignedToUsername() != null && t.assignedToUsername().equals(currentUser)) return 1;
                            if (t.assignedToUsername() != null) return 2;
                            if (t.status() == CaseStatus.OPEN) return 3;
                            return 4;
                        }))
                .toList();

        model.addAttribute("tickets", tickets);
        model.addAttribute("statuses", CaseStatus.values());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("filter", filter);
        return "handlerpage";
    }


}
