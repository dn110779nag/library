package org.dnu.novomlynov.library.config.security;

import lombok.RequiredArgsConstructor;
import org.dnu.novomlynov.library.model.UserRole;
import org.dnu.novomlynov.library.repository.UserPasswordRepository;
import org.dnu.novomlynov.library.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LibUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserPasswordRepository userPasswordRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
                .map(u -> LibUserDetails.builder()
                        .username(u.getLogin())
                        .enabled(u.isActive())
                        .authorities(u.getRoles().stream().map(this::convertToAuthority).toList())
                        .password(userPasswordRepository.findById(u.getId()).orElseThrow().getPasswordHash())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private SimpleGrantedAuthority convertToAuthority(UserRole role) {
        return new SimpleGrantedAuthority("ROLE_" + role);
    }
}
