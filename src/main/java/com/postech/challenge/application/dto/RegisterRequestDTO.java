package com.postech.challenge.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para cadastro de novo usuário")
public record RegisterRequestDTO(

        @NotBlank(message = "name is required")
        @Schema(description = "Nome completo do usuário", example = "João Silva")
        String name,

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Schema(description = "E-mail do usuário (utilizado como login)", example = "joao@oficina.com")
        String email,

        @NotBlank(message = "password is required")
        @Schema(description = "Senha de acesso", example = "senha123")
        String password,

        @NotBlank(message = "profile is required")
        @Schema(
                description = "Perfil de acesso do usuário",
                example = "ADMIN",
                allowableValues = {"ADMIN", "ATENDENTE", "MECANICO"}
        )
        String profile
) {
}
