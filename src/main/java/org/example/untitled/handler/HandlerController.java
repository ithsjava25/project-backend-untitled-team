package org.example.untitled.handler;

import org.example.untitled.user.Role;
import org.example.untitled.user.service.UserService;
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
import java.util.List;

@Controller
public class HandlerController {

    private final CaseService caseService;
    private final UserService userService;

    public HandlerController(CaseService caseService, UserService userService) {
        this.caseService = caseService;
        this.userService = userService;
    }

    @GetMapping("/handler")
    @PreAuthorize("hasAnyRole('HANDLER', 'SUPERVISOR', 'ADMIN')")
    public String handlerDashboard(Model model,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam(required = false) String filter) {
        var tickets = caseService.getAllTickets();

        if ("active".equals(filter)) {
            tickets = tickets.stream()
                    .filter(t -> t.status() != CaseStatus.CLOSED
                            && t.status() != CaseStatus.SOLVED)
                    .toList();
        } else if ("closed".equals(filter)) {
            tickets = tickets.stream()
                    .filter(t -> t.status() == CaseStatus.CLOSED
                            || t.status() == CaseStatus.SOLVED)
                    .toList();
        }

        String currentUser = userDetails.getUsername();

        tickets = tickets.stream()
                .sorted(Comparator
                        .<CaseEntityDto>comparingInt(t -> {
                            if (t.assignedToUsername() != null && t.assignedToUsername().equals(currentUser)
                                    && t.status() == CaseStatus.IN_PROGRESS) return 0;
                            if (t.assignedToUsername() != null && t.assignedToUsername().equals(currentUser)) return 1;
                            if (t.assignedToUsername() == null && t.status() == CaseStatus.OPEN) return 2;
                            if (t.assignedToUsername() != null) return 3;
                            return 4;
                        }))
                .toList();

        Role currentRole = userDetails.getAuthorities().stream()
                .findFirst()
                .flatMap(a -> Role.fromAuthority(a.getAuthority()))
                .orElse(Role.HANDLER);

        boolean isSupervisor = currentRole == Role.SUPERVISOR;
        boolean isSupervisorOrAdmin = currentRole == Role.SUPERVISOR || currentRole == Role.ADMIN;

        if (isSupervisorOrAdmin) {
            model.addAttribute("handlers", userService.getUsersByRoles(
                    List.of(Role.HANDLER, Role.SUPERVISOR)));
        }

        model.addAttribute("tickets", tickets);
        model.addAttribute("statuses", CaseStatus.values());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUserRole", currentRole.name());
        model.addAttribute("isSupervisor", isSupervisor);
        model.addAttribute("isSupervisorOrAdmin", isSupervisorOrAdmin);
        model.addAttribute("filter", filter);
        return "handlerpage";
    }
}
