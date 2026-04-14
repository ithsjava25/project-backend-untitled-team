package org.example.untitled.user.controller;

import org.example.untitled.auth.dto.LoginRequest;
import org.example.untitled.auth.dto.RegisterRequest;
import org.example.untitled.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService service){
        this.userService = service;
    }

    @GetMapping("/user")
    public String userLanding(){
        return "userpage";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerForm", new RegisterRequest());
        return "register_user";
    }

    @PostMapping("/register")
    public String processRegisterUser(@ModelAttribute("registerForm") RegisterRequest registerForm) {
        userService.register(registerForm);
        return "redirect:/user";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginForm", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String processLoginRequest(@ModelAttribute("loginForm") LoginRequest loginForm) {
        //login user
        return "redirect:/user";
    }
}
