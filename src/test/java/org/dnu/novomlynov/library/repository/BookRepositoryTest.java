package org.dnu.novomlynov.library.repository;

import org.dnu.novomlynov.library.TestcontainersConfiguration;
import org.dnu.novomlynov.library.model.Author;
import org.dnu.novomlynov.library.model.Book;
import org.dnu.novomlynov.library.model.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({TestcontainersConfiguration.class})
@ActiveProfiles("test")
@Transactional
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void clear() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    private Author author1;
    private Author author2;
    private Category category1;
    private Category category2;

    @BeforeEach
    void setup() {
        // Clear any previous test data
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        categoryRepository.deleteAll();

        // Create test authors
        author1 = authorRepository.save(Author.builder().name("J.K. Rowling").build());
        author2 = authorRepository.save(Author.builder().name("George Orwell").build());

        // Create test categories
        category1 = categoryRepository.save(Category.builder().name("Fantasy").build());
        category2 = categoryRepository.save(Category.builder().name("Dystopian").build());
    }

    @Test
    void shouldSaveBook() {
        // given
        Set<Author> authors = new HashSet<>();
        authors.add(author1);

        Set<Category> categories = new HashSet<>();
        categories.add(category1);

        Book book = Book.builder()
                .title("Harry Potter and the Philosopher's Stone")
                .isbn("9780747532743")
                .publicationDate(LocalDate.of(1997, 6, 26))
                .totalCopies(10)
                .availableCopies(10)
                .authors(authors)
                .categories(categories)
                .build();

        // when
        Book savedBook = bookRepository.save(book);

        // then
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Harry Potter and the Philosopher's Stone");
        assertThat(savedBook.getIsbn()).isEqualTo("9780747532743");
        assertThat(savedBook.getAuthors()).hasSize(1);
        assertThat(savedBook.getCategories()).hasSize(1);
    }

    @Test
    void shouldFindByTitleContainingIgnoreCase() {
        // given
        Book book1 = createBook("Harry Potter and the Philosopher's Stone", "9780747532743", 
                Set.of(author1), Set.of(category1));
        Book book2 = createBook("1984", "9780451524935", 
                Set.of(author2), Set.of(category2));
        Book book3 = createBook("Harry Potter and the Chamber of Secrets", "9780747538486", 
                Set.of(author1), Set.of(category1));

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        // when
        Page<Book> booksPage = bookRepository.findByTitleContainingIgnoreCase("harry", 
                PageRequest.of(0, 10));

        // then
        assertThat(booksPage.getTotalElements()).isEqualTo(2);
        assertThat(booksPage.getContent())
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Harry Potter and the Philosopher's Stone", 
                        "Harry Potter and the Chamber of Secrets");
    }

    @Test
    void shouldFindByIsbn() {
        // given
        Book book1 = createBook("Harry Potter", "9780747532743", 
                Set.of(author1), Set.of(category1));
        Book book2 = createBook("1984", "9780451524935", 
                Set.of(author2), Set.of(category2));

        bookRepository.save(book1);
        bookRepository.save(book2);

        // when
        Optional<Book> foundBook = bookRepository.findByIsbn("9780451524935");

        // then
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("1984");
        assertThat(foundBook.get().getIsbn()).isEqualTo("9780451524935");
    }

    @Test
    void shouldReturnEmptyWhenIsbnNotFound() {
        // given
        Book book = createBook("Harry Potter", "9780747532743", 
                Set.of(author1), Set.of(category1));
        bookRepository.save(book);

        // when
        Optional<Book> foundBook = bookRepository.findByIsbn("nonexistent");

        // then
        assertThat(foundBook).isEmpty();
    }

    @Test
    void shouldFindByAuthorNameContainingIgnoreCase() {
        // given
        Book book1 = createBook("Harry Potter and the Philosopher's Stone", "9780747532743", 
                Set.of(author1), Set.of(category1));
        Book book2 = createBook("1984", "9780451524935", 
                Set.of(author2), Set.of(category2));
        Book book3 = createBook("Harry Potter and the Chamber of Secrets", "9780747538486", 
                Set.of(author1), Set.of(category1));

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        // when
        Page<Book> booksPage = bookRepository.findByAuthorNameContainingIgnoreCase("rowling", 
                PageRequest.of(0, 10));

        // then
        assertThat(booksPage.getTotalElements()).isEqualTo(2);
        assertThat(booksPage.getContent())
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Harry Potter and the Philosopher's Stone", 
                        "Harry Potter and the Chamber of Secrets");
    }

    @Test
    void shouldFindByCategoryNameContainingIgnoreCase() {
        // given
        Book book1 = createBook("Harry Potter and the Philosopher's Stone", "9780747532743", 
                Set.of(author1), Set.of(category1));
        Book book2 = createBook("1984", "9780451524935", 
                Set.of(author2), Set.of(category2));
        Book book3 = createBook("Animal Farm", "9780452284241", 
                Set.of(author2), Set.of(category2));

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        // when
        Page<Book> booksPage = bookRepository.findByCategoryNameContainingIgnoreCase("dystopian", 
                PageRequest.of(0, 10));

        // then
        assertThat(booksPage.getTotalElements()).isEqualTo(2);
        assertThat(booksPage.getContent())
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("1984", "Animal Farm");
    }

    @Test
    void shouldSearchBooks() {
        // given
        Book book1 = createBook("Harry Potter and the Philosopher's Stone", "9780747532743", 
                Set.of(author1), Set.of(category1));
        Book book2 = createBook("1984", "9780451524935", 
                Set.of(author2), Set.of(category2));
        Book book3 = createBook("Animal Farm", "9780452284241", 
                Set.of(author2), Set.of(category2));

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        // when - search by title
        Page<Book> titleSearchPage = bookRepository.search("harry", PageRequest.of(0, 10));
        
        // then
        assertThat(titleSearchPage.getTotalElements()).isEqualTo(1);
        assertThat(titleSearchPage.getContent().getFirst().getTitle()).isEqualTo("Harry Potter and the Philosopher's Stone");

        // when - search by isbn
        Page<Book> isbnSearchPage = bookRepository.search("9780452284241", PageRequest.of(0, 10));
        
        // then
        assertThat(isbnSearchPage.getTotalElements()).isEqualTo(1);
        assertThat(isbnSearchPage.getContent().getFirst().getTitle()).isEqualTo("Animal Farm");
        
        // when - search by author
        Page<Book> authorSearchPage = bookRepository.search("orwell", PageRequest.of(0, 10));
        
        // then
        assertThat(authorSearchPage.getTotalElements()).isEqualTo(2);
        assertThat(authorSearchPage.getContent())
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("1984", "Animal Farm");
                
        // when - search by category
        Page<Book> categorySearchPage = bookRepository.search("fantasy", PageRequest.of(0, 10));
        
        // then
        assertThat(categorySearchPage.getTotalElements()).isEqualTo(1);
        assertThat(categorySearchPage.getContent().getFirst().getTitle()).isEqualTo("Harry Potter and the Philosopher's Stone");
    }

    @Test
    void shouldCheckIfBookExistsByIsbn() {
        // given
        Book book = createBook("Harry Potter", "9780747532743", 
                Set.of(author1), Set.of(category1));
        bookRepository.save(book);

        // when & then
        assertThat(bookRepository.existsByIsbn("9780747532743")).isTrue();
        assertThat(bookRepository.existsByIsbn("nonexistent")).isFalse();
    }

    // Helper method to create book entities
    private Book createBook(String title, String isbn, Set<Author> authors, Set<Category> categories) {
        return Book.builder()
                .title(title)
                .isbn(isbn)
                .publicationDate(LocalDate.now())
                .totalCopies(5)
                .availableCopies(5)
                .authors(authors)
                .categories(categories)
                .build();
    }
}
