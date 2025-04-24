package org.dnu.novomlynov.library.repository;

import org.dnu.novomlynov.library.TestcontainersConfiguration;
import org.dnu.novomlynov.library.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUser() {
        // given
        User user = User.builder()
                .login("testuser")
                .password("password123")
                .build();

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getLogin()).isEqualTo("testuser");
        assertThat(savedUser.getPassword()).isEqualTo("password123");
    }

    @Test
    void shouldFindUserByLogin() {
        // given
        User user = User.builder()
                .login("johndoe")
                .password("secure123")
                .build();
        userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findByLogin("johndoe");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getLogin()).isEqualTo("johndoe");
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
                .password("secure456")
                .build();
        userRepository.save(user);

        // when & then
        assertThat(userRepository.existsByLogin("janedoe")).isTrue();
        assertThat(userRepository.existsByLogin("nonexistent")).isFalse();
    }

    @Test
    void shouldFindAllUsers() {
        // given
        User user1 = User.builder().login("user1").password("pass1").build();
        User user2 = User.builder().login("user2").password("pass2").build();
        userRepository.saveAll(List.of(user1, user2));

        // when
        List<User> users = userRepository.findAll();

        // then
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getLogin).containsExactlyInAnyOrder("user1", "user2");
    }
}