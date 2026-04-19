package com.postech.challenge.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação ou atualização de cliente")
public record ClienteRequestDTO(

        @Schema(description = "Nome completo do cliente", example = "Joao Silva")
        String nome,

        @Schema(description = "CPF ou CNPJ do cliente", example = "123.456.789-00")
        String cpfCnpj
) {
}
