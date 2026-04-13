package org.example.untitled.usercase.service;

import java.util.List;
import org.example.untitled.user.User;
import org.example.untitled.user.repository.UserRepository;
import org.example.untitled.usercase.CaseEntity;
import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.mapper.CaseMapper;
import org.example.untitled.usercase.repository.CaseRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));
        caseEntity.setStatus(newStatus);
        return CaseMapper.toDto(caseRepository.save(caseEntity));
    }

    public CaseEntityDto assignTicket(Long id, String username) {
        CaseEntity caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + id));
        User handler = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
        caseEntity.setAssignedTo(handler);
        return CaseMapper.toDto(caseRepository.save(caseEntity));
    }
}