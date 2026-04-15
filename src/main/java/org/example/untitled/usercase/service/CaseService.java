package org.example.untitled.usercase.service;

import java.util.List;
import org.example.untitled.user.User;
import org.example.untitled.user.repository.UserRepository;
import org.example.untitled.usercase.CaseEntity;
import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CreateCaseRequest;
import org.example.untitled.usercase.mapper.CaseMapper;
import org.example.untitled.usercase.repository.CaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;

    public CaseService(
            CaseRepository caseRepository,
            UserRepository userRepository) {
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
    }

    public CaseEntityDto createTicket(CreateCaseRequest request, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (caseRepository.existsByTitleAndOwner(request.getTitle(), owner)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "A ticket for this issue is already in the system");
        }
        CaseEntity caseEntity = CaseMapper.toEntity(request);
        caseEntity.setOwner(owner);
        caseEntity.setStatus(CaseStatus.OPEN);
        return CaseMapper.toDto(caseRepository.save(caseEntity));
    }

    public List<CaseEntityDto> getMyTickets(String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return caseRepository.findByOwner(owner).stream()
                .map(CaseMapper::toDto)
                .toList();
    }

    public CaseEntityDto updateTicket(Long id, CreateCaseRequest request, String username) {
        CaseEntity caseEntity = caseRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + id));
        if (!caseEntity.getOwner().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this ticket");
        }
        if (caseRepository.existsByTitleAndOwnerAndIdNot(request.getTitle(), caseEntity.getOwner(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A ticket with this title already exists");
        }
        caseEntity.setTitle(request.getTitle());
        caseEntity.setDescription(request.getDescription());
        return CaseMapper.toDto(caseRepository.save(caseEntity));
    }

    @Transactional
    public void saveTicket(CreateCaseRequest createForm, User user) {
        if (createForm == null) {
            throw new IllegalArgumentException("ticketForm can not be null");
        }
        if (user == null) throw new IllegalArgumentException("User can not be null");
        if (caseRepository.existsByTitleAndOwner(createForm.getTitle(), user))
            throw new IllegalArgumentException("A ticket for this issue is already in the system");
        var entity = CaseMapper.toEntity(createForm);
        entity.setOwner(user);
        entity.setStatus(CaseStatus.OPEN);
        caseRepository.save(entity);
    }

    public List<CaseEntityDto> getAllTickets() {
        return caseRepository.findAll().stream()
                .map(CaseMapper::toDto)
                .toList();
    }

    public List<CaseEntityDto> getTicketsAssignedTo(User user) {
        return caseRepository.findByAssignedTo(user).stream()
                .map(CaseMapper::toDto)
                .toList();
    }

    public CaseEntityDto updateStatus(Long id, CaseStatus newStatus) {
        CaseEntity caseEntity = caseRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + id));
        caseEntity.setStatus(newStatus);
        return CaseMapper.toDto(caseRepository.save(caseEntity));
    }

    public CaseEntityDto assignTicket(Long id, String username) {
        CaseEntity caseEntity = caseRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + id));
        User handler = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        caseEntity.setAssignedTo(handler);
        return CaseMapper.toDto(caseRepository.save(caseEntity));
    }
}
