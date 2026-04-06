package org.example.untitled.user.mapper;

import org.example.untitled.user.Role;
import org.example.untitled.user.User;
import org.example.untitled.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void toEntity_shouldNotMapId() {
        UserDto dto = new UserDto();
        dto.setId(99L);
        dto.setUsername("alice");

        User user = userMapper.toEntity(dto);

        assertNull(user.getId());
    }

    @Test
    void toEntity_shouldNotMapRole() {
        UserDto dto = new UserDto();
        dto.setRole(Role.ADMIN);
        dto.setUsername("alice");

        User user = userMapper.toEntity(dto);

        assertNull(user.getRole());
    }
}
