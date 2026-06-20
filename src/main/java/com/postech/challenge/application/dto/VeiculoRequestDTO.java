package com.postech.challenge.application.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para criacao ou atualizacao de veiculo")
public record VeiculoRequestDTO(

        @NotBlank(message = "marca is required")
        @Schema(description = "Marca do veiculo", example = "Toyota")
        String marca,

        @NotBlank(message = "modelo is required")
        @Schema(description = "Modelo do veiculo", example = "Corolla")
        String modelo,

        @NotBlank(message = "placa is required")
        @Schema(description = "Placa do veiculo", example = "ABC1D23")
        String placa,

        @NotNull(message = "ano is required")
        @Schema(description = "Ano do veiculo", example = "2020")
        Integer ano,

        @NotNull(message = "clienteId is required")
        @Schema(description = "ID do cliente proprietario", example = "11111111-1111-1111-1111-111111111111")
        UUID clienteId
) {
}
