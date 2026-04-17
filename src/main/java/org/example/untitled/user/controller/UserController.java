package org.example.untitled.user.controller;

import jakarta.validation.Valid;
import org.example.untitled.auth.dto.RegisterRequest;
import org.example.untitled.exception.EmailAlreadyExistsException;
import org.example.untitled.exception.UserAlreadyExistsException;
import org.example.untitled.user.service.UserService;
import org.example.untitled.usercase.service.CaseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final CaseService caseService;
    private final UserService userService;

    public UserController(UserService service) {
        this.userService = service;
    }

    @GetMapping("/user")
    public String userLanding() {
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