package org.dnu.novomlynov.library.controller;

import lombok.extern.slf4j.Slf4j;
import org.dnu.novomlynov.library.TestcontainersConfiguration;
import org.dnu.novomlynov.library.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class})
@Slf4j
public class AuthControllerE2eTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnUnauthorizedForInvalidLogin() {
        String url = "http://localhost:" + port + "/api/auth/login";
        String body = "{\"username\":\"invalid\",\"password\":\"wrong\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid credentials"));
    }

    @Test
    void shouldAuthorize() throws IOException {
        String url = "http://localhost:" + port + "/api/auth/login";
        String body = "{\"username\":\"admin\",\"password\":\"admin\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.info("Response body: {}", response.getBody());
        Map map = new ObjectMapper().readValue(response.getBody(), Map.class);
        assertThat(map.get("type")).isEqualTo("Bearer");
        assertThat(map.get("token")).isNotNull();
    }
}
