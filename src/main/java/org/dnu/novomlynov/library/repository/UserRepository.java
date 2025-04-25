package org.dnu.novomlynov.library.repository;

import org.dnu.novomlynov.library.model.User;
import org.dnu.novomlynov.library.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    List<User> findByRoles(UserRole role);

    boolean existsByLogin(String login);
}