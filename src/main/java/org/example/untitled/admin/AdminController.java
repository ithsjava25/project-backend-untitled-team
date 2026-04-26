package org.example.untitled.admin;

import java.util.List;
import org.example.untitled.user.Role;
import org.example.untitled.user.dto.UserDto;
import org.example.untitled.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", Role.values());
        return "adminpage";
    }

    @PutMapping("/users/{id}/role")
    public String updateRole(@PathVariable Long id,
                             @RequestParam Role role,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.updateRole(id, role);
            redirectAttributes.addFlashAttribute("success", "Role updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update role");
        }
        return "redirect:/admin";
    }
}
