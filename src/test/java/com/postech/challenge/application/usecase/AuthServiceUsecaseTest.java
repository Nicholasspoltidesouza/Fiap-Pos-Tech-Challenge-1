package com.postech.challenge.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.postech.challenge.application.dto.AuthResponseDTO;
import com.postech.challenge.application.dto.LoginRequestDTO;
import com.postech.challenge.application.dto.RegisterRequestDTO;
import com.postech.challenge.infrastructure.persistence.entity.PerfilUsuario;
import com.postech.challenge.infrastructure.persistence.entity.UsuarioEntity;
import com.postech.challenge.infrastructure.persistence.repository.UsuarioRepository;
import com.postech.challenge.infrastructure.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
class AuthServiceUsecaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceUsecase authServiceUsecase;

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequestDTO request = new RegisterRequestDTO("Admin User", "admin@oficina.com", "senha123", "ADMIN");
        UsuarioEntity usuarioSalvo = buildUsuario(UUID.randomUUID(), "Admin User", "admin@oficina.com", "hashed", PerfilUsuario.ADMIN);

        when(usuarioRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashed");
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioSalvo);
        when(jwtTokenProvider.generateAccessToken(usuarioSalvo.getId(), usuarioSalvo.getEmail(), usuarioSalvo.getPerfil().name()))
                .thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(usuarioSalvo.getId(), usuarioSalvo.getEmail(), usuarioSalvo.getPerfil().name()))
                .thenReturn("refresh-token");

        AuthResponseDTO response = authServiceUsecase.register(request);

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(usuarioSalvo.getId(), response.userId());
        assertEquals("ADMIN", response.profile());
        verify(usuarioRepository).existsByEmail(request.email());
        verify(passwordEncoder).encode(request.password());
    }

    @Test
    void shouldThrowWhenRegisterAndEmailAlreadyExists() {
        RegisterRequestDTO request = new RegisterRequestDTO("Admin User", "admin@oficina.com", "senha123", "ADMIN");
        when(usuarioRepository.existsByEmail(request.email())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authServiceUsecase.register(request));

        assertTrue(exception.getMessage().contains("Email already registered"));
    }

    @Test
    void shouldThrowWhenRegisterAndProfileIsInvalid() {
        RegisterRequestDTO request = new RegisterRequestDTO("Admin User", "admin@oficina.com", "senha123", "OWNER");
        when(usuarioRepository.existsByEmail(request.email())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authServiceUsecase.register(request));

        assertTrue(exception.getMessage().contains("Invalid profile"));
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginRequestDTO request = new LoginRequestDTO("admin@oficina.com", "senha123");
        UsuarioEntity usuario = buildUsuario(UUID.randomUUID(), "Admin User", "admin@oficina.com", "hashed", PerfilUsuario.ADMIN);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(usuario));
        when(jwtTokenProvider.generateAccessToken(usuario.getId(), usuario.getEmail(), usuario.getPerfil().name()))
                .thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(usuario.getId(), usuario.getEmail(), usuario.getPerfil().name()))
                .thenReturn("refresh-token");

        AuthResponseDTO response = authServiceUsecase.login(request);

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository).findByEmail(request.email());
    }

    @Test
    void shouldThrowWhenLoginAndUserNotFound() {
        LoginRequestDTO request = new LoginRequestDTO("admin@oficina.com", "senha123");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authServiceUsecase.login(request));

        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        String refreshToken = "valid-refresh-token";
        UsuarioEntity usuario = buildUsuario(UUID.randomUUID(), "Admin User", "admin@oficina.com", "hashed", PerfilUsuario.ADMIN);

        when(jwtTokenProvider.isTokenValid(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.extractTokenType(refreshToken)).thenReturn("REFRESH");
        when(jwtTokenProvider.extractEmail(refreshToken)).thenReturn(usuario.getEmail());
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));
        when(jwtTokenProvider.generateAccessToken(usuario.getId(), usuario.getEmail(), usuario.getPerfil().name()))
                .thenReturn("new-access");
        when(jwtTokenProvider.generateRefreshToken(usuario.getId(), usuario.getEmail(), usuario.getPerfil().name()))
                .thenReturn("new-refresh");

        AuthResponseDTO response = authServiceUsecase.refreshToken(refreshToken);

        assertEquals("new-access", response.accessToken());
        assertEquals("new-refresh", response.refreshToken());
        verify(jwtTokenProvider).isTokenValid(refreshToken);
        verify(jwtTokenProvider).extractTokenType(refreshToken);
        verify(jwtTokenProvider).extractEmail(refreshToken);
    }

    @Test
    void shouldThrowWhenRefreshTokenIsInvalid() {
        String refreshToken = "invalid-token";
        when(jwtTokenProvider.isTokenValid(refreshToken)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authServiceUsecase.refreshToken(refreshToken));

        assertTrue(exception.getMessage().contains("Invalid or expired refresh token"));
    }

    @Test
    void shouldThrowWhenTokenTypeIsNotRefresh() {
        String token = "access-token";
        when(jwtTokenProvider.isTokenValid(token)).thenReturn(true);
        when(jwtTokenProvider.extractTokenType(token)).thenReturn("ACCESS");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authServiceUsecase.refreshToken(token));

        assertTrue(exception.getMessage().contains("not a refresh token"));
    }

    @Test
    void shouldThrowWhenRefreshAndUserNotFound() {
        String token = "refresh-token";
        when(jwtTokenProvider.isTokenValid(token)).thenReturn(true);
        when(jwtTokenProvider.extractTokenType(token)).thenReturn("REFRESH");
        when(jwtTokenProvider.extractEmail(token)).thenReturn("missing@oficina.com");
        when(usuarioRepository.findByEmail("missing@oficina.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authServiceUsecase.refreshToken(token));

        assertTrue(exception.getMessage().contains("User not found"));
    }

    private UsuarioEntity buildUsuario(UUID id, String nome, String email, String senha, PerfilUsuario perfil) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setPerfil(perfil);
        return usuario;
    }
}
