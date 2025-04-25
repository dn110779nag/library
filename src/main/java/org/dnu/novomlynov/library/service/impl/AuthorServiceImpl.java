package org.dnu.novomlynov.library.service.impl;

import lombok.RequiredArgsConstructor;
import org.dnu.novomlynov.library.dto.AuthorDto;
import org.dnu.novomlynov.library.exception.ResourceNotFoundException;
import org.dnu.novomlynov.library.model.Author;
import org.dnu.novomlynov.library.repository.AuthorRepository;
import org.dnu.novomlynov.library.repository.BookRepository;
import org.dnu.novomlynov.library.service.AuthorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public AuthorDto createAuthor(AuthorDto authorDto) {
        Author author = mapToEntity(authorDto);
        Author savedAuthor = authorRepository.save(author);
        return mapToDto(savedAuthor);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorDto getAuthorById(Long id) {
        return authorRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorDto> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorDto> searchAuthors(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AuthorDto updateAuthor(Long id, AuthorDto authorDto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));

        author.setName(authorDto.getName());
        if (authorDto.getBiography() != null) {
            author.setBiography(authorDto.getBiography());
        }

        Author updatedAuthor = authorRepository.save(author);
        return mapToDto(updatedAuthor);
    }

    @Override
    @Transactional
    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author not found with id: " + id);
        }

        if (isAuthorUsed(id)) {
            throw new IllegalStateException("Cannot delete author as it is associated with one or more books");
        }

        authorRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAuthorUsed(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));

        // Check if any books reference this author
        return !author.getBooks().isEmpty();
    }

    private AuthorDto mapToDto(Author author) {
        return AuthorDto.builder()
                .id(author.getId())
                .name(author.getName())
                .biography(author.getBiography())
                .createdAt(author.getCreatedAt())
                .updatedAt(author.getUpdatedAt())
                .build();
    }

    private Author mapToEntity(AuthorDto authorDto) {
        return Author.builder()
                .id(authorDto.getId())
                .name(authorDto.getName())
                .biography(authorDto.getBiography())
                .build();
    }
}