package org.example.untitled.user.service;

import org.example.untitled.auth.dto.RegisterRequest;
import org.example.untitled.exception.EmailAlreadyExistsException;
import org.example.untitled.exception.UserAlreadyExistsException;
import org.example.untitled.user.Role;
import org.example.untitled.user.User;
import org.example.untitled.user.dto.UserDto;
import org.example.untitled.user.mapper.UserMapper;
import org.example.untitled.user.repository.UserRepository;
import org.example.untitled.usercase.AuditAction;
import org.example.untitled.usercase.service.AuditLogService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRep;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public UserService(UserRepository userRep, PasswordEncoder passwordEncoder, AuditLogService auditLogService) {
        this.userRep = userRep;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    public List<UserDto> getAllUsers() {
        return userRep.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public UserDto updateRole(Long id, Role role) {
        User user = userRep.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
        user.setRole(role);
        UserDto saved = UserMapper.toDto(userRep.save(user));
        auditLogService.log(AuditAction.USER_ROLE_CHANGED, id, null);
        return saved;
    }

    public void register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        try {
            userRep.save(user);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (message.contains("email")) {
                throw new EmailAlreadyExistsException(request.getEmail(), e);
            } else if (message.contains("username")) {
                throw new UserAlreadyExistsException(request.getUsername(), e);
            } else {
                throw e;
            }
        }
    }
}
