package org.dnu.novomlynov.library.service.impl;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.dnu.novomlynov.library.dto.UserCreateDto;
import org.dnu.novomlynov.library.dto.UserDto;
import org.dnu.novomlynov.library.dto.UserUpdateDto;
import org.dnu.novomlynov.library.exception.ResourceNotFoundException;
import org.dnu.novomlynov.library.model.User;
import org.dnu.novomlynov.library.model.UserPassword;
import org.dnu.novomlynov.library.model.UserRole;
import org.dnu.novomlynov.library.repository.UserPasswordRepository;
import org.dnu.novomlynov.library.repository.UserRepository;
import org.dnu.novomlynov.library.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPasswordRepository userPasswordRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public UserDto createUser(UserCreateDto userCreateDto) {
        if (userRepository.existsByLogin(userCreateDto.getLogin())) {
            throw new IllegalArgumentException("User with this login already exists");
        }

        User user = User.builder()
                .login(userCreateDto.getLogin())
                .roles(userCreateDto.getRoles())
                .active(true)
                .userName(userCreateDto.getUserName())
                .build();

        User savedUser = userRepository.save(user);
        userPasswordRepository.save(UserPassword.builder()
                .userId(savedUser.getId())
                .passwordHash(passwordEncoder.encode(userCreateDto.getPassword()))
                .build());

        return mapToDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with login: " + login));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (StringUtils.isNotBlank(userUpdateDto.getUserName())) {
            user.setUserName(userUpdateDto.getUserName());
        }

        if (userUpdateDto.getRoles() != null) {
            user.setRoles(userUpdateDto.getRoles());
        }

        if (userUpdateDto.getActive() != null) {
            user.setActive(userUpdateDto.getActive());
        }

        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    @Override
    @Transactional
    public void changeUserActivity(Long id, boolean isActive) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setActive(isActive);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
                .map(this::mapToUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + username));
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .login(user.getLogin())
                .role(user.getRoles())
                .userName(user.getUserName())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private UserDetails mapToUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLogin())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toList()))
                .disabled(user.isActive())
                .password(userPasswordRepository.findById(user.getId())
                        .map(UserPassword::getPasswordHash)
                        .orElseThrow(() -> new ResourceNotFoundException("Password not found for user with id: " + user.getId())))
                .build();
    }
}