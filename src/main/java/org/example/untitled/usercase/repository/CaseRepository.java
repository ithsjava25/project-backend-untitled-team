package org.example.untitled.usercase.repository;

import org.example.untitled.user.User;
import org.example.untitled.usercase.CaseEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseRepository extends ListCrudRepository<CaseEntity, Long> {

    boolean existsByTitleAndOwner(String title, User user);
    boolean existsByTitleAndOwnerAndIdNot(String title, User owner, Long id);
    List<CaseEntity> findByAssignedTo(User assignedTo);

    List<CaseEntity> findByOwner(User owner);

    User findOwnerById(long id);

    CaseEntity findCaseEntityById(long id);
}
