package org.dnu.novomlynov.library.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dnu.novomlynov.library.dto.UserCreateDto;
import org.dnu.novomlynov.library.dto.UserDto;
import org.dnu.novomlynov.library.dto.UserUpdateDto;
import org.dnu.novomlynov.library.model.UserRole;
import org.dnu.novomlynov.library.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        return new ResponseEntity<>(userService.createUser(userCreateDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> allUsers = userService.getAllUsers();
        log.info("All users: {}", allUsers);
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable UserRole role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("Update user: {}", userUpdateDto);
        return ResponseEntity.ok(userService.updateUser(id, userUpdateDto));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> changeUserActivity(
            @PathVariable Long id,
            @RequestParam boolean active) {
        userService.changeUserActivity(id, active);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}