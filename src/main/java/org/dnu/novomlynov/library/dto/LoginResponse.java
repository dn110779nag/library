package org.dnu.novomlynov.library.dto;

public record LoginResponse(String token, String type) {

    public LoginResponse(String token) {
        this(token, "Bearer");
    }

}
