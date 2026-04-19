package com.postech.challenge.application.usecase;

import com.postech.challenge.application.dto.AuthResponseDTO;
import com.postech.challenge.application.dto.LoginRequestDTO;
import com.postech.challenge.application.dto.RegisterRequestDTO;
import com.postech.challenge.infrastructure.persistence.entity.PerfilUsuario;
import com.postech.challenge.infrastructure.persistence.entity.UsuarioEntity;
import com.postech.challenge.infrastructure.persistence.repository.UsuarioRepository;
import com.postech.challenge.infrastructure.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceUsecase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthServiceUsecase(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        UsuarioEntity usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return buildAuthResponse(usuario);
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered: " + request.email());
        }

        PerfilUsuario perfil;
        try {
            perfil = PerfilUsuario.valueOf(request.profile().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid profile: " + request.profile() + ". Allowed values: ADMIN, ATENDENTE, MECANICO");
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNome(request.name());
        usuario.setEmail(request.email());
        usuario.setSenha(passwordEncoder.encode(request.password()));
        usuario.setPerfil(perfil);

        usuario = usuarioRepository.save(usuario);

        return buildAuthResponse(usuario);
    }

    public AuthResponseDTO refreshToken(String refreshToken) {
        if (!jwtTokenProvider.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        String tokenType = jwtTokenProvider.extractTokenType(refreshToken);
        if (!"REFRESH".equals(tokenType)) {
            throw new IllegalArgumentException("Token provided is not a refresh token");
        }

        String email = jwtTokenProvider.extractEmail(refreshToken);
        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return buildAuthResponse(usuario);
    }

    private AuthResponseDTO buildAuthResponse(UsuarioEntity usuario) {
        String accessToken = jwtTokenProvider.generateAccessToken(
                usuario.getId(), usuario.getEmail(), usuario.getPerfil().name());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(
                usuario.getId(), usuario.getEmail(), usuario.getPerfil().name());

        return AuthResponseDTO.of(
                accessToken, newRefreshToken,
                usuario.getId(), usuario.getEmail(),
                usuario.getNome(), usuario.getPerfil().name());
    }
}
