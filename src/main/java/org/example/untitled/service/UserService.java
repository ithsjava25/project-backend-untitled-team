package org.example.untitled.service;

import org.example.untitled.Mapper.UserMapper;
import org.example.untitled.Repository.UserRepository;
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