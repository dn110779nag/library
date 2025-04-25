package org.dnu.novomlynov.library.repository;

import org.dnu.novomlynov.library.TestcontainersConfiguration;
import org.dnu.novomlynov.library.model.*;
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
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({TestcontainersConfiguration.class})
@ActiveProfiles("test")
@Transactional
public class BookLendingRepositoryTest {

    @Autowired
    private BookLendingRepository bookLendingRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Book book1;
    private Book book2;
    private Subscriber subscriber1;
    private Subscriber subscriber2;

    @BeforeEach
    void setup() {
        // Clear any previous test data
        bookLendingRepository.deleteAll();
        bookRepository.deleteAll();
        subscriberRepository.deleteAll();
        authorRepository.deleteAll();
        categoryRepository.deleteAll();

        // Create test authors and categories
        Author author = authorRepository.save(Author.builder().name("Test Author").build());
        Category category = categoryRepository.save(Category.builder().name("Test Category").build());

        // Create test books
        book1 = bookRepository.save(Book.builder()
                .title("Book 1")
                .isbn("1234567890")
                .publicationDate(LocalDate.now())
                .totalCopies(5)
                .availableCopies(3)
                .authors(Set.of(author))
                .categories(Set.of(category))
                .build());

        book2 = bookRepository.save(Book.builder()
                .title("Book 2")
                .isbn("0987654321")
                .publicationDate(LocalDate.now())
                .totalCopies(3)
                .availableCopies(2)
                .authors(Set.of(author))
                .categories(Set.of(category))
                .build());

        // Create test subscribers
        subscriber1 = subscriberRepository.save(Subscriber.builder()
                .name("John Doe")
                .email("john@example.com")
                .libraryCardNumber("LC001")
                .active(true)
                .build());

        subscriber2 = subscriberRepository.save(Subscriber.builder()
                .name("Jane Smith")
                .email("jane@example.com")
                .libraryCardNumber("LC002")
                .active(true)
                .build());
    }

    @Test
    void shouldSaveBookLending() {
        // given
        BookLending bookLending = BookLending.builder()
                .book(book1)
                .subscriber(subscriber1)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(LendingStatus.ISSUED)
                .build();

        // when
        BookLending savedBookLending = bookLendingRepository.save(bookLending);

        // then
        assertThat(savedBookLending.getId()).isNotNull();
        assertThat(savedBookLending.getBook().getTitle()).isEqualTo("Book 1");
        assertThat(savedBookLending.getSubscriber().getName()).isEqualTo("John Doe");
        assertThat(savedBookLending.getStatus()).isEqualTo(LendingStatus.ISSUED);
    }

    @Test
    void shouldFindBySubscriberId() {
        // given
        bookLendingRepository.saveAll(List.of(
                BookLending.builder()
                        .book(book1)
                        .subscriber(subscriber1)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(14))
                        .status(LendingStatus.ISSUED)
                        .build(),
                BookLending.builder()
                        .book(book2)
                        .subscriber(subscriber1)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(14))
                        .status(LendingStatus.ISSUED)
                        .build(),
                BookLending.builder()
                        .book(book1)
                        .subscriber(subscriber2)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(14))
                        .status(LendingStatus.ISSUED)
                        .build()));

        // when
        List<BookLending> subscriber1Lendings = bookLendingRepository.findBySubscriberId(subscriber1.getId());
        List<BookLending> subscriber2Lendings = bookLendingRepository.findBySubscriberId(subscriber2.getId());

        // then
        assertThat(subscriber1Lendings).hasSize(2);
        assertThat(subscriber1Lendings).extracting(lending -> lending.getSubscriber().getId())
                .containsOnly(subscriber1.getId());

        assertThat(subscriber2Lendings).hasSize(1);
        assertThat(subscriber2Lendings).extracting(lending -> lending.getSubscriber().getId())
                .containsOnly(subscriber2.getId());
    }

    @Test
    void shouldFindBySubscriberIdPaginated() {
        // given
        for (int i = 0; i < 15; i++) {
            bookLendingRepository.save(BookLending.builder()
                    .book(i % 2 == 0 ? book1 : book2)
                    .subscriber(subscriber1)
                    .issueDate(LocalDate.now())
                    .dueDate(LocalDate.now().plusDays(14))
                    .status(LendingStatus.ISSUED)
                    .build());
        }

        // when
        Page<BookLending> page1 = bookLendingRepository.findBySubscriberId(
                subscriber1.getId(), PageRequest.of(0, 10));
        Page<BookLending> page2 = bookLendingRepository.findBySubscriberId(
                subscriber1.getId(), PageRequest.of(1, 10));

        // then
        assertThat(page1.getContent()).hasSize(10);
        assertThat(page1.getTotalElements()).isEqualTo(15);
        assertThat(page1.getTotalPages()).isEqualTo(2);

        assertThat(page2.getContent()).hasSize(5);
        assertThat(page2.getTotalElements()).isEqualTo(15);
        assertThat(page2.getTotalPages()).isEqualTo(2);
    }

    @Test
    void shouldFindByBookIdAndStatus() {
        // given
        bookLendingRepository.saveAll(List.of(
                BookLending.builder()
                        .book(book1)
                        .subscriber(subscriber1)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(14))
                        .status(LendingStatus.ISSUED)
                        .build(),
                BookLending.builder()
                        .book(book1)
                        .subscriber(subscriber2)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().minusDays(6))
                        .status(LendingStatus.RETURNED)
                        .returnDate(LocalDate.now().minusDays(7))
                        .build(),
                BookLending.builder()
                        .book(book2)
                        .subscriber(subscriber1)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(14))
                        .status(LendingStatus.ISSUED)
                        .build()));

        // when
        List<BookLending> book1IssuedLendings = bookLendingRepository.findByBookIdAndStatus(
                book1.getId(), LendingStatus.ISSUED);
        List<BookLending> book1ReturnedLendings = bookLendingRepository.findByBookIdAndStatus(
                book1.getId(), LendingStatus.RETURNED);

        // then
        assertThat(book1IssuedLendings).hasSize(1);
        assertThat(book1IssuedLendings.getFirst().getBook().getId()).isEqualTo(book1.getId());
        assertThat(book1IssuedLendings.getFirst().getStatus()).isEqualTo(LendingStatus.ISSUED);

        assertThat(book1ReturnedLendings).hasSize(1);
        assertThat(book1ReturnedLendings.getFirst().getBook().getId()).isEqualTo(book1.getId());
        assertThat(book1ReturnedLendings.getFirst().getStatus()).isEqualTo(LendingStatus.RETURNED);
    }

    @Test
    void shouldFindByStatus() {
        // given
        bookLendingRepository.saveAll(List.of(
                BookLending.builder()
                        .book(book1)
                        .subscriber(subscriber1)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(14))
                        .status(LendingStatus.ISSUED)
                        .build(),
                BookLending.builder()
                        .book(book2)
                        .subscriber(subscriber2)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(14))
                        .status(LendingStatus.ISSUED)
                        .build(),
                BookLending.builder()
                        .book(book1)
                        .subscriber(subscriber2)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().minusDays(6))
                        .status(LendingStatus.RETURNED)
                        .returnDate(LocalDate.now().minusDays(7))
                        .build()));

        // when
        Page<BookLending> issuedLendings = bookLendingRepository.findByStatus(
                LendingStatus.ISSUED, PageRequest.of(0, 10));
        Page<BookLending> returnedLendings = bookLendingRepository.findByStatus(
                LendingStatus.RETURNED, PageRequest.of(0, 10));

        // then
        assertThat(issuedLendings.getTotalElements()).isEqualTo(2);
        assertThat(issuedLendings.getContent()).extracting(BookLending::getStatus)
                .containsOnly(LendingStatus.ISSUED);

        assertThat(returnedLendings.getTotalElements()).isEqualTo(1);
        assertThat(returnedLendings.getContent()).extracting(BookLending::getStatus)
                .containsOnly(LendingStatus.RETURNED);
    }

    @Test
    void shouldFindBySubscriberIdAndStatus() {
        // given
        bookLendingRepository.saveAll(List.of(
                BookLending.builder()
                        .book(book1)
                        .subscriber(subscriber1)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(14))
                        .status(LendingStatus.ISSUED)
                        .build(),
                BookLending.builder()
                        .book(book2)
                        .subscriber(subscriber1)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().minusDays(6))
                        .status(LendingStatus.RETURNED)
                        .returnDate(LocalDate.now().minusDays(7))
                        .build(),
                BookLending.builder()
                        .book(book1)
                        .subscriber(subscriber2)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(14))
                        .status(LendingStatus.ISSUED)
                        .build()));

        // when
        Page<BookLending> subscriber1IssuedLendings = bookLendingRepository.findBySubscriberIdAndStatus(
                subscriber1.getId(), LendingStatus.ISSUED, PageRequest.of(0, 10));

        // then
        assertThat(subscriber1IssuedLendings.getTotalElements()).isEqualTo(1);
        assertThat(subscriber1IssuedLendings.getContent().getFirst().getSubscriber().getId())
                .isEqualTo(subscriber1.getId());
        assertThat(subscriber1IssuedLendings.getContent().getFirst().getStatus()).isEqualTo(LendingStatus.ISSUED);
    }

    @Test
    void shouldFindOverdueBooks() {
        // given
        LocalDate today = LocalDate.now();

        bookLendingRepository.saveAll(List.of(
                BookLending.builder()
                        .book(book1)
                        .subscriber(subscriber1)
                        .issueDate(LocalDate.now())
                        .dueDate(today.minusDays(6)) // overdue
                        .status(LendingStatus.ISSUED)
                        .build(),
                BookLending.builder()
                        .book(book2)
                        .subscriber(subscriber2)
                        .issueDate(LocalDate.now())
                        .dueDate(today.plusDays(7)) // not overdue
                        .status(LendingStatus.ISSUED)
                        .build(),
                BookLending.builder()
                        .book(book1)
                        .subscriber(subscriber2)
                        .issueDate(LocalDate.now())
                        .dueDate(today.minusDays(16)) // overdue but returned
                        .status(LendingStatus.RETURNED)
                        .returnDate(today.minusDays(15))
                        .build()));

        // when
        List<BookLending> overdueBooks = bookLendingRepository.findOverdueBooks(today);

        // then
        assertThat(overdueBooks).hasSize(1);
        assertThat(overdueBooks.getFirst().getStatus()).isEqualTo(LendingStatus.ISSUED);
        assertThat(overdueBooks.getFirst().getDueDate()).isBefore(today);
    }

    @Test
    void shouldCountCurrentBorrowingsForSubscriber() {
        // given
        bookLendingRepository.saveAll(List.of(
                BookLending.builder()
                        .book(book1)
                        .subscriber(subscriber1)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(4))
                        .status(LendingStatus.ISSUED)
                        .build(),
                BookLending.builder()
                        .book(book2)
                        .subscriber(subscriber1)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(9))
                        .status(LendingStatus.ISSUED)
                        .build(),
                BookLending.builder()
                        .book(book1)
                        .subscriber(subscriber1)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().minusDays(16))
                        .status(LendingStatus.RETURNED)
                        .returnDate(LocalDate.now().minusDays(18))
                        .build(),
                BookLending.builder()
                        .book(book2)
                        .subscriber(subscriber2)
                        .issueDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(14))
                        .status(LendingStatus.ISSUED)
                        .build()));

        // when
        long subscriber1Borrowings = bookLendingRepository.countCurrentBorrowingsForSubscriber(subscriber1.getId());
        long subscriber2Borrowings = bookLendingRepository.countCurrentBorrowingsForSubscriber(subscriber2.getId());

        // then
        assertThat(subscriber1Borrowings).isEqualTo(2);
        assertThat(subscriber2Borrowings).isEqualTo(1);
    }
}