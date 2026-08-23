package com.postech.challenge.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.postech.challenge.domain.model.StatusOrdemServico;

public record StatusHistoricoPonto(
        UUID ordemServicoId,
        StatusOrdemServico status,
        LocalDateTime alteradoEm) {
}
