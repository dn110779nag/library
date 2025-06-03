package org.dnu.novomlynov.library.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt")
@Component
@lombok.Data
public class JwtConfigurationProperties {
    private String secret;
    private long expiration;
}
