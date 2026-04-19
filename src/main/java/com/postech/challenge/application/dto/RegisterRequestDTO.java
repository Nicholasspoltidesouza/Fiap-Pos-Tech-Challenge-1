package com.postech.challenge.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para cadastro de novo usuário")
public record RegisterRequestDTO(

        @Schema(description = "Nome completo do usuário", example = "João Silva")
        String name,

        @Schema(description = "E-mail do usuário (utilizado como login)", example = "joao@oficina.com")
        String email,

        @Schema(description = "Senha de acesso", example = "senha123")
        String password,

        @Schema(
                description = "Perfil de acesso do usuário",
                example = "ADMIN",
                allowableValues = {"ADMIN", "ATENDENTE", "MECANICO"}
        )
        String profile
) {
}
