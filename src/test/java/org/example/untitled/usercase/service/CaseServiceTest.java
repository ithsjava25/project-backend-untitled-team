package org.example.untitled.usercase.service;

import org.example.untitled.user.Role;
import org.example.untitled.user.User;
import org.example.untitled.user.repository.UserRepository;
import org.example.untitled.usercase.CaseEntity;
import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CreateCaseRequest;
import org.example.untitled.usercase.repository.CaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {

    @Mock
    private CaseRepository caseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogService auditLogService;

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
    void assignTicket_shouldAssign_whenUserIsHandler() {
        User handler = makeUser(2L, "handler", Role.HANDLER);
        User owner = makeUser(1L, "owner", Role.USER);
        CaseEntity caseEntity = makeCaseEntity(1L, owner);

        when(caseRepository.findById(1L)).thenReturn(Optional.of(caseEntity));
        when(userRepository.findByUsername("handler")).thenReturn(Optional.of(handler));
        when(caseRepository.save(any())).thenReturn(caseEntity);

        CaseEntityDto result = caseService.assignTicket(1L, "handler");

        assertNotNull(result);
        assertEquals(handler, caseEntity.getAssignedTo(), "Ticket was not assigned to handler");
    }

    @Test
    void assignTicket_shouldThrow400_whenUserIsAdmin() {
        User admin = makeUser(3L, "admin", Role.ADMIN);
        User owner = makeUser(1L, "owner", Role.USER);
        CaseEntity caseEntity = makeCaseEntity(1L, owner);

        when(caseRepository.findById(1L)).thenReturn(Optional.of(caseEntity));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> caseService.assignTicket(1L, "admin"));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
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

    @Test
    void assignTicket_throwsNotFound_whenTicketNotFound() {
        when(caseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> caseService.assignTicket(99L, "handler"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void assignTicket_throwsNotFound_whenHandlerNotFound() {
        User owner = makeUser(1L, "owner", Role.USER);
        CaseEntity caseEntity = makeCaseEntity(10L, owner);

        when(caseRepository.findById(10L)).thenReturn(Optional.of(caseEntity));
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> caseService.assignTicket(10L, "unknown"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void assignTicket_shouldReassign_whenAlreadyAssigned() {
        User owner = makeUser(1L, "owner", Role.USER);
        User handler1 = makeUser(2L, "handler1", Role.HANDLER);
        User handler2 = makeUser(3L, "handler2", Role.HANDLER);
        CaseEntity caseEntity = makeCaseEntity(1L, owner);
        caseEntity.setAssignedTo(handler1);

        when(caseRepository.findById(1L)).thenReturn(Optional.of(caseEntity));
        when(userRepository.findByUsername("handler2")).thenReturn(Optional.of(handler2));
        when(caseRepository.save(any())).thenReturn(caseEntity);

        CaseEntityDto result = caseService.assignTicket(1L, "handler2");

        assertNotNull(result);
        assertEquals(handler2, caseEntity.getAssignedTo());
    }

    // --- createTicket ---

    @Test
    void createTicket_success_returnsDto() {
        User owner = makeUser(1L, "alice", Role.USER);
        CaseEntity ticket = makeCaseEntity(10L, owner);
        CreateCaseRequest req = new CreateCaseRequest();
        req.setTitle("Test ticket");
        req.setDescription("Description");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(owner));
        when(caseRepository.existsByTitleAndOwner(req.getTitle(), owner)).thenReturn(false);
        when(caseRepository.save(any(CaseEntity.class))).thenReturn(ticket);

        CaseEntityDto result = caseService.createTicket(req, "alice");

        assertThat(result.title()).isEqualTo("Test ticket");
        assertThat(result.ownerUsername()).isEqualTo("alice");
    }

    @Test
    void createTicket_throwsNotFound_whenUserNotFound() {
        CreateCaseRequest req = new CreateCaseRequest();
        req.setTitle("Test ticket");
        req.setDescription("Description");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> caseService.createTicket(req, "unknown"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createTicket_throwsConflict_whenDuplicateTitle() {
        User owner = makeUser(1L, "alice", Role.USER);
        CreateCaseRequest req = new CreateCaseRequest();
        req.setTitle("Duplicate");
        req.setDescription("Description");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(owner));
        when(caseRepository.existsByTitleAndOwner(req.getTitle(), owner)).thenReturn(true);

        assertThatThrownBy(() -> caseService.createTicket(req, "alice"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    // --- getMyTickets ---

    @Test
    void getMyTickets_returnsTicketsForUser() {
        User owner = makeUser(1L, "alice", Role.USER);
        CaseEntity ticket = makeCaseEntity(10L, owner);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(owner));
        when(caseRepository.findByOwner(owner)).thenReturn(List.of(ticket));

        List<CaseEntityDto> result = caseService.getMyTickets("alice");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().ownerUsername()).isEqualTo("alice");
    }

    @Test
    void getMyTickets_throwsNotFound_whenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> caseService.getMyTickets("unknown"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    // --- updateTicket ---

    @Test
    void updateTicket_success_returnsUpdatedDto() {
        User owner = makeUser(1L, "alice", Role.USER);
        CaseEntity ticket = makeCaseEntity(10L, owner);
        CreateCaseRequest req = new CreateCaseRequest();
        req.setTitle("New title");
        req.setDescription("New desc");

        when(caseRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(caseRepository.existsByTitleAndOwnerAndIdNot("New title", owner, 10L)).thenReturn(false);
        when(caseRepository.save(ticket)).thenReturn(ticket);

        CaseEntityDto result = caseService.updateTicket(10L, req, "alice");

        assertThat(result).isNotNull();
        verify(caseRepository).save(ticket);
    }

    @Test
    void updateTicket_throwsNotFound_whenTicketNotFound() {
        CreateCaseRequest req = new CreateCaseRequest();
        req.setTitle("Title");
        req.setDescription("Desc");

        when(caseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> caseService.updateTicket(99L, req, "alice"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateTicket_throwsForbidden_whenNotOwner() {
        User owner = makeUser(1L, "alice", Role.USER);
        CaseEntity ticket = makeCaseEntity(10L, owner);
        CreateCaseRequest req = new CreateCaseRequest();
        req.setTitle("Title");
        req.setDescription("Desc");

        when(caseRepository.findById(10L)).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> caseService.updateTicket(10L, req, "bob"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void updateTicket_throwsConflict_whenDuplicateTitle() {
        User owner = makeUser(1L, "alice", Role.USER);
        CaseEntity ticket = makeCaseEntity(10L, owner);
        CreateCaseRequest req = new CreateCaseRequest();
        req.setTitle("Duplicate title");
        req.setDescription("Desc");

        when(caseRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(caseRepository.existsByTitleAndOwnerAndIdNot("Duplicate title", owner, 10L)).thenReturn(true);

        assertThatThrownBy(() -> caseService.updateTicket(10L, req, "alice"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }


    // --- getAllTickets ---

    @Test
    void getAllTickets_returnsAllTickets() {
        User owner = makeUser(1L, "alice", Role.USER);
        CaseEntity ticket = makeCaseEntity(10L, owner);

        when(caseRepository.findAll()).thenReturn(List.of(ticket));

        List<CaseEntityDto> result = caseService.getAllTickets();

        assertThat(result).hasSize(1);
    }

    // --- getTicketsAssignedTo ---

    @Test
    void getTicketsAssignedTo_returnsAssignedTickets() {
        User owner = makeUser(1L, "alice", Role.USER);
        User handler = makeUser(2L, "bob", Role.HANDLER);
        CaseEntity ticket = makeCaseEntity(10L, owner);
        ticket.setAssignedTo(handler);

        when(caseRepository.findByAssignedTo(handler)).thenReturn(List.of(ticket));

        List<CaseEntityDto> result = caseService.getTicketsAssignedTo(handler);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().assignedToUsername()).isEqualTo("bob");
    }

    // --- updateStatus ---

    @Test
    void updateStatus_success_updatesStatus() {
        User owner = makeUser(1L, "alice", Role.USER);
        CaseEntity ticket = makeCaseEntity(10L, owner);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(owner));
        when(caseRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(caseRepository.save(ticket)).thenReturn(ticket);

        CaseEntityDto result = caseService.updateStatus(10L, CaseStatus.IN_PROGRESS, "alice");

        assertThat(result.status()).isEqualTo(CaseStatus.IN_PROGRESS);
    }

    @Test
    void updateStatus_throwsNotFound_whenTicketNotFound() {
        User actor = makeUser(1L, "alice", Role.USER);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(actor));
        when(caseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> caseService.updateStatus(99L, CaseStatus.IN_PROGRESS, "alice"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
