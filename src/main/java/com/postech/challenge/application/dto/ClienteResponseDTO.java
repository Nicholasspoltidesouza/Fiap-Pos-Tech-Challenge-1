package com.postech.challenge.application.dto;

import java.util.UUID;

public record ClienteResponseDTO(
        UUID id,
        String nome,
        String cpfCnpj
) {
}
