package org.dnu.novomlynov.library.repository;

import org.dnu.novomlynov.library.TestcontainersConfiguration;
import org.dnu.novomlynov.library.model.Author;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({TestcontainersConfiguration.class})
@ActiveProfiles("test")
@Transactional
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @AfterEach
    void clear() {
        authorRepository.deleteAll();
    }

    @Test
    void shouldSaveAuthor() {
        // given
        Author author = Author.builder()
                .name("J.K. Rowling")
                .build();

        // when
        Author savedAuthor = authorRepository.save(author);

        // then
        assertThat(savedAuthor.getId()).isNotNull();
        assertThat(savedAuthor.getName()).isEqualTo("J.K. Rowling");
    }

    @Test
    void shouldFindByNameContainingIgnoreCase() {
        // given
        authorRepository.saveAll(List.of(
                Author.builder().name("Stephen King").build(),
                Author.builder().name("J.R.R. Tolkien").build(),
                Author.builder().name("Jane Austen").build()));

        // when
        List<Author> authors = authorRepository.findByNameContainingIgnoreCase("king");

        // then
        assertThat(authors).hasSize(1);
        assertThat(authors.getFirst().getName()).isEqualTo("Stephen King");
    }

    @Test
    void shouldFindMultipleAuthorsWithSamePartialName() {
        // given
        authorRepository.saveAll(List.of(
                Author.builder().name("J.K. Rowling").build(),
                Author.builder().name("J.R.R. Tolkien").build(),
                Author.builder().name("Jane Austen").build()));

        // when
        List<Author> authors = authorRepository.findByNameContainingIgnoreCase("j.");

        // then
        assertThat(authors).hasSize(2);
        assertThat(authors).extracting(Author::getName)
                .containsExactlyInAnyOrder("J.K. Rowling", "J.R.R. Tolkien");
    }

    @Test
    void shouldCheckIfAuthorExistsByNameIgnoreCase() {
        // given
        Author author = Author.builder()
                .name("George Orwell")
                .build();
        authorRepository.save(author);

        // when & then
        assertThat(authorRepository.existsByNameIgnoreCase("george orwell")).isTrue();
        assertThat(authorRepository.existsByNameIgnoreCase("GEORGE ORWELL")).isTrue();
        assertThat(authorRepository.existsByNameIgnoreCase("George Orwell")).isTrue();
        assertThat(authorRepository.existsByNameIgnoreCase("Ernest Hemingway")).isFalse();
    }

    @Test
    void shouldFindAllAuthors() {
        // given
        authorRepository.saveAll(List.of(
                Author.builder().name("Author 1").build(),
                Author.builder().name("Author 2").build(),
                Author.builder().name("Author 3").build()));

        // when
        List<Author> authors = authorRepository.findAll();

        // then
        assertThat(authors).hasSize(3);
        assertThat(authors).extracting(Author::getName)
                .containsExactlyInAnyOrder("Author 1", "Author 2", "Author 3");
    }
}