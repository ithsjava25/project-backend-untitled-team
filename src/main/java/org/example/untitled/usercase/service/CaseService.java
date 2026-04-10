package org.example.untitled.usercase.service;

import org.example.untitled.user.User;
import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.dto.CreateCaseRequest;
import org.example.untitled.usercase.mapper.CaseMapper;
import org.example.untitled.usercase.repository.CaseRepository;
import org.springframework.stereotype.Service;

@Service
public class CaseService {

    private final CaseRepository caseRepository;

    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public void saveTicket(CreateCaseRequest createForm, User user) {
        if (createForm == null) {
            throw new IllegalArgumentException("ticketForm can not be null");
        }
        if (caseRepository.existsByTitleAndOwner(createForm.getTitle(), user))
            throw new IllegalArgumentException(
                    "A ticket for this issue is already in the system"
            );
        var entity = CaseMapper.toEntity(createForm);
        entity.setOwner(user);
        entity.setStatus(CaseStatus.OPEN);
        caseRepository.save(entity);
    }
}
