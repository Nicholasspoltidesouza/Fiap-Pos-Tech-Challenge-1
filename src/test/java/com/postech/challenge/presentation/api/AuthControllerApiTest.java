package com.postech.challenge.presentation.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.postech.challenge.application.dto.AuthResponseDTO;
import com.postech.challenge.application.dto.LoginRequestDTO;
import com.postech.challenge.application.dto.RefreshTokenRequestDTO;
import com.postech.challenge.application.dto.RegisterRequestDTO;
import com.postech.challenge.application.usecase.AuthServiceUsecase;

@ExtendWith(MockitoExtension.class)
class AuthControllerApiTest {

    @Mock
    private AuthServiceUsecase authServiceUsecase;

    @Test
    void shouldLogin() {
        AuthControllerApi controller = new AuthControllerApi(authServiceUsecase);
        LoginRequestDTO request = new LoginRequestDTO("admin@oficina.com", "senha");
        AuthResponseDTO responseDTO = buildAuthResponse();
        when(authServiceUsecase.login(request)).thenReturn(responseDTO);

        ResponseEntity<AuthResponseDTO> response = controller.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(authServiceUsecase).login(request);
    }

    @Test
    void shouldRegister() {
        AuthControllerApi controller = new AuthControllerApi(authServiceUsecase);
        RegisterRequestDTO request = new RegisterRequestDTO("Admin", "admin@oficina.com", "senha", "ADMIN");
        AuthResponseDTO responseDTO = buildAuthResponse();
        when(authServiceUsecase.register(request)).thenReturn(responseDTO);

        ResponseEntity<AuthResponseDTO> response = controller.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(authServiceUsecase).register(request);
    }

    @Test
    void shouldRefreshToken() {
        AuthControllerApi controller = new AuthControllerApi(authServiceUsecase);
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("refresh-token");
        AuthResponseDTO responseDTO = buildAuthResponse();
        when(authServiceUsecase.refreshToken("refresh-token")).thenReturn(responseDTO);

        ResponseEntity<AuthResponseDTO> response = controller.refreshToken(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(authServiceUsecase).refreshToken("refresh-token");
    }

    private AuthResponseDTO buildAuthResponse() {
        return new AuthResponseDTO(
                "access-token",
                "refresh-token",
                "Bearer",
                UUID.randomUUID(),
                "admin@oficina.com",
                "Admin",
                "ADMIN");
    }
}
