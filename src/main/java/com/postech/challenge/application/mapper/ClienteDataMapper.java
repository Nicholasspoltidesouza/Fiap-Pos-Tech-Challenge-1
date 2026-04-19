package com.postech.challenge.application.mapper;

import org.springframework.stereotype.Component;

import com.postech.challenge.application.dto.ClienteRequestDTO;
import com.postech.challenge.application.dto.ClienteResponseDTO;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;

@Component
public class ClienteDataMapper {

    public ClienteResponseDTO toResponse(ClienteEntity cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpfCnpj()
        );
    }

    public ClienteEntity toEntity(ClienteRequestDTO request) {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setNome(request.nome());
        cliente.setCpfCnpj(request.cpfCnpj());
        return cliente;
    }

    public void updateEntity(ClienteEntity cliente, ClienteRequestDTO request) {
        cliente.setNome(request.nome());
        cliente.setCpfCnpj(request.cpfCnpj());
    }
}
