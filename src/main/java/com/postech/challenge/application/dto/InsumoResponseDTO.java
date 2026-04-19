package com.postech.challenge.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record InsumoResponseDTO(
        UUID id,
        String nome,
        BigDecimal precoUnitario,
        Integer quantidadeEstoque
) {
}
