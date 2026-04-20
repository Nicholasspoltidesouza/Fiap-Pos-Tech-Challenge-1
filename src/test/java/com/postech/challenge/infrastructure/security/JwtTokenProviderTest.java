package com.postech.challenge.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Jwts;

class JwtTokenProviderTest {

    private static final String SECRET = "1234567890123456789012345678901234567890123456789012345678901234";

    @Test
    void shouldGenerateAndExtractAccessTokenData() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 3600000, 7200000);
        UUID userId = UUID.randomUUID();

        String token = provider.generateAccessToken(userId, "admin@oficina.com", "ADMIN");

        assertTrue(provider.isTokenValid(token));
        assertEquals("admin@oficina.com", provider.extractEmail(token));
        assertEquals(userId, provider.extractUserId(token));
        assertEquals("ADMIN", provider.extractProfile(token));
        assertEquals("ACCESS", provider.extractTokenType(token));
    }

    @Test
    void shouldGenerateAndExtractRefreshTokenType() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 3600000, 7200000);
        UUID userId = UUID.randomUUID();

        String token = provider.generateRefreshToken(userId, "atendente@oficina.com", "ATENDENTE");

        assertTrue(provider.isTokenValid(token));
        assertEquals("REFRESH", provider.extractTokenType(token));
    }

    @Test
    void shouldReturnFalseWhenTokenIsInvalid() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 3600000, 7200000);

        assertFalse(provider.isTokenValid("token-invalido"));
        assertFalse(provider.isTokenValid(""));
    }

    @Test
    void shouldReturnFalseWhenTokenIsExpired() {
        JwtTokenProvider providerWithExpiredAccess = new JwtTokenProvider(SECRET, -1000, 7200000);
        String expiredToken = providerWithExpiredAccess.generateAccessToken(
                UUID.randomUUID(),
                "admin@oficina.com",
                "ADMIN");

        assertFalse(providerWithExpiredAccess.isTokenValid(expiredToken));
    }

    @Test
    void shouldReturnFalseWhenTokenIsUnsupported() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 3600000, 7200000);
        String unsecuredToken = Jwts.builder()
                .subject("admin@oficina.com")
                .compact();

        assertFalse(provider.isTokenValid(unsecuredToken));
    }
}
