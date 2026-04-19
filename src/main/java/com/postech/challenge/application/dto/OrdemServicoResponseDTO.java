package com.postech.challenge.application.dto;

import java.math.BigDecimal;
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
        BigDecimal valorOrcamento,
        Boolean orcamentoAprovado,
        LocalDateTime dataEnvioOrcamento,
        List<UUID> servicosSolicitadosIds,
        List<UUID> insumosSolicitadosIds,
        List<UUID> pecasSolicitadasIds
) {
}
