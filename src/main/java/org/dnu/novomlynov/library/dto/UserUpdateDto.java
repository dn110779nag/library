package org.dnu.novomlynov.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dnu.novomlynov.library.model.UserRole;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    private Set<UserRole> roles;
    private Boolean active;
    private String userName;
}