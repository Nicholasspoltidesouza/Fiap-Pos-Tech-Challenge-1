package com.postech.challenge.application.dto;

import java.util.UUID;

public record ServicoResponseDTO(
        UUID id,
        String nome,
        String descricao
) {
}
