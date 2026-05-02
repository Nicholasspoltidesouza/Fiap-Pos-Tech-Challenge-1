package com.postech.challenge.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.postech.challenge.domain.model.PerfilUsuario;

class CustomUserDetailsTest {

    @Test
    void shouldExposeUserDataAndAuthorities() {
        UUID id = UUID.randomUUID();
        CustomUserDetails userDetails = new CustomUserDetails(
                id,
                "Admin User",
                "admin@oficina.com",
                "senha-criptografada",
                PerfilUsuario.ADMIN);

        assertEquals(id, userDetails.getId());
        assertEquals("Admin User", userDetails.getNome());
        assertEquals("admin@oficina.com", userDetails.getUsername());
        assertEquals("senha-criptografada", userDetails.getPassword());
        assertEquals(PerfilUsuario.ADMIN, userDetails.getPerfil());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())));
    }
}
