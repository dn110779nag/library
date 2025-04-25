package org.dnu.novomlynov.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dnu.novomlynov.library.model.UserRole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    private String password;
    private UserRole role;
    private Boolean active;
}