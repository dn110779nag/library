package org.dnu.novomlynov.library.repository;

import org.dnu.novomlynov.library.model.BookLending;
import org.dnu.novomlynov.library.model.LendingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookLendingRepository extends JpaRepository<BookLending, Long> {
    List<BookLending> findBySubscriberId(Long subscriberId);

    Page<BookLending> findBySubscriberId(Long subscriberId, Pageable pageable);

    List<BookLending> findByBookIdAndStatus(Long bookId, LendingStatus status);

    Page<BookLending> findByStatus(LendingStatus status, Pageable pageable);

    Page<BookLending> findBySubscriberIdAndStatus(Long subscriberId, LendingStatus status, Pageable pageable);

    @Query("SELECT bl FROM BookLending bl WHERE bl.status = 'ISSUED' AND bl.dueDate < :today")
    List<BookLending> findOverdueBooks(@Param("today") LocalDate today);

    @Query("SELECT COUNT(bl) FROM BookLending bl WHERE bl.subscriber.id = :subscriberId AND bl.status = 'ISSUED'")
    long countCurrentBorrowingsForSubscriber(@Param("subscriberId") Long subscriberId);
}