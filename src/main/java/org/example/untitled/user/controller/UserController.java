package org.example.untitled.user.controller;

import org.example.untitled.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    UserService userService;

    private UserController(UserService service){
        this.userService = service;
    }

    @GetMapping("/user")
    public String userLanding(){
        return "userpage";
    }
}
