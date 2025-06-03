package org.dnu.novomlynov.library.repository;

import org.dnu.novomlynov.library.model.User;
import org.dnu.novomlynov.library.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    @Query(
            value = "SELECT * FROM users WHERE :role = ANY(roles)",
            nativeQuery = true)
    List<User> findByRole(@Param("role") String role);

    default List<User> findByRole(UserRole role){
        return findByRole(role.name());
    }

    boolean existsByLogin(String login);
}