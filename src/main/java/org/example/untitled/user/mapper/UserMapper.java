package org.example.untitled.user.mapper;

import org.example.untitled.user.User;
import org.example.untitled.user.dto.UserDto;

public class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        if (user == null) return null;
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public static User toEntity(UserDto dto) {
        if (dto == null) return null;
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        return user;
    }
}
