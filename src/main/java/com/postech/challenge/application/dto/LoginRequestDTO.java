package com.postech.challenge.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credenciais de login")
public record LoginRequestDTO(

        @Schema(description = "E-mail cadastrado", example = "joao@oficina.com")
        String email,

        @Schema(description = "Senha de acesso", example = "senha123")
        String password
) {
}
