package com.postech.challenge.presentation.api;

import com.postech.challenge.application.dto.AuthResponseDTO;
import com.postech.challenge.application.dto.LoginRequestDTO;
import com.postech.challenge.application.dto.RefreshTokenRequestDTO;
import com.postech.challenge.application.dto.RegisterRequestDTO;
import com.postech.challenge.application.usecase.AuthServiceUsecase;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@SecurityRequirements
@Tag(name = "Autenticação", description = "Endpoints públicos de login e cadastro")
public class AuthControllerApi {

    private final AuthServiceUsecase authService;

    public AuthControllerApi(AuthServiceUsecase authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request.refreshToken()));
    }
}
