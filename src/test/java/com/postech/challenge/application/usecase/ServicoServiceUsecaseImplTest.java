package com.postech.challenge.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.challenge.application.dto.ServicoRequestDTO;
import com.postech.challenge.application.dto.ServicoResponseDTO;
import com.postech.challenge.application.mapper.ServicoDataMapper;
import com.postech.challenge.infrastructure.persistence.entity.ServicoEntity;
import com.postech.challenge.infrastructure.persistence.repository.ServicoRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ServicoServiceUsecaseImplTest {

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private ServicoDataMapper servicoDataMapper;

    @InjectMocks
    private ServicoServiceUsecaseImpl servicoServiceUsecase;

    @Test
    void shouldFindAllServicos() {
        ServicoEntity servico = buildServicoEntity("Troca de óleo", "Substituição do óleo e filtro");
        ServicoResponseDTO response = new ServicoResponseDTO(servico.getId(), servico.getNome(), servico.getDescricao());

        when(servicoRepository.findAll()).thenReturn(List.of(servico));
        when(servicoDataMapper.toResponse(servico)).thenReturn(response);

        List<ServicoResponseDTO> result = servicoServiceUsecase.findAll();

        assertEquals(1, result.size());
        assertEquals(response, result.getFirst());
        verify(servicoRepository).findAll();
        verify(servicoDataMapper).toResponse(servico);
    }

    @Test
    void shouldFindServicoById() {
        UUID id = UUID.randomUUID();
        ServicoEntity servico = buildServicoEntity(id, "Alinhamento", "Alinhamento da direção e ajuste de geometria");
        ServicoResponseDTO response = new ServicoResponseDTO(id, servico.getNome(), servico.getDescricao());

        when(servicoRepository.findById(id)).thenReturn(Optional.of(servico));
        when(servicoDataMapper.toResponse(servico)).thenReturn(response);

        ServicoResponseDTO result = servicoServiceUsecase.findById(id);

        assertEquals(response, result);
        verify(servicoRepository).findById(id);
        verify(servicoDataMapper).toResponse(servico);
    }

    @Test
    void shouldThrowWhenFindByIdAndServicoDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(servicoRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> servicoServiceUsecase.findById(id));

        assertTrue(exception.getMessage().contains("Servico not found"));
        verify(servicoRepository).findById(id);
    }

    @Test
    void shouldCreateServico() {
        ServicoRequestDTO request = new ServicoRequestDTO("Revisão elétrica", "Verificação do sistema elétrico");
        ServicoEntity entityToSave = buildServicoEntity("Revisão elétrica", "Verificação do sistema elétrico");
        ServicoEntity savedEntity = buildServicoEntity("Revisão elétrica", "Verificação do sistema elétrico");
        ServicoResponseDTO response = new ServicoResponseDTO(
                savedEntity.getId(),
                savedEntity.getNome(),
                savedEntity.getDescricao());

        when(servicoDataMapper.toEntity(request)).thenReturn(entityToSave);
        when(servicoRepository.save(entityToSave)).thenReturn(savedEntity);
        when(servicoDataMapper.toResponse(savedEntity)).thenReturn(response);

        ServicoResponseDTO result = servicoServiceUsecase.create(request);

        assertEquals(response, result);
        verify(servicoDataMapper).toEntity(request);
        verify(servicoRepository).save(entityToSave);
        verify(servicoDataMapper).toResponse(savedEntity);
    }

    @Test
    void shouldUpdateServico() {
        UUID id = UUID.randomUUID();
        ServicoRequestDTO request = new ServicoRequestDTO("Nome Novo", "Descrição Nova");
        ServicoEntity existing = buildServicoEntity(id, "Nome Antigo", "Descrição Antiga");
        ServicoResponseDTO response = new ServicoResponseDTO(id, "Nome Novo", "Descrição Nova");

        when(servicoRepository.findById(id)).thenReturn(Optional.of(existing));
        when(servicoRepository.save(existing)).thenReturn(existing);
        when(servicoDataMapper.toResponse(existing)).thenReturn(response);

        ServicoResponseDTO result = servicoServiceUsecase.update(id, request);

        assertEquals(response, result);
        verify(servicoRepository).findById(id);
        verify(servicoDataMapper).updateEntity(existing, request);
        verify(servicoRepository).save(existing);
        verify(servicoDataMapper).toResponse(existing);
    }

    @Test
    void shouldThrowWhenUpdateAndServicoDoesNotExist() {
        UUID id = UUID.randomUUID();
        ServicoRequestDTO request = new ServicoRequestDTO("Nome Novo", "Descrição Nova");
        when(servicoRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> servicoServiceUsecase.update(id, request));

        assertTrue(exception.getMessage().contains("Servico not found"));
        verify(servicoRepository).findById(id);
        verify(servicoRepository, never()).save(any());
    }

    @Test
    void shouldDeleteServico() {
        UUID id = UUID.randomUUID();
        when(servicoRepository.existsById(id)).thenReturn(true);

        servicoServiceUsecase.delete(id);

        verify(servicoRepository).existsById(id);
        verify(servicoRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteAndServicoDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(servicoRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> servicoServiceUsecase.delete(id));

        assertTrue(exception.getMessage().contains("Servico not found"));
        verify(servicoRepository).existsById(id);
        verify(servicoRepository, never()).deleteById(id);
    }

    private ServicoEntity buildServicoEntity(String nome, String descricao) {
        return buildServicoEntity(UUID.randomUUID(), nome, descricao);
    }

    private ServicoEntity buildServicoEntity(UUID id, String nome, String descricao) {
        ServicoEntity servico = new ServicoEntity();
        servico.setId(id);
        servico.setNome(nome);
        servico.setDescricao(descricao);
        return servico;
    }
}
