package com.postech.challenge.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para criação ou atualização de cliente")
public record ClienteRequestDTO(

        @NotBlank(message = "nome is required")
        @Schema(description = "Nome completo do cliente", example = "Joao Silva")
        String nome,

        @NotBlank(message = "cpfCnpj is required")
        @Schema(description = "CPF ou CNPJ do cliente", example = "123.456.789-00")
        String cpfCnpj
) {
}
