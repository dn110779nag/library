package org.dnu.novomlynov.library.repository;

import org.dnu.novomlynov.library.TestcontainersConfiguration;
import org.dnu.novomlynov.library.model.UserPassword;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest
@Import({TestcontainersConfiguration.class})
@ActiveProfiles("test")
@Transactional
class UserPasswordRepositoryTest {

    @Autowired
    private UserPasswordRepository sut;

    @AfterEach
    void clear(){
        sut.deleteAll();
    }

    @Test
    @Transactional
    void create(){
        UserPassword userPassword = sut.save(UserPassword.builder()
                .userId(1L)
                .passwordHash("123")
                .build());

        assertThat(sut.findById(userPassword.getUserId()))
                .isPresent()
                .get()
                .satisfies(p -> {
                    assertThat(p.getCreatedAt()).isNotNull();
                    assertThat(p.getUpdatedAt()).isNotNull();
                    assertThat(p.getPasswordHash()).isEqualTo("123");
                });
    }

    @Test
    @Transactional
    void create_modify() throws InterruptedException {


        UserPassword userPassword = sut.save(UserPassword.builder()
                .userId(1L)
                .passwordHash("123")
                .build());

        assertThat(sut.findById(userPassword.getUserId()))
                .isPresent()
                .get()
                .satisfies(p -> {
                    assertThat(p.getCreatedAt()).isNotNull();
                    assertThat(p.getUpdatedAt()).isNotNull();
                    assertThat(p.getPasswordHash()).isEqualTo("123");
                });
        userPassword  = sut.findById(userPassword.getUserId()).orElseThrow();
        userPassword.setPasswordHash("456");
        sut.saveAndFlush(userPassword);


        assertThat(sut.findById(userPassword.getUserId()))
                .isPresent()
                .get()
                .satisfies(p -> {
                    assertThat(p.getCreatedAt()).isNotNull();
                    assertThat(p.getUpdatedAt()).isNotNull();
                    assertThat(p.getUpdatedAt()).isAfter(p.getCreatedAt());
                    assertThat(p.getPasswordHash()).isEqualTo("456");
                });
    }
}