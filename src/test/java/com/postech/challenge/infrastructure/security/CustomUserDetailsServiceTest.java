package com.postech.challenge.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.postech.challenge.domain.model.PerfilUsuario;
import com.postech.challenge.infrastructure.persistence.entity.UsuarioEntity;
import com.postech.challenge.infrastructure.persistence.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldLoadUserByUsername() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Mecanico");
        usuario.setEmail("mecanico@oficina.com");
        usuario.setSenha("senha");
        usuario.setPerfil(PerfilUsuario.MECANICO);

        when(usuarioRepository.findByEmail("mecanico@oficina.com")).thenReturn(Optional.of(usuario));

        CustomUserDetails userDetails = assertInstanceOf(
                CustomUserDetails.class,
                customUserDetailsService.loadUserByUsername("mecanico@oficina.com"));

        assertEquals("mecanico@oficina.com", userDetails.getUsername());
        assertEquals(PerfilUsuario.MECANICO, userDetails.getPerfil());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(usuarioRepository.findByEmail("missing@oficina.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("missing@oficina.com"));

        assertEquals("User not found with email: missing@oficina.com", exception.getMessage());
    }
}
