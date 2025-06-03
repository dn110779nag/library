package org.dnu.novomlynov.library.controller;

import org.dnu.novomlynov.library.config.security.JwtService;
import org.dnu.novomlynov.library.config.security.LibUserDetailsService;
import org.dnu.novomlynov.library.config.security.PasswordConfig;
import org.dnu.novomlynov.library.config.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, PasswordConfig.class})
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private LibUserDetailsService libUserDetailsService;

    @Test
    void shouldReturnUnauthorizedForInvalidLogin() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Invalid login"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"invalid\",\"password\":\"wrong\"}"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(content().string("Invalid credentials!"));
    }

    @Test
    void shouldReturnOkForValidLogin() throws Exception {
        // Mock successful authentication and token generation
        var authentication = mock(org.springframework.security.core.Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        var userDetails = mock(org.springframework.security.core.userdetails.UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("mocked-jwt-token");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"valid\",\"password\":\"correct\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"token\":\"mocked-jwt-token\",\"type\":\"Bearer\"}"));
    }
}
