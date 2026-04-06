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
        dto.setEmail("alice@example.com");

        User user = userMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals("alice", user.getUsername());
        assertEquals("alice@example.com", user.getEmail());
        assertNull(user.getId());
    }

    @Test
    void toEntity_shouldNotMapRole() {
        UserDto dto = new UserDto();
        dto.setRole(Role.ADMIN);
        dto.setUsername("alice");
        dto.setEmail("alice@example.com");

        User user = userMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals("alice", user.getUsername());
        assertEquals("alice@example.com", user.getEmail());
        assertNull(user.getRole());
    }
}
