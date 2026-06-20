package com.postech.challenge.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para criação ou atualização de ordem de serviço")
public record OrdemServicoRequestDTO(

        @NotNull(message = "clienteId is required")
        @Schema(description = "ID do cliente", example = "11111111-1111-1111-1111-111111111111")
        UUID clienteId,

        @NotNull(message = "veiculoId is required")
        @Schema(description = "ID do veículo", example = "22222222-2222-2222-2222-222222222221")
        UUID veiculoId,

        @Schema(description = "Status da ordem de serviço", example = "RECEBIDA")
        String status,

        @Schema(description = "Data e hora de abertura", example = "2026-04-19T10:30:00")
        LocalDateTime dataAbertura,

        @Schema(description = "Data e hora de finalização", example = "2026-04-20T15:00:00")
        LocalDateTime dataFinalizacao,

        @Schema(description = "Lista de IDs dos serviços solicitados")
        List<UUID> servicosSolicitadosIds,

        @Schema(description = "Lista de IDs dos insumos solicitados")
        List<UUID> insumosSolicitadosIds,

        @Schema(description = "Lista de IDs das peças solicitadas")
        List<UUID> pecasSolicitadasIds
) {
}
