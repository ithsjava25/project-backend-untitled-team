package org.example.untitled.user.service;

import org.example.untitled.user.mapper.UserMapper;
import org.example.untitled.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    UserRepository userRep;
    UserMapper userMapper;

    public UserService(UserRepository userRep){
        this.userRep = userRep;
        userMapper = new UserMapper();
    }
}
