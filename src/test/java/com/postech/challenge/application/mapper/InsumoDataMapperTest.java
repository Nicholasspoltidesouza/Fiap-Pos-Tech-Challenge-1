package com.postech.challenge.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.postech.challenge.application.dto.InsumoRequestDTO;
import com.postech.challenge.application.dto.InsumoResponseDTO;
import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;

class InsumoDataMapperTest {

    private final InsumoDataMapper insumoDataMapper = new InsumoDataMapper();

    @Test
    void shouldMapRequestToEntity() {
        InsumoRequestDTO request = new InsumoRequestDTO("Oleo 5W30 1L", new BigDecimal("45.90"), 120);

        InsumoEntity entity = insumoDataMapper.toEntity(request);

        assertEquals("Oleo 5W30 1L", entity.getNome());
        assertEquals(new BigDecimal("45.90"), entity.getPrecoUnitario());
        assertEquals(120, entity.getQuantidadeEstoque());
    }

    @Test
    void shouldMapEntityToResponse() {
        UUID id = UUID.randomUUID();
        InsumoEntity entity = new InsumoEntity();
        entity.setId(id);
        entity.setNome("Filtro de oleo");
        entity.setPrecoUnitario(new BigDecimal("32.50"));
        entity.setQuantidadeEstoque(80);

        InsumoResponseDTO response = insumoDataMapper.toResponse(entity);

        assertEquals(id, response.id());
        assertEquals("Filtro de oleo", response.nome());
        assertEquals(new BigDecimal("32.50"), response.precoUnitario());
        assertEquals(80, response.quantidadeEstoque());
    }

    @Test
    void shouldUpdateEntityFromRequest() {
        InsumoEntity entity = new InsumoEntity();
        entity.setNome("Nome Antigo");
        entity.setPrecoUnitario(new BigDecimal("10.00"));
        entity.setQuantidadeEstoque(1);

        InsumoRequestDTO request = new InsumoRequestDTO("Nome Novo", new BigDecimal("12.50"), 15);
        insumoDataMapper.updateEntity(entity, request);

        assertEquals("Nome Novo", entity.getNome());
        assertEquals(new BigDecimal("12.50"), entity.getPrecoUnitario());
        assertEquals(15, entity.getQuantidadeEstoque());
    }
}
