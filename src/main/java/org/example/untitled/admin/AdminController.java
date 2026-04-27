package org.example.untitled.admin;

import org.example.untitled.user.Role;
import org.example.untitled.user.User;
import org.example.untitled.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for the admin dashboard, accessible only to users with the ADMIN role.
 * Provides functionality for viewing all users and managing their roles.
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
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
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByUsername(userDetails.getUsername());

            if (currentUser.getId().equals(id)) {
                redirectAttributes.addFlashAttribute("error", "You cannot change your own role");
                return "redirect:/admin";
            }

            if (role != Role.ADMIN && userService.countAdmins() <= 1) {
                User target = userService.findById(id);
                if (target.getRole() == Role.ADMIN) {
                    redirectAttributes.addFlashAttribute("error", "Cannot demote the last admin");
                    return "redirect:/admin";
                }
            }

            userService.updateRole(id, role);
            redirectAttributes.addFlashAttribute("success", "Role updated successfully");
        } catch (ResponseStatusException e) {
            log.warn("Role update failed for user {}.", id, e);
            redirectAttributes.addFlashAttribute("error",
                    e.getReason() != null ? e.getReason() : "Could not update role");
        } catch (Exception e) {
            log.error("Unexpected error updating role for user {}.", id, e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred");
        }
        return "redirect:/admin";
    }
}
