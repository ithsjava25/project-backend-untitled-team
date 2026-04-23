package org.example.untitled.user.service;

import org.example.untitled.auth.dto.RegisterRequest;
import org.example.untitled.exception.EmailAlreadyExistsException;
import org.example.untitled.exception.UserAlreadyExistsException;
import org.example.untitled.user.Role;
import org.example.untitled.user.User;
import org.example.untitled.user.dto.UserDto;
import org.example.untitled.user.repository.UserRepository;
import org.example.untitled.usercase.service.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private AuditLogService auditLogService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("alice");
        registerRequest.setEmail("alice@example.com");
        registerRequest.setPassword("password123");
    }

    // --- register ---

    @Test
    void register_success_savesEncodedPassword() {
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");

        userService.register(registerRequest);

        verify(passwordEncoder).encode("password123");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("alice");
        assertThat(captor.getValue().getPassword()).isEqualTo("hashed");
        assertThat(captor.getValue().getRole()).isEqualTo(Role.USER);
    }

    @Test
    void register_throwsUserAlreadyExists_whenUsernameTaken() {
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("constraint [username]"));

        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void register_throwsEmailAlreadyExists_whenEmailTaken() {
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("constraint [email]"));

        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    // --- updateRole ---

    @Test
    void updateRole_success_updatesAndReturnsDto() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setRole(Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto result = userService.updateRole(1L, Role.HANDLER);

        assertThat(result.username()).isEqualTo("alice");
        assertThat(result.role()).isEqualTo(Role.HANDLER);
        assertThat(user.getRole()).isEqualTo(Role.HANDLER);
    }

    @Test
    void updateRole_throwsNotFound_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateRole(99L, Role.HANDLER))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    // --- getAllUsers ---

    @Test
    void getAllUsers_returnsAllUsers() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setRole(Role.USER);

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().username()).isEqualTo("alice");
    }
}