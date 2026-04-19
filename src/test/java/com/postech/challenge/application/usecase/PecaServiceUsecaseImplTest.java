package com.postech.challenge.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.challenge.application.dto.PecaRequestDTO;
import com.postech.challenge.application.dto.PecaResponseDTO;
import com.postech.challenge.application.mapper.PecaDataMapper;
import com.postech.challenge.infrastructure.persistence.entity.PecaEntity;
import com.postech.challenge.infrastructure.persistence.repository.PecaRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class PecaServiceUsecaseImplTest {

    @Mock
    private PecaRepository pecaRepository;

    @Mock
    private PecaDataMapper pecaDataMapper;

    @InjectMocks
    private PecaServiceUsecaseImpl pecaServiceUsecase;

    @Test
    void shouldFindAllPecas() {
        PecaEntity peca = buildPecaEntity("Filtro de óleo", "59.90", 25);
        PecaResponseDTO response = buildPecaResponse(peca);

        when(pecaRepository.findAll()).thenReturn(List.of(peca));
        when(pecaDataMapper.toResponse(peca)).thenReturn(response);

        List<PecaResponseDTO> result = pecaServiceUsecase.findAll();

        assertEquals(1, result.size());
        assertEquals(response, result.getFirst());
        verify(pecaRepository).findAll();
        verify(pecaDataMapper).toResponse(peca);
    }

    @Test
    void shouldFindPecaById() {
        UUID id = UUID.randomUUID();
        PecaEntity peca = buildPecaEntity(id, "Pastilha de freio", "149.90", 30);
        PecaResponseDTO response = buildPecaResponse(peca);

        when(pecaRepository.findById(id)).thenReturn(Optional.of(peca));
        when(pecaDataMapper.toResponse(peca)).thenReturn(response);

        PecaResponseDTO result = pecaServiceUsecase.findById(id);

        assertEquals(response, result);
        verify(pecaRepository).findById(id);
        verify(pecaDataMapper).toResponse(peca);
    }

    @Test
    void shouldThrowWhenFindByIdAndPecaDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(pecaRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> pecaServiceUsecase.findById(id));

        assertTrue(exception.getMessage().contains("Peca not found"));
        verify(pecaRepository).findById(id);
    }

    @Test
    void shouldCreatePeca() {
        PecaRequestDTO request = new PecaRequestDTO("Correia dentada", new BigDecimal("220.00"), 12);
        PecaEntity entityToSave = buildPecaEntity("Correia dentada", "220.00", 12);
        PecaEntity savedEntity = buildPecaEntity("Correia dentada", "220.00", 12);
        PecaResponseDTO response = buildPecaResponse(savedEntity);

        when(pecaDataMapper.toEntity(request)).thenReturn(entityToSave);
        when(pecaRepository.save(entityToSave)).thenReturn(savedEntity);
        when(pecaDataMapper.toResponse(savedEntity)).thenReturn(response);

        PecaResponseDTO result = pecaServiceUsecase.create(request);

        assertEquals(response, result);
        verify(pecaDataMapper).toEntity(request);
        verify(pecaRepository).save(entityToSave);
        verify(pecaDataMapper).toResponse(savedEntity);
    }

    @Test
    void shouldUpdatePeca() {
        UUID id = UUID.randomUUID();
        PecaRequestDTO request = new PecaRequestDTO("Filtro de ar", new BigDecimal("89.90"), 18);
        PecaEntity existing = buildPecaEntity(id, "Filtro antigo", "40.00", 9);
        PecaResponseDTO response = buildPecaResponse(existing);

        when(pecaRepository.findById(id)).thenReturn(Optional.of(existing));
        when(pecaRepository.save(existing)).thenReturn(existing);
        when(pecaDataMapper.toResponse(existing)).thenReturn(response);

        PecaResponseDTO result = pecaServiceUsecase.update(id, request);

        assertEquals(response, result);
        verify(pecaRepository).findById(id);
        verify(pecaDataMapper).updateEntity(existing, request);
        verify(pecaRepository).save(existing);
        verify(pecaDataMapper).toResponse(existing);
    }

    @Test
    void shouldThrowWhenUpdateAndPecaDoesNotExist() {
        UUID id = UUID.randomUUID();
        PecaRequestDTO request = new PecaRequestDTO("Filtro de ar", new BigDecimal("89.90"), 18);
        when(pecaRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> pecaServiceUsecase.update(id, request));

        assertTrue(exception.getMessage().contains("Peca not found"));
        verify(pecaRepository).findById(id);
        verify(pecaRepository, never()).save(any());
    }

    @Test
    void shouldDeletePeca() {
        UUID id = UUID.randomUUID();
        when(pecaRepository.existsById(id)).thenReturn(true);

        pecaServiceUsecase.delete(id);

        verify(pecaRepository).existsById(id);
        verify(pecaRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteAndPecaDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(pecaRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> pecaServiceUsecase.delete(id));

        assertTrue(exception.getMessage().contains("Peca not found"));
        verify(pecaRepository).existsById(id);
        verify(pecaRepository, never()).deleteById(id);
    }

    private PecaEntity buildPecaEntity(String nome, String precoUnitario, Integer quantidadeEstoque) {
        return buildPecaEntity(UUID.randomUUID(), nome, precoUnitario, quantidadeEstoque);
    }

    private PecaEntity buildPecaEntity(UUID id, String nome, String precoUnitario, Integer quantidadeEstoque) {
        PecaEntity peca = new PecaEntity();
        peca.setId(id);
        peca.setNome(nome);
        peca.setPrecoUnitario(new BigDecimal(precoUnitario));
        peca.setQuantidadeEstoque(quantidadeEstoque);
        return peca;
    }

    private PecaResponseDTO buildPecaResponse(PecaEntity peca) {
        return new PecaResponseDTO(
                peca.getId(),
                peca.getNome(),
                peca.getPrecoUnitario(),
                peca.getQuantidadeEstoque());
    }
}
