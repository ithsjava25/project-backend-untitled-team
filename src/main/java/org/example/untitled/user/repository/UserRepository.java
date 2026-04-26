package org.example.untitled.user.repository;

import java.util.Collection;
import java.util.Optional;
import org.example.untitled.user.Role;
import org.example.untitled.user.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends ListCrudRepository<User, Long> {

    long countByRole(Role role);

    Optional<User> findByUsername(String username);

    List<User> findByRoleIn(Collection<Role> roles);
}
