package org.dnu.novomlynov.library.service;

import org.dnu.novomlynov.library.dto.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    BookDto createBook(BookDto bookDto);

    BookDto getBookById(Long id);

    BookDto getBookByIsbn(String isbn);

    Page<BookDto> getAllBooks(Pageable pageable);

    Page<BookDto> searchBooks(String searchTerm, Pageable pageable);

    Page<BookDto> getBooksByAuthor(Long authorId, Pageable pageable);

    Page<BookDto> getBooksByCategory(Long categoryId, Pageable pageable);

    BookDto updateBook(Long id, BookDto bookDto);

    void updateBookCopies(Long id, int totalCopies);

    void deleteBook(Long id);

    boolean isBookAvailable(Long id);

    List<BookDto> getAvailableBooks();
}