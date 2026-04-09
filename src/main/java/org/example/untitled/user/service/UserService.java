package org.example.untitled.user.service;

import org.example.untitled.auth.dto.RegisterRequest;
import org.example.untitled.user.Role;
import org.example.untitled.user.User;
import org.example.untitled.user.mapper.UserMapper;
import org.example.untitled.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRep;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRep, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRep = userRep;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean usernameExists(String username) {
        return userRep.findByUsername(username).isPresent();
    }

    public User register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        return userRep.save(user);
    }
}
