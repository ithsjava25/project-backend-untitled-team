package org.example.untitled.auth;

import jakarta.validation.Valid;
import org.example.untitled.auth.dto.LoginRequest;
import org.example.untitled.auth.dto.RegisterRequest;
import org.example.untitled.exception.UserAlreadyExistsException;
import org.example.untitled.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(
            UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginForm", new LoginRequest());
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerForm", new RegisterRequest());
        return "register_user";
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Void> register(@Valid @ModelAttribute RegisterRequest registerForm) {
        try {
            userService.register(registerForm);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}