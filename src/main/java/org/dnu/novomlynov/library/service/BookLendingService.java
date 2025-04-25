package org.dnu.novomlynov.library.service;

import org.dnu.novomlynov.library.dto.BookLendingDto;
import org.dnu.novomlynov.library.model.LendingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookLendingService {
    BookLendingDto issueBook(BookLendingDto bookLendingDto);

    BookLendingDto returnBook(Long id);

    BookLendingDto getLendingById(Long id);

    Page<BookLendingDto> getAllLendings(Pageable pageable);

    Page<BookLendingDto> getLendingsByStatus(LendingStatus status, Pageable pageable);

    Page<BookLendingDto> getLendingsBySubscriber(Long subscriberId, Pageable pageable);

    Page<BookLendingDto> getCurrentLendingsForSubscriber(Long subscriberId, Pageable pageable);

    List<BookLendingDto> getOverdueBooks();

    void deleteBookLending(Long id);
}