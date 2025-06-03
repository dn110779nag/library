package org.dnu.novomlynov.library.service;

import org.dnu.novomlynov.library.TestcontainersConfiguration;
import org.dnu.novomlynov.library.dto.UserCreateDto;
import org.dnu.novomlynov.library.dto.UserDto;
import org.dnu.novomlynov.library.dto.UserUpdateDto;
import org.dnu.novomlynov.library.model.User;
import org.dnu.novomlynov.library.model.UserRole;
import org.dnu.novomlynov.library.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import({TestcontainersConfiguration.class})
@ActiveProfiles("test")
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private Long createdUserId;
    private final String testLogin = "testuser";
    private final String testPassword = "password";
    private final String testUserName = "Test User";

    @BeforeEach
    void setUp() {
        if (!userService.existsByLogin(testLogin)) {
            UserCreateDto dto = UserCreateDto.builder()
                    .login(testLogin)
                    .password(testPassword)
                    .userName(testUserName)
                    .roles(Set.of(UserRole.USER_ADMIN))
                    .build();
            UserDto user = userService.createUser(dto);
            createdUserId = user.getId();
        } else {
            createdUserId = userService.getUserByLogin(testLogin).getId();
        }
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser() {
        String login = "newuser";
        UserCreateDto dto = UserCreateDto.builder()
                .login(login)
                .password("newpass")
                .userName("New User")
                .roles(Set.of(UserRole.USER_ADMIN))
                .build();
        UserDto user = userService.createUser(dto);
        assertNotNull(user.getId());
        assertEquals(login, user.getLogin());
    }

    @Test
    void testGetUserById() {
        UserDto user = userService.getUserById(createdUserId);
        assertEquals(testLogin, user.getLogin());
    }

    @Test
    void testGetUserByLogin() {
        UserDto user = userService.getUserByLogin(testLogin);
        assertEquals(createdUserId, user.getId());
    }

    @Test
    void testGetAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        assertFalse(users.isEmpty());
    }

    @Test
    void testGetUsersByRole() {
        List<UserDto> users = userService.getUsersByRole(UserRole.USER_ADMIN);
        assertTrue(users.stream().anyMatch(u -> u.getLogin().equals(testLogin)));
    }

    @Test
    void testUpdateUser() {
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .roles(Set.of(UserRole.BOOK_ADMIN, UserRole.LIBRARIAN))
                .active(true)
                .userName("Updated Name")
                .build();
        UserDto updated = userService.updateUser(createdUserId, updateDto);
        Optional<User> result = userRepository.findById(createdUserId);
        assertThat(result).isPresent()
                        .get()
                .satisfies(
                user -> {
                    assertThat(user.getUserName()).isEqualTo("Updated Name");
                    assertThat(user.isActive()).isTrue();
                    assertThat(user.getRoles()).containsExactlyInAnyOrder(
                            UserRole.BOOK_ADMIN, UserRole.LIBRARIAN);
                }
        );

    }

    @Test
    void testChangeUserActivity() {
        userService.changeUserActivity(createdUserId, false);
        UserDto user = userService.getUserById(createdUserId);
        assertThat(user.isActive()).isFalse();
        userService.changeUserActivity(createdUserId, true);
        UserDto result = userService.getUserById(createdUserId);
        assertThat(result.isActive()).isTrue();
    }
}
