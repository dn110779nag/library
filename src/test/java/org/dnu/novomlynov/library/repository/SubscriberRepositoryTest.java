package org.dnu.novomlynov.library.repository;

import org.dnu.novomlynov.library.TestcontainersConfiguration;
import org.dnu.novomlynov.library.model.Subscriber;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({TestcontainersConfiguration.class})
@ActiveProfiles("test")
@Transactional
public class SubscriberRepositoryTest {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @AfterEach
    void clear() {
        subscriberRepository.deleteAll();
    }

    @Test
    void shouldSaveSubscriber() {
        // given
        Subscriber subscriber = Subscriber.builder()
                .name("John Smith")
                .email("john@example.com")
                .libraryCardNumber("LC12345")
                .active(true)
                .build();

        // when
        Subscriber savedSubscriber = subscriberRepository.save(subscriber);

        // then
        assertThat(savedSubscriber.getId()).isNotNull();
        assertThat(savedSubscriber.getName()).isEqualTo("John Smith");
        assertThat(savedSubscriber.getEmail()).isEqualTo("john@example.com");
        assertThat(savedSubscriber.getLibraryCardNumber()).isEqualTo("LC12345");
        assertThat(savedSubscriber.isActive()).isTrue();
    }

    @Test
    void shouldFindByNameContainingIgnoreCase() {
        // given
        subscriberRepository.saveAll(List.of(
                Subscriber.builder().name("John Smith").email("john@example.com").phoneNumber("1111")
                        .libraryCardNumber("LC001").active(true).build(),
                Subscriber.builder().name("Jane Doe").email("jane@example.com").phoneNumber("2222").libraryCardNumber("LC002")
                        .active(true).build(),
                Subscriber.builder().name("Richard Smith").email("richard@example.com").phoneNumber("3333")
                        .libraryCardNumber("LC003").active(true).build()));

        // when
        Page<Subscriber> subscribersPage = subscriberRepository.findByNameContainingIgnoreCase("smith",
                PageRequest.of(0, 10));
        List<Subscriber> subscribers = subscribersPage.getContent();

        // then
        assertThat(subscribersPage.getTotalElements()).isEqualTo(2);
        assertThat(subscribers).hasSize(2);
        assertThat(subscribers).extracting(Subscriber::getName)
                .containsExactlyInAnyOrder("John Smith", "Richard Smith");
    }

    @Test
    void shouldFindByLibraryCardNumber() {
        // given
        subscriberRepository.saveAll(List.of(
                Subscriber.builder().name("John Smith").email("john@example.com").phoneNumber("1111")
                        .libraryCardNumber("LC001").active(true).build(),
                Subscriber.builder().name("Jane Doe").email("jane@example.com").phoneNumber("2222").libraryCardNumber("LC002")
                        .active(true).build()));

        // when
        Optional<Subscriber> subscriber = subscriberRepository.findByLibraryCardNumber("LC002");

        // then
        assertThat(subscriber).isPresent();
        assertThat(subscriber.get().getName()).isEqualTo("Jane Doe");
        assertThat(subscriber.get().getLibraryCardNumber()).isEqualTo("LC002");
    }

    @Test
    void shouldReturnEmptyWhenLibraryCardNotFound() {
        // given
        subscriberRepository.saveAll(List.of(
                Subscriber.builder().name("John Smith").email("john@example.com").phoneNumber("1111")
                        .libraryCardNumber("LC001").active(true).build()));

        // when
        Optional<Subscriber> subscriber = subscriberRepository.findByLibraryCardNumber("NONEXISTENT");

        // then
        assertThat(subscriber).isEmpty();
    }

    @Test
    void shouldFindByActive() {
        // given
        subscriberRepository.saveAll(List.of(
                Subscriber.builder().name("John Smith").email("john@example.com").phoneNumber("1111")
                        .libraryCardNumber("LC001").active(true).build(),
                Subscriber.builder().name("Jane Doe").email("jane@example.com").phoneNumber("2222").libraryCardNumber("LC002")
                        .active(false).build(),
                Subscriber.builder().name("Richard Roe").email("richard@example.com").phoneNumber("3333")
                        .libraryCardNumber("LC003").active(true).build()));

        // when
        List<Subscriber> activeSubscribers = subscriberRepository.findByActive(true);
        List<Subscriber> inactiveSubscribers = subscriberRepository.findByActive(false);

        // then
        assertThat(activeSubscribers).hasSize(2);
        assertThat(activeSubscribers).extracting(Subscriber::getName).containsExactlyInAnyOrder("John Smith",
                "Richard Roe");

        assertThat(inactiveSubscribers).hasSize(1);
        assertThat(inactiveSubscribers.getFirst().getName()).isEqualTo("Jane Doe");
    }

    @Test
    void shouldCheckIfSubscriberExistsByLibraryCardNumber() {
        // given
        Subscriber subscriber = Subscriber.builder()
                .name("John Smith")
                .email("john@example.com")
                .phoneNumber("1234567890")
                .libraryCardNumber("LC12345")
                .active(true)
                .build();
        subscriberRepository.save(subscriber);

        // when & then
        assertThat(subscriberRepository.existsByLibraryCardNumber("LC12345")).isTrue();
        assertThat(subscriberRepository.existsByLibraryCardNumber("NONEXISTENT")).isFalse();
    }

    @Test
    void shouldCheckIfSubscriberExistsByEmail() {
        // given
        Subscriber subscriber = Subscriber.builder()
                .name("John Smith")
                .email("john@example.com")
                .phoneNumber("1234567890")
                .libraryCardNumber("LC12345")
                .active(true)
                .build();
        subscriberRepository.save(subscriber);

        // when & then
        assertThat(subscriberRepository.existsByEmail("john@example.com")).isTrue();
        assertThat(subscriberRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }
}