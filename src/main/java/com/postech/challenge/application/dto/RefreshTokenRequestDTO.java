package com.postech.challenge.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token de renovação da sessão")
public record RefreshTokenRequestDTO(

        @Schema(description = "Refresh token retornado no login ou register", example = "eyJhbGciOiJIUzI1NiJ9...")
        String refreshToken
) {
}
