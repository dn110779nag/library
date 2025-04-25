package org.dnu.novomlynov.library.service.impl;

import lombok.RequiredArgsConstructor;
import org.dnu.novomlynov.library.dto.AuthorDto;
import org.dnu.novomlynov.library.dto.BookDto;
import org.dnu.novomlynov.library.dto.CategoryDto;
import org.dnu.novomlynov.library.exception.ResourceNotFoundException;
import org.dnu.novomlynov.library.model.Author;
import org.dnu.novomlynov.library.model.Book;
import org.dnu.novomlynov.library.model.Category;
import org.dnu.novomlynov.library.repository.AuthorRepository;
import org.dnu.novomlynov.library.repository.BookRepository;
import org.dnu.novomlynov.library.repository.CategoryRepository;
import org.dnu.novomlynov.library.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public BookDto createBook(BookDto bookDto) {
        if (bookDto.getIsbn() != null && bookRepository.existsByIsbn(bookDto.getIsbn())) {
            throw new IllegalArgumentException("Book with this ISBN already exists");
        }

        Book book = mapToEntity(bookDto);
        Book savedBook = bookRepository.save(book);
        return mapToDto(savedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDto> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDto> searchBooks(String searchTerm, Pageable pageable) {
        return bookRepository.search(searchTerm, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDto> getBooksByAuthor(Long authorId, Pageable pageable) {
        if (!authorRepository.existsById(authorId)) {
            throw new ResourceNotFoundException("Author not found with id: " + authorId);
        }
        return bookRepository.findByAuthorNameContainingIgnoreCase(authorId.toString(), pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDto> getBooksByCategory(Long categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        return bookRepository.findByCategoryNameContainingIgnoreCase(categoryId.toString(), pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public BookDto updateBook(Long id, BookDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // Check if another book already has the requested ISBN
        if (bookDto.getIsbn() != null && !bookDto.getIsbn().equals(book.getIsbn()) &&
                bookRepository.existsByIsbn(bookDto.getIsbn())) {
            throw new IllegalArgumentException("Another book with this ISBN already exists");
        }

        book.setTitle(bookDto.getTitle());
        book.setIsbn(bookDto.getIsbn());

        // Update authors if provided
        if (bookDto.getAuthorIds() != null && !bookDto.getAuthorIds().isEmpty()) {
            Set<Author> authors = new HashSet<>();
            for (Long authorId : bookDto.getAuthorIds()) {
                Author author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
                authors.add(author);
            }
            book.setAuthors(authors);
        }

        // Update categories if provided
        if (bookDto.getCategoryIds() != null && !bookDto.getCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>();
            for (Long categoryId : bookDto.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
                categories.add(category);
            }
            book.setCategories(categories);
        }

        Book updatedBook = bookRepository.save(book);
        return mapToDto(updatedBook);
    }

    @Override
    @Transactional
    public void updateBookCopies(Long id, int totalCopies) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }

        // Calculate the difference between new total and current total
        int difference = totalCopies - book.getTotalCopies();

        // Update total copies
        book.setTotalCopies(totalCopies);

        // Update available copies (cannot be negative)
        int newAvailableCopies = Math.max(0, book.getAvailableCopies() + difference);
        book.setAvailableCopies(newAvailableCopies);

        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }

        // Check if book has outstanding lendings
        Book book = bookRepository.findById(id).orElseThrow();
        if (book.getTotalCopies() > book.getAvailableCopies()) {
            throw new IllegalStateException("Cannot delete book as it has outstanding lendings");
        }

        bookRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookAvailable(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return book.getAvailableCopies() > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> getAvailableBooks() {
        return bookRepository.findAll().stream()
                .filter(book -> book.getAvailableCopies() > 0)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private BookDto mapToDto(Book book) {
        BookDto dto = BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();

        // Add author IDs
        Set<Long> authorIds = new HashSet<>();
        Set<AuthorDto> authors = new HashSet<>();

        for (Author author : book.getAuthors()) {
            authorIds.add(author.getId());

            // Create a simplified AuthorDto without books to avoid circular references
            authors.add(AuthorDto.builder()
                    .id(author.getId())
                    .name(author.getName())
                    .build());
        }
        dto.setAuthorIds(authorIds);
        dto.setAuthors(authors);

        // Add category IDs
        Set<Long> categoryIds = new HashSet<>();
        Set<CategoryDto> categories = new HashSet<>();

        for (Category category : book.getCategories()) {
            categoryIds.add(category.getId());

            // Create a simplified CategoryDto without books to avoid circular references
            categories.add(CategoryDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .build());
        }
        dto.setCategoryIds(categoryIds);
        dto.setCategories(categories);

        return dto;
    }

    private Book mapToEntity(BookDto bookDto) {
        Book book = Book.builder()
                .id(bookDto.getId())
                .title(bookDto.getTitle())
                .isbn(bookDto.getIsbn())
                .totalCopies(bookDto.getTotalCopies() != null ? bookDto.getTotalCopies() : 0)
                .availableCopies(
                        bookDto.getAvailableCopies() != null ? bookDto.getAvailableCopies() : bookDto.getTotalCopies())
                .build();

        // Set authors
        if (bookDto.getAuthorIds() != null && !bookDto.getAuthorIds().isEmpty()) {
            Set<Author> authors = new HashSet<>();
            for (Long authorId : bookDto.getAuthorIds()) {
                Author author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
                authors.add(author);
            }
            book.setAuthors(authors);
        }

        // Set categories
        if (bookDto.getCategoryIds() != null && !bookDto.getCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>();
            for (Long categoryId : bookDto.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
                categories.add(category);
            }
            book.setCategories(categories);
        }

        return book;
    }
}