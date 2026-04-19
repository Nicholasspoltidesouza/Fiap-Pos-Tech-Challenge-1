package com.postech.challenge.application.dto;

import java.util.UUID;

public record TempoMedioServicoResponseDTO(
        UUID servicoId,
        String servicoNome,
        Long quantidadeOrdensConcluidas,
        Double tempoMedioMinutos
) {
}
