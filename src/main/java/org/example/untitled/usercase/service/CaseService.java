package org.example.untitled.usercase.service;

import org.example.untitled.usercase.mapper.CaseMapper;
import org.example.untitled.usercase.repository.CaseRepository;
import org.springframework.stereotype.Service;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final CaseMapper caseMapper;

    public CaseService(CaseRepository caseRepository, CaseMapper caseMapper) {
        this.caseRepository = caseRepository;
        this.caseMapper = caseMapper;
    }
}
