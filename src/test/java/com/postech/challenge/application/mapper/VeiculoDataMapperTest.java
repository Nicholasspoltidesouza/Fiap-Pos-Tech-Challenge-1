package com.postech.challenge.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.postech.challenge.application.dto.VeiculoRequestDTO;
import com.postech.challenge.application.dto.VeiculoResponseDTO;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;
import com.postech.challenge.infrastructure.persistence.entity.VeiculoEntity;

class VeiculoDataMapperTest {

    private final VeiculoDataMapper veiculoDataMapper = new VeiculoDataMapper();

    @Test
    void shouldMapRequestToEntity() {
        UUID clienteId = UUID.randomUUID();
        ClienteEntity cliente = buildCliente(clienteId);
        VeiculoRequestDTO request = new VeiculoRequestDTO("Toyota", "Corolla", "ABC1D23", 2020, clienteId);

        VeiculoEntity entity = veiculoDataMapper.toEntity(request, cliente);

        assertEquals("Toyota", entity.getMarca());
        assertEquals("Corolla", entity.getModelo());
        assertEquals("ABC1D23", entity.getPlaca());
        assertEquals(2020, entity.getAno());
        assertEquals(clienteId, entity.getCliente().getId());
    }

    @Test
    void shouldMapEntityToResponse() {
        UUID veiculoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        VeiculoEntity entity = new VeiculoEntity();
        entity.setId(veiculoId);
        entity.setMarca("Honda");
        entity.setModelo("Civic");
        entity.setPlaca("DEF2G34");
        entity.setAno(2019);
        entity.setCliente(buildCliente(clienteId));

        VeiculoResponseDTO response = veiculoDataMapper.toResponse(entity);

        assertEquals(veiculoId, response.id());
        assertEquals("Honda", response.marca());
        assertEquals("Civic", response.modelo());
        assertEquals("DEF2G34", response.placa());
        assertEquals(2019, response.ano());
        assertEquals(clienteId, response.clienteId());
    }

    @Test
    void shouldUpdateEntityFromRequest() {
        UUID oldClienteId = UUID.randomUUID();
        UUID newClienteId = UUID.randomUUID();

        VeiculoEntity entity = new VeiculoEntity();
        entity.setMarca("Marca Antiga");
        entity.setModelo("Modelo Antigo");
        entity.setPlaca("ZZZ9Z99");
        entity.setAno(2015);
        entity.setCliente(buildCliente(oldClienteId));

        VeiculoRequestDTO request = new VeiculoRequestDTO("Chevrolet", "Onix", "GHI3J45", 2022, newClienteId);
        veiculoDataMapper.updateEntity(entity, request, buildCliente(newClienteId));

        assertEquals("Chevrolet", entity.getMarca());
        assertEquals("Onix", entity.getModelo());
        assertEquals("GHI3J45", entity.getPlaca());
        assertEquals(2022, entity.getAno());
        assertEquals(newClienteId, entity.getCliente().getId());
    }

    private ClienteEntity buildCliente(UUID id) {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setId(id);
        return cliente;
    }
}
