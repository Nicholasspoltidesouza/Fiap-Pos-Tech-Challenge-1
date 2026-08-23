package com.postech.challenge.application.dto;

import java.time.LocalDate;

public record VolumeDiarioOrdemServicoResponseDTO(
        LocalDate data,
        long quantidade
) {
}
