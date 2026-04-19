package com.postech.challenge.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.postech.challenge.application.dto.ClienteRequestDTO;
import com.postech.challenge.application.dto.ClienteResponseDTO;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;

class ClienteDataMapperTest {

    private final ClienteDataMapper clienteDataMapper = new ClienteDataMapper();

    @Test
    void shouldMapRequestToEntity() {
        ClienteRequestDTO request = new ClienteRequestDTO("Joao Silva", "123.456.789-00");

        ClienteEntity entity = clienteDataMapper.toEntity(request);

        assertEquals("Joao Silva", entity.getNome());
        assertEquals("123.456.789-00", entity.getCpfCnpj());
    }

    @Test
    void shouldMapEntityToResponse() {
        UUID id = UUID.randomUUID();
        ClienteEntity entity = new ClienteEntity();
        entity.setId(id);
        entity.setNome("Maria Oliveira");
        entity.setCpfCnpj("234.567.890-11");

        ClienteResponseDTO response = clienteDataMapper.toResponse(entity);

        assertEquals(id, response.id());
        assertEquals("Maria Oliveira", response.nome());
        assertEquals("234.567.890-11", response.cpfCnpj());
    }

    @Test
    void shouldUpdateEntityFromRequest() {
        ClienteEntity entity = new ClienteEntity();
        entity.setNome("Nome Antigo");
        entity.setCpfCnpj("111.111.111-11");

        ClienteRequestDTO request = new ClienteRequestDTO("Nome Novo", "222.222.222-22");
        clienteDataMapper.updateEntity(entity, request);

        assertEquals("Nome Novo", entity.getNome());
        assertEquals("222.222.222-22", entity.getCpfCnpj());
    }
}
