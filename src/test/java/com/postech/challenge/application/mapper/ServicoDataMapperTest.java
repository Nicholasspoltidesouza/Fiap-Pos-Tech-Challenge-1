package com.postech.challenge.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.postech.challenge.application.dto.ServicoRequestDTO;
import com.postech.challenge.application.dto.ServicoResponseDTO;
import com.postech.challenge.infrastructure.persistence.entity.ServicoEntity;

class ServicoDataMapperTest {

    private final ServicoDataMapper servicoDataMapper = new ServicoDataMapper();

    @Test
    void shouldMapRequestToEntity() {
        ServicoRequestDTO request = new ServicoRequestDTO("Troca de óleo", "Substituição do óleo e filtro");

        ServicoEntity entity = servicoDataMapper.toEntity(request);

        assertEquals("Troca de óleo", entity.getNome());
        assertEquals("Substituição do óleo e filtro", entity.getDescricao());
    }

    @Test
    void shouldMapEntityToResponse() {
        UUID id = UUID.randomUUID();
        ServicoEntity entity = new ServicoEntity();
        entity.setId(id);
        entity.setNome("Alinhamento");
        entity.setDescricao("Alinhamento da direção e ajuste de geometria");

        ServicoResponseDTO response = servicoDataMapper.toResponse(entity);

        assertEquals(id, response.id());
        assertEquals("Alinhamento", response.nome());
        assertEquals("Alinhamento da direção e ajuste de geometria", response.descricao());
    }

    @Test
    void shouldUpdateEntityFromRequest() {
        ServicoEntity entity = new ServicoEntity();
        entity.setNome("Nome Antigo");
        entity.setDescricao("Descrição Antiga");

        ServicoRequestDTO request = new ServicoRequestDTO("Nome Novo", "Descrição Nova");
        servicoDataMapper.updateEntity(entity, request);

        assertEquals("Nome Novo", entity.getNome());
        assertEquals("Descrição Nova", entity.getDescricao());
    }
}
