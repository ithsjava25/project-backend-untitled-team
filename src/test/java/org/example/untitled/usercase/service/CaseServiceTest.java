package org.example.untitled.usercase.service;

import org.example.untitled.user.Role;
import org.example.untitled.user.User;
import org.example.untitled.user.repository.UserRepository;
import org.example.untitled.usercase.CaseEntity;
import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.repository.CaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {

    @Mock
    private CaseRepository caseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CaseService caseService;

    private User makeUser(Long id, String username, Role role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setRole(role);
        return user;
    }

    private CaseEntity makeCaseEntity(Long id, User owner) {
        CaseEntity caseEntity = new CaseEntity();
        caseEntity.setId(id);
        caseEntity.setTitle("Test ticket");
        caseEntity.setStatus(CaseStatus.OPEN);
        caseEntity.setOwner(owner);
        return caseEntity;
    }

    @Test
    void assingTicket_shouldAssign_whenUserIsHandler() {
        User handler = makeUser(2L, "handler", Role.HANDLER);
        User owner = makeUser(1L, "owner", Role.USER);
        CaseEntity caseEntity = makeCaseEntity(1L, owner);

        when(caseRepository.findById(1L)).thenReturn(Optional.of(caseEntity));
        when(userRepository.findByUsername("handler")).thenReturn(Optional.of(handler));
        when(caseRepository.save(any())).thenReturn(caseEntity);

        CaseEntityDto result = caseService.assignTicket(1L,"handler");

        assertNotNull(result);
    }

    @Test
    void assignTicket_shouldAssign_whenUserIsAdmin() {
        User admin = makeUser(3L, "admin", Role.ADMIN);
        User owner = makeUser(1L, "owner", Role.USER);
        CaseEntity caseEntity = makeCaseEntity(1L, owner);

        when(caseRepository.findById(1L)).thenReturn(Optional.of(caseEntity));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(caseRepository.save(any())).thenReturn(caseEntity);

        CaseEntityDto result = caseService.assignTicket(1L, "admin");

        assertNotNull(result);
    }

    @Test
    void assignTicket_shouldThrow400_whenUserIsRegularUser() {
        User regularUser = makeUser(4L, "user", Role.USER);
        User owner = makeUser(1L, "owner", Role.USER);
        CaseEntity caseEntity = makeCaseEntity(1L, owner);

        when(caseRepository.findById(1L)).thenReturn(Optional.of(caseEntity));
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(regularUser));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> caseService.assignTicket(1L, "user"));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}
