package org.example.untitled.user.controller;

import org.example.untitled.usercase.service.CaseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private final CaseService caseService;

    public UserController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping("/user")
    public String userLanding(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("tickets", caseService.getMyTickets(userDetails.getUsername()));
        return "userpage";
    }
}