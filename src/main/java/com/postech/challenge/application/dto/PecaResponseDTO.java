package com.postech.challenge.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PecaResponseDTO(
        UUID id,
        String nome,
        BigDecimal precoUnitario,
        Integer quantidadeEstoque
) {
}
