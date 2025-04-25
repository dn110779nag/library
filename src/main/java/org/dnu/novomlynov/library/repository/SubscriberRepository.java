package org.dnu.novomlynov.library.repository;

import org.dnu.novomlynov.library.model.Subscriber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    Page<Subscriber> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Optional<Subscriber> findByLibraryCardNumber(String libraryCardNumber);

    List<Subscriber> findByActive(boolean active);

    boolean existsByLibraryCardNumber(String libraryCardNumber);

    boolean existsByEmail(String email);
}