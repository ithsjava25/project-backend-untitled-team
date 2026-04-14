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
import org.springframework.web.server.ResponseStatusException;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final CaseMapper caseMapper;
    private final UserRepository userRepository;

    public CaseService(
            CaseRepository caseRepository,
            CaseMapper caseMapper,
            UserRepository userRepository) {
        this.caseRepository = caseRepository;
        this.caseMapper = caseMapper;
        this.userRepository = userRepository;
    }

    public CaseEntityDto createTicket(CreateCaseRequest request, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        CaseEntity caseEntity = new CaseEntity();
        caseEntity.setTitle(request.getTitle());
        caseEntity.setDescription(request.getDescription());
        caseEntity.setStatus(CaseStatus.OPEN);
        caseEntity.setOwner(owner);
        return caseMapper.toDto(caseRepository.save(caseEntity));
    }

    public List<CaseEntityDto> getMyTickets(String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return caseRepository.findByOwner(owner).stream()
                .map(caseMapper::toDto)
                .toList();
    }

    public CaseEntityDto updateTicket(Long id, CreateCaseRequest request, String username) {
        CaseEntity caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + id));
        if (!caseEntity.getOwner().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this ticket");
        }
        caseEntity.setTitle(request.getTitle());
        caseEntity.setDescription(request.getDescription());
        return caseMapper.toDto(caseRepository.save(caseEntity));
    }

    public List<CaseEntityDto> getAllTickets() {
        return caseRepository.findAll().stream()
                .map(caseMapper::toDto)
                .toList();
    }

    public List<CaseEntityDto> getTicketsAssignedTo(User user) {
        return caseRepository.findByAssignedTo(user).stream()
                .map(caseMapper::toDto)
                .toList();
    }

    public CaseEntityDto updateStatus(Long id, CaseStatus newStatus) {
        CaseEntity caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + id));
        caseEntity.setStatus(newStatus);
        return caseMapper.toDto(caseRepository.save(caseEntity));
    }

    public CaseEntityDto assignTicket(Long id, String username) {
        CaseEntity caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + id));
        User handler = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        caseEntity.setAssignedTo(handler);
        return caseMapper.toDto(caseRepository.save(caseEntity));
    }
}