package com.postech.challenge.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.postech.challenge.application.dto.PecaRequestDTO;
import com.postech.challenge.application.dto.PecaResponseDTO;
import com.postech.challenge.infrastructure.persistence.entity.PecaEntity;

class PecaDataMapperTest {

    private final PecaDataMapper pecaDataMapper = new PecaDataMapper();

    @Test
    void shouldMapRequestToEntity() {
        PecaRequestDTO request = new PecaRequestDTO("Filtro de óleo", new BigDecimal("59.90"), 25);

        PecaEntity entity = pecaDataMapper.toEntity(request);

        assertEquals("Filtro de óleo", entity.getNome());
        assertEquals(new BigDecimal("59.90"), entity.getPrecoUnitario());
        assertEquals(25, entity.getQuantidadeEstoque());
    }

    @Test
    void shouldMapEntityToResponse() {
        UUID id = UUID.randomUUID();
        PecaEntity entity = new PecaEntity();
        entity.setId(id);
        entity.setNome("Pastilha de freio");
        entity.setPrecoUnitario(new BigDecimal("149.90"));
        entity.setQuantidadeEstoque(30);

        PecaResponseDTO response = pecaDataMapper.toResponse(entity);

        assertEquals(id, response.id());
        assertEquals("Pastilha de freio", response.nome());
        assertEquals(new BigDecimal("149.90"), response.precoUnitario());
        assertEquals(30, response.quantidadeEstoque());
    }

    @Test
    void shouldUpdateEntityFromRequest() {
        PecaEntity entity = new PecaEntity();
        entity.setNome("Nome Antigo");
        entity.setPrecoUnitario(new BigDecimal("10.00"));
        entity.setQuantidadeEstoque(1);

        PecaRequestDTO request = new PecaRequestDTO("Nome Novo", new BigDecimal("12.50"), 15);
        pecaDataMapper.updateEntity(entity, request);

        assertEquals("Nome Novo", entity.getNome());
        assertEquals(new BigDecimal("12.50"), entity.getPrecoUnitario());
        assertEquals(15, entity.getQuantidadeEstoque());
    }
}
