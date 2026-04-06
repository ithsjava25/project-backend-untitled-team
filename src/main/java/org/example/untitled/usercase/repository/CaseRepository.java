package org.example.untitled.usercase.repository;

import org.example.untitled.usercase.CaseEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseRepository extends ListCrudRepository<CaseEntity, Long> {
}
