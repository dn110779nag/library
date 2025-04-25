package org.dnu.novomlynov.library.repository;

import org.dnu.novomlynov.library.TestcontainersConfiguration;
import org.dnu.novomlynov.library.model.User;
import org.dnu.novomlynov.library.model.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({TestcontainersConfiguration.class})
@ActiveProfiles("test")
@Transactional
public class UserRepositoryTest {

    @AfterEach
    void clear() {
        userRepository.deleteAll();
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUser() {
        // given
        User user = User.builder()
                .login("testuser")
                .roles(Set.of(UserRole.USER_ADMIN)) // Add role
                .active(true)
                .build();

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getLogin()).isEqualTo("testuser");
        assertThat(savedUser.getRoles()).containsOnly(UserRole.USER_ADMIN);
    }

    @Test
    void shouldFindUserByLogin() {
        // given
        User user = User.builder()
                .login("johndoe")
                .roles(Set.of(UserRole.BOOK_ADMIN)) // Add role
                .active(true)
                .build();
        userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findByLogin("johndoe");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getLogin()).isEqualTo("johndoe");
        assertThat(foundUser.get().getRoles()).containsOnly(UserRole.BOOK_ADMIN);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        // when
        Optional<User> foundUser = userRepository.findByLogin("nonexistent");

        // then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void shouldCheckIfUserExistsByLogin() {
        // given
        User user = User.builder()
                .login("janedoe")
                .roles(Set.of(UserRole.LIBRARIAN)) // Add role
                .active(true)
                .build();
        userRepository.save(user);

        // when & then
        assertThat(userRepository.existsByLogin("janedoe")).isTrue();
        assertThat(userRepository.existsByLogin("nonexistent")).isFalse();
    }

    @Test
    void shouldFindAllUsers() {
        // given
        User user1 = User.builder()
                .login("user1")
                .roles(Set.of(UserRole.USER_ADMIN)) // Add role
                .active(true)
                .build();
        User user2 = User.builder()
                .login("user2")
                .roles(Set.of(UserRole.BOOK_ADMIN)) // Add role
                .active(true)
                .build();
        userRepository.saveAll(List.of(user1, user2));

        // when
        List<User> users = userRepository.findAll();

        // then
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getLogin).containsExactlyInAnyOrder("user1", "user2");
    }
}