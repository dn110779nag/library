package org.dnu.novomlynov.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dnu.novomlynov.library.model.UserRole;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String login;
    private Set<UserRole> role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}