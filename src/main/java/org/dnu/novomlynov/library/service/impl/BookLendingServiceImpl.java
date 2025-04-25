package org.dnu.novomlynov.library.service.impl;

import lombok.RequiredArgsConstructor;
import org.dnu.novomlynov.library.dto.BookDto;
import org.dnu.novomlynov.library.dto.BookLendingDto;
import org.dnu.novomlynov.library.dto.SubscriberDto;
import org.dnu.novomlynov.library.exception.ResourceNotFoundException;
import org.dnu.novomlynov.library.model.Book;
import org.dnu.novomlynov.library.model.BookLending;
import org.dnu.novomlynov.library.model.LendingStatus;
import org.dnu.novomlynov.library.model.Subscriber;
import org.dnu.novomlynov.library.repository.BookLendingRepository;
import org.dnu.novomlynov.library.repository.BookRepository;
import org.dnu.novomlynov.library.repository.SubscriberRepository;
import org.dnu.novomlynov.library.service.BookLendingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookLendingServiceImpl implements BookLendingService {

    private final BookLendingRepository bookLendingRepository;
    private final BookRepository bookRepository;
    private final SubscriberRepository subscriberRepository;

    @Override
    @Transactional
    public BookLendingDto issueBook(BookLendingDto bookLendingDto) {
        Book book = bookRepository.findById(bookLendingDto.getBookId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Book not found with id: " + bookLendingDto.getBookId()));

        Subscriber subscriber = subscriberRepository.findById(bookLendingDto.getSubscriberId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscriber not found with id: " + bookLendingDto.getSubscriberId()));

        // Check if the book has available copies
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies for this book");
        }

        // Check if the subscriber is active
        if (!subscriber.isActive()) {
            throw new IllegalStateException("Subscriber is not active");
        }

        // Create the lending record
        BookLending bookLending = BookLending.builder()
                .book(book)
                .subscriber(subscriber)
                .issueDate(bookLendingDto.getIssueDate() != null ? bookLendingDto.getIssueDate() : LocalDate.now())
                .dueDate(bookLendingDto.getDueDate())
                .status(LendingStatus.ISSUED)
                .build();

        // Update book availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // Save the lending record
        BookLending savedLending = bookLendingRepository.save(bookLending);
        return mapToDto(savedLending);
    }

    @Override
    @Transactional
    public BookLendingDto returnBook(Long id) {
        BookLending bookLending = bookLendingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lending record not found with id: " + id));

        // Check if the book is already returned
        if (bookLending.getStatus() == LendingStatus.RETURNED) {
            throw new IllegalStateException("Book is already returned");
        }

        // Update the book's available copies
        Book book = bookLending.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        // Update the lending record
        bookLending.setReturnDate(LocalDate.now());
        bookLending.setStatus(LendingStatus.RETURNED);

        BookLending updatedLending = bookLendingRepository.save(bookLending);
        return mapToDto(updatedLending);
    }

    @Override
    @Transactional(readOnly = true)
    public BookLendingDto getLendingById(Long id) {
        return bookLendingRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Lending record not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookLendingDto> getAllLendings(Pageable pageable) {
        return bookLendingRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookLendingDto> getLendingsByStatus(LendingStatus status, Pageable pageable) {
        return bookLendingRepository.findByStatus(status, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookLendingDto> getLendingsBySubscriber(Long subscriberId, Pageable pageable) {
        if (!subscriberRepository.existsById(subscriberId)) {
            throw new ResourceNotFoundException("Subscriber not found with id: " + subscriberId);
        }
        return bookLendingRepository.findBySubscriberId(subscriberId, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookLendingDto> getCurrentLendingsForSubscriber(Long subscriberId, Pageable pageable) {
        if (!subscriberRepository.existsById(subscriberId)) {
            throw new ResourceNotFoundException("Subscriber not found with id: " + subscriberId);
        }
        return bookLendingRepository.findBySubscriberIdAndStatus(subscriberId, LendingStatus.ISSUED, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public List<BookLendingDto> getOverdueBooks() {
        LocalDate today = LocalDate.now();

        // Find all issued books with due date before today
        List<BookLending> overdueBooks = bookLendingRepository.findOverdueBooks(today);

        // Update their status to OVERDUE
        overdueBooks.forEach(lending -> {
            if (lending.getStatus() == LendingStatus.ISSUED) {
                lending.setStatus(LendingStatus.OVERDUE);
                bookLendingRepository.save(lending);
            }
        });

        return overdueBooks.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBookLending(Long id) {
        BookLending bookLending = bookLendingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lending record not found with id: " + id));

        // If the book is currently issued, update the book's available copies
        if (bookLending.getStatus() == LendingStatus.ISSUED || bookLending.getStatus() == LendingStatus.OVERDUE) {
            Book book = bookLending.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        }

        bookLendingRepository.deleteById(id);
    }

    private BookLendingDto mapToDto(BookLending bookLending) {
        BookLendingDto dto = BookLendingDto.builder()
                .id(bookLending.getId())
                .bookId(bookLending.getBook().getId())
                .subscriberId(bookLending.getSubscriber().getId())
                .issueDate(bookLending.getIssueDate())
                .dueDate(bookLending.getDueDate())
                .returnDate(bookLending.getReturnDate())
                .status(bookLending.getStatus())
                .createdAt(bookLending.getCreatedAt())
                .updatedAt(bookLending.getUpdatedAt())
                .build();

        // Set simplified book info
        dto.setBook(BookDto.builder()
                .id(bookLending.getBook().getId())
                .title(bookLending.getBook().getTitle())
                .isbn(bookLending.getBook().getIsbn())
                .build());

        // Set simplified subscriber info
        dto.setSubscriber(SubscriberDto.builder()
                .id(bookLending.getSubscriber().getId())
                .name(bookLending.getSubscriber().getName())
                .libraryCardNumber(bookLending.getSubscriber().getLibraryCardNumber())
                .build());

        return dto;
    }
}