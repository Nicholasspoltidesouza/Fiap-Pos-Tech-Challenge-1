package com.postech.challenge.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AcompanhamentoOrdemServicoResponseDTO(
        UUID ordemServicoId,
        String clienteCpfCnpj,
        String status,
        BigDecimal valorOrcamento,
        Boolean orcamentoAprovado,
        LocalDateTime dataAbertura,
        LocalDateTime dataEnvioOrcamento,
        LocalDateTime dataFinalizacao
) {
}
