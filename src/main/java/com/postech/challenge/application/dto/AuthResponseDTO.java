package com.postech.challenge.application.dto;

import java.util.UUID;

public record AuthResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType,
        UUID userId,
        String email,
        String name,
        String profile
) {
    public static AuthResponseDTO of(String accessToken, String refreshToken,
                                  UUID userId, String email, String name, String profile) {
        return new AuthResponseDTO(accessToken, refreshToken, "Bearer", userId, email, name, profile);
    }
}
