package org.dnu.novomlynov.library.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dnu.novomlynov.library.dto.BookLendingDto;
import org.dnu.novomlynov.library.model.LendingStatus;
import org.dnu.novomlynov.library.service.BookLendingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lendings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LIBRARIAN')")
public class BookLendingController {

    private final BookLendingService bookLendingService;

    @PostMapping("/issue")
    public ResponseEntity<BookLendingDto> issueBook(@Valid @RequestBody BookLendingDto bookLendingDto) {
        return new ResponseEntity<>(bookLendingService.issueBook(bookLendingDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<BookLendingDto> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookLendingService.returnBook(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookLendingDto> getLendingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookLendingService.getLendingById(id));
    }

    @GetMapping
    public ResponseEntity<Page<BookLendingDto>> getAllLendings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(bookLendingService.getAllLendings(pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<BookLendingDto>> getLendingsByStatus(
            @PathVariable LendingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookLendingService.getLendingsByStatus(status, pageable));
    }

    @GetMapping("/subscriber/{subscriberId}")
    public ResponseEntity<Page<BookLendingDto>> getLendingsBySubscriber(
            @PathVariable Long subscriberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookLendingService.getLendingsBySubscriber(subscriberId, pageable));
    }

    @GetMapping("/subscriber/{subscriberId}/current")
    public ResponseEntity<Page<BookLendingDto>> getCurrentLendingsForSubscriber(
            @PathVariable Long subscriberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookLendingService.getCurrentLendingsForSubscriber(subscriberId, pageable));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<BookLendingDto>> getOverdueBooks() {
        return ResponseEntity.ok(bookLendingService.getOverdueBooks());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookLending(@PathVariable Long id) {
        bookLendingService.deleteBookLending(id);
        return ResponseEntity.noContent().build();
    }
}