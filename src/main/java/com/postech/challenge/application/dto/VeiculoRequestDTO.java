package com.postech.challenge.application.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criacao ou atualizacao de veiculo")
public record VeiculoRequestDTO(

        @Schema(description = "Marca do veiculo", example = "Toyota")
        String marca,

        @Schema(description = "Modelo do veiculo", example = "Corolla")
        String modelo,

        @Schema(description = "Placa do veiculo", example = "ABC1D23")
        String placa,

        @Schema(description = "Ano do veiculo", example = "2020")
        Integer ano,

        @Schema(description = "ID do cliente proprietario", example = "11111111-1111-1111-1111-111111111111")
        UUID clienteId
) {
}
