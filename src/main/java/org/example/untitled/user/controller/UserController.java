package org.example.untitled.user.controller;

import jakarta.validation.Valid;
import org.example.untitled.auth.dto.RegisterRequest;
import org.example.untitled.exception.EmailAlreadyExistsException;
import org.example.untitled.exception.UserAlreadyExistsException;
import org.example.untitled.user.service.UserService;
import org.example.untitled.usercase.service.CaseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserService userService;
    private final CaseService caseService;

    public UserController(UserService service, CaseService caseService) {
        this.userService = service;
        this.caseService = caseService;
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userLanding(Model model,
                              @AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam(required = false) String filter) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        var tickets = caseService.getMyTickets(userDetails.getUsername());

        if ("open".equals(filter)) {
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

        model.addAttribute("tickets", tickets);
        model.addAttribute("filter", filter);
        return "userpage";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerForm", new RegisterRequest());
        return "register_user";
    }

    @PostMapping("/register")
    public String processRegister(
            @Valid @ModelAttribute("registerForm") RegisterRequest registerForm,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register_user";
        }
        try {
            userService.register(registerForm);
        } catch (UserAlreadyExistsException e) {
            bindingResult.rejectValue("username", "error.registerForm", e.getMessage());
            return "register_user";
        } catch (EmailAlreadyExistsException e) {
            bindingResult.rejectValue("email", "error.registerForm", e.getMessage());
            return "register_user";
        }
        return "redirect:/login";
    }
}
