package org.dnu.novomlynov.library.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dnu.novomlynov.library.model.User;
import org.dnu.novomlynov.library.model.UserRole;
import org.dnu.novomlynov.library.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
@Slf4j
public class InitialDataConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            // Create an initial admin user if no users exist
            if (userRepository.count() == 0) {
                User adminUser = User.builder()
                        .login("admin")
                        .userName("Admin")
                        .roles(Set.of(UserRole.USER_ADMIN))
                        .active(true)
                        .build();

                userRepository.save(adminUser);

                log.info("Created initial admin user with login: admin and password: admin");
                log.info("IMPORTANT: Please change the admin password after first login!");
            }
        };
    }
}