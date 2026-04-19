package com.postech.challenge.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrdemServicoResponseDTO(
        UUID id,
        UUID clienteId,
        UUID veiculoId,
        String status,
        LocalDateTime dataAbertura,
        LocalDateTime dataFinalizacao,
        List<UUID> servicosSolicitadosIds,
        List<UUID> insumosSolicitadosIds
) {
}
