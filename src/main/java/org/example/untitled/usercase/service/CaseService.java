package org.example.untitled.usercase.service;

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

import java.util.List;

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

    @Transactional
    public void saveTicket(CreateCaseRequest createForm, User user) {
        if (createForm == null) {
            throw new IllegalArgumentException("ticketForm can not be null");
        }
        if (user == null)
            throw new IllegalArgumentException("User can not be null");
        if (caseRepository.existsByTitleAndOwner(createForm.getTitle(), user))
            throw new IllegalArgumentException(
                    "A ticket for this issue is already in the system"
            );
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + id));
        caseEntity.setStatus(newStatus);
        return CaseMapper.toDto(caseRepository.save(caseEntity));
    }

    public CaseEntityDto assignTicket(Long id, String username) {
        CaseEntity caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + id));
        User handler = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        caseEntity.setAssignedTo(handler);
        return CaseMapper.toDto(caseRepository.save(caseEntity));
    }
}
