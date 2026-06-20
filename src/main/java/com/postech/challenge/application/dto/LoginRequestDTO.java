package com.postech.challenge.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais de login")
public record LoginRequestDTO(

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Schema(description = "E-mail cadastrado", example = "joao@oficina.com")
        String email,

        @NotBlank(message = "password is required")
        @Schema(description = "Senha de acesso", example = "senha123")
        String password
) {
}
