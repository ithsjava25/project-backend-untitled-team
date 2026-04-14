package org.example.untitled.usercase.repository;

import java.util.List;
import org.example.untitled.user.User;
import org.example.untitled.usercase.CaseEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseRepository extends ListCrudRepository<CaseEntity, Long> {

    boolean existsByTitleAndOwner(String title, User user);
    List<CaseEntity> findByAssignedTo(User assignedTo);
}
