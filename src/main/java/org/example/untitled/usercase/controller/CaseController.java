package org.example.untitled.usercase.controller;

import org.example.untitled.user.User;
import org.example.untitled.usercase.dto.CreateCaseRequest;
import org.example.untitled.usercase.service.CaseService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

public class CaseController {

    CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }


    @GetMapping("/tickets/create")
    public String createTicketForm(Model model) {
        model.addAttribute("ticketForm", new CreateCaseRequest());
        return "create_ticket";
    }

    @PostMapping
    public String processCreateTicket(@ModelAttribute("ticketForm") CreateCaseRequest ticketForm, User user) {
        caseService.saveTicket(ticketForm, user);
        return "redirect:/userpage";
    }

}
