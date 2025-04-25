package org.dnu.novomlynov.library.service;

import org.dnu.novomlynov.library.dto.AuthorDto;

import java.util.List;

public interface AuthorService {
    AuthorDto createAuthor(AuthorDto authorDto);

    AuthorDto getAuthorById(Long id);

    List<AuthorDto> getAllAuthors();

    List<AuthorDto> searchAuthors(String name);

    AuthorDto updateAuthor(Long id, AuthorDto authorDto);

    void deleteAuthor(Long id);

    boolean isAuthorUsed(Long id);
}