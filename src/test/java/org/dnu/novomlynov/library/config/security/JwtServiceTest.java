package org.dnu.novomlynov.library.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {
    private JwtService jwtService;
    private JwtConfigurationProperties jwtProperties;
    private final String secret = "01234567890123456789012345678901"; // 32 chars for HS256
    private final long expiration = 1000 * 60 * 60; // 1 hour

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtConfigurationProperties();
        jwtProperties.setSecret(secret);
        jwtProperties.setExpiration(expiration);
        jwtService = new JwtService(jwtProperties);
    }

    @Test
    void testGenerateAndValidateToken() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void testExtractUsername() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void testExtractExpiration() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtService.generateToken(userDetails);
        Date expirationDate = jwtService.extractExpiration(token);
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalid.token.value";
        assertFalse(jwtService.validateToken(invalidToken));
    }

    @Test
    void testValidateToken_ExpiredToken() throws InterruptedException {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("expireduser");
        // Set expiration to 0 (already expired)
        jwtProperties.setExpiration(1);
        String token = jwtService.generateToken(userDetails);
        Thread.sleep(2);
        assertFalse(jwtService.validateToken(token, userDetails));
        assertFalse(jwtService.validateToken(token));
    }
}
