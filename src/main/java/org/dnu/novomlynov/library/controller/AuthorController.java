package org.dnu.novomlynov.library.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dnu.novomlynov.library.dto.AuthorDto;
import org.dnu.novomlynov.library.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('BOOK_ADMIN', 'LIBRARIAN')")
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    @PreAuthorize("hasRole('BOOK_ADMIN')")
    public ResponseEntity<AuthorDto> createAuthor(@Valid @RequestBody AuthorDto authorDto) {
        return new ResponseEntity<>(authorService.createAuthor(authorDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @GetMapping
    public ResponseEntity<List<AuthorDto>> getAllAuthors() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @GetMapping("/search")
    public ResponseEntity<List<AuthorDto>> searchAuthors(@RequestParam String name) {
        return ResponseEntity.ok(authorService.searchAuthors(name));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BOOK_ADMIN')")
    public ResponseEntity<AuthorDto> updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody AuthorDto authorDto) {
        return ResponseEntity.ok(authorService.updateAuthor(id, authorDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BOOK_ADMIN')")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/used")
    @PreAuthorize("hasRole('BOOK_ADMIN')")
    public ResponseEntity<Boolean> isAuthorUsed(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.isAuthorUsed(id));
    }
}