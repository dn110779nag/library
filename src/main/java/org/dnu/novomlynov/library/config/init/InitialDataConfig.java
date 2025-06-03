package org.dnu.novomlynov.library.config.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dnu.novomlynov.library.dto.UserCreateDto;
import org.dnu.novomlynov.library.model.UserRole;
import org.dnu.novomlynov.library.repository.UserRepository;
import org.dnu.novomlynov.library.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
@Slf4j
public class InitialDataConfig {

    private final UserRepository userRepository;
    private final UserService userService;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            if (userRepository.count() == 0) {
                userService.createUser(UserCreateDto.builder()
                        .login("admin")
                        .userName("Administrator")
                        .password("admin")
                        .roles(Stream.of(UserRole.values()).collect(Collectors.toSet()))
                        .build());

                log.info("Created initial admin user with login: admin and password: admin");
                log.info("IMPORTANT: Please change the admin password after first login!");
            }
        };
    }
}