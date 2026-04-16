package org.example.untitled.usercase.controller;

import jakarta.validation.Valid;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CreateCaseRequest;
import org.example.untitled.usercase.service.CaseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/tickets")
public class CaseViewController {

    private final CaseService caseService;

    public CaseViewController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping("/create")
    public String createTicketForm(Model model) {
        model.addAttribute("ticketForm", new CreateCaseRequest());
        return "create_ticket";
    }

    @PostMapping("/create")
    public String processCreateTicket(
            @Valid @ModelAttribute("ticketForm") CreateCaseRequest ticketForm,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            return "create_ticket";
        }
        try {
            caseService.createTicket(ticketForm, userDetails.getUsername());
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                bindingResult.rejectValue("title", "error.ticketForm", e.getReason());
                return "create_ticket";
            }
            throw e;
        }
        return "redirect:/user";
    }

    @GetMapping("/{id}/edit")
    public String editTicketForm(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {
        CaseEntityDto ticket = caseService.getMyTickets(userDetails.getUsername()).stream()
                .filter(t -> t.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        CreateCaseRequest form = new CreateCaseRequest();
        form.setTitle(ticket.title());
        form.setDescription(ticket.description());
        model.addAttribute("ticketForm", form);
        model.addAttribute("ticketId", id);
        return "edit_ticket";
    }

    @PostMapping("/{id}/edit")
    public String processEditTicket(
            @PathVariable Long id,
            @Valid @ModelAttribute("ticketForm") CreateCaseRequest ticketForm,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            return "edit_ticket";
        }
        try {
            caseService.updateTicket(id, ticketForm, userDetails.getUsername());
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                bindingResult.rejectValue("title", "error.ticketForm", e.getReason());
                return "edit_ticket";
            }
            throw e;
        }
        return "redirect:/user";
    }
}