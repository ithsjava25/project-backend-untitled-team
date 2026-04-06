package org.example.untitled.user.service;

import org.example.untitled.user.mapper.UserMapper;
import org.example.untitled.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRep;
    private final UserMapper userMapper;

    public UserService(UserRepository userRep, UserMapper userMapper) {
        this.userRep = userRep;
        this.userMapper = userMapper;
    }
}
