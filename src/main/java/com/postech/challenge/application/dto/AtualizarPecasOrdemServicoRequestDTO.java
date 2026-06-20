package com.postech.challenge.application.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "Dados para vincular peças na ordem de serviço")
public record AtualizarPecasOrdemServicoRequestDTO(
        @NotEmpty(message = "pecasIds must not be empty")
        @Schema(description = "Lista de IDs de peças")
        List<UUID> pecasIds
) {
}
