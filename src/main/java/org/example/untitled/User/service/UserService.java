package org.example.untitled.User.service;

import org.example.untitled.User.Mapper.UserMapper;
import org.example.untitled.User.Repository.UserRepository;
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