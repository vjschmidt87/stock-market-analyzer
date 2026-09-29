package com.portfolio.stockanalyzer.dto.response;

public record AuthResponse(
        String token,
        String email,
        String displayName,
        String tokenType
) {
    public AuthResponse(String token, String email, String displayName) {
        this(token, email, displayName, "Bearer");
    }
}
