package com.postech.challenge.application.dto;

import java.util.UUID;

import com.postech.challenge.domain.model.StatusCliente;

public record ClienteResponseDTO(
        UUID id,
        String nome,
        String cpfCnpj,
        StatusCliente status
) {
    public ClienteResponseDTO(UUID id, String nome, String cpfCnpj) {
        this(id, nome, cpfCnpj, StatusCliente.ATIVO);
    }
}
