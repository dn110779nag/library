package org.dnu.novomlynov.library.service;

import org.dnu.novomlynov.library.dto.UserCreateDto;
import org.dnu.novomlynov.library.dto.UserDto;
import org.dnu.novomlynov.library.dto.UserUpdateDto;
import org.dnu.novomlynov.library.model.UserRole;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserCreateDto userCreateDto);

    UserDto getUserById(Long id);

    UserDto getUserByLogin(String login);

    List<UserDto> getAllUsers();

    List<UserDto> getUsersByRole(UserRole role);

    UserDto updateUser(Long id, UserUpdateDto userUpdateDto);

    void changeUserActivity(Long id, boolean isActive);

    void deleteUser(Long id);

    boolean existsByLogin(String login);
}