package org.dnu.novomlynov.library.repository;

import org.dnu.novomlynov.library.model.UserPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPasswordRepository extends JpaRepository<UserPassword, Long> {
}