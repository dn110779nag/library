package org.dnu.novomlynov.library.service;

import org.dnu.novomlynov.library.dto.LoginRequest;
import org.dnu.novomlynov.library.dto.UserDto;

public interface AuthService {
    UserDto login(LoginRequest loginRequest);

    UserDto getCurrentUser();
}