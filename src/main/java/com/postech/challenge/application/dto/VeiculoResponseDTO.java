package com.postech.challenge.application.dto;

import java.util.UUID;

public record VeiculoResponseDTO(
        UUID id,
        String marca,
        String modelo,
        Integer ano,
        UUID clienteId
) {
}
