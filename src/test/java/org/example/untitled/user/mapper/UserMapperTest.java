package org.example.untitled.user.mapper;

import org.example.untitled.user.Role;
import org.example.untitled.user.User;
import org.example.untitled.user.dto.UserDto;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toEntity_shouldNotMapId() {
        UserDto dto = new UserDto(99L,
                "alice",
                "alice@example.com",
                Role.USER,
                Instant.now()
        );

        User user = UserMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals("alice", user.getUsername());
        assertEquals("alice@example.com", user.getEmail());
        assertNull(user.getId());
    }

    @Test
    void toEntity_shouldNotMapRole() {
        UserDto dto = new UserDto(99L,
                "alice",
                "alice@example.com",
                Role.ADMIN,
                Instant.now()
        );

        User user = UserMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals("alice", user.getUsername());
        assertEquals("alice@example.com", user.getEmail());
        assertNull(user.getRole());
    }
}
