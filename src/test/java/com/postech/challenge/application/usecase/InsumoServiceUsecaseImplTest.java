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

import com.postech.challenge.application.dto.InsumoRequestDTO;
import com.postech.challenge.application.dto.InsumoResponseDTO;
import com.postech.challenge.application.mapper.InsumoDataMapper;
import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;
import com.postech.challenge.infrastructure.persistence.repository.InsumoRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class InsumoServiceUsecaseImplTest {

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private InsumoDataMapper insumoDataMapper;

    @InjectMocks
    private InsumoServiceUsecaseImpl insumoServiceUsecase;

    @Test
    void shouldFindAllInsumos() {
        InsumoEntity insumo = buildInsumoEntity("Oleo 5W30 1L", "45.90", 120);
        InsumoResponseDTO response = buildInsumoResponse(insumo);

        when(insumoRepository.findAll()).thenReturn(List.of(insumo));
        when(insumoDataMapper.toResponse(insumo)).thenReturn(response);

        List<InsumoResponseDTO> result = insumoServiceUsecase.findAll();

        assertEquals(1, result.size());
        assertEquals(response, result.getFirst());
        verify(insumoRepository).findAll();
        verify(insumoDataMapper).toResponse(insumo);
    }

    @Test
    void shouldFindInsumoById() {
        UUID id = UUID.randomUUID();
        InsumoEntity insumo = buildInsumoEntity(id, "Filtro de oleo", "32.50", 80);
        InsumoResponseDTO response = buildInsumoResponse(insumo);

        when(insumoRepository.findById(id)).thenReturn(Optional.of(insumo));
        when(insumoDataMapper.toResponse(insumo)).thenReturn(response);

        InsumoResponseDTO result = insumoServiceUsecase.findById(id);

        assertEquals(response, result);
        verify(insumoRepository).findById(id);
        verify(insumoDataMapper).toResponse(insumo);
    }

    @Test
    void shouldThrowWhenFindByIdAndInsumoDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(insumoRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> insumoServiceUsecase.findById(id));

        assertTrue(exception.getMessage().contains("Insumo not found"));
        verify(insumoRepository).findById(id);
    }

    @Test
    void shouldCreateInsumo() {
        InsumoRequestDTO request = new InsumoRequestDTO("Fluido de freio DOT4", new BigDecimal("29.90"), 60);
        InsumoEntity entityToSave = buildInsumoEntity("Fluido de freio DOT4", "29.90", 60);
        InsumoEntity savedEntity = buildInsumoEntity("Fluido de freio DOT4", "29.90", 60);
        InsumoResponseDTO response = buildInsumoResponse(savedEntity);

        when(insumoDataMapper.toEntity(request)).thenReturn(entityToSave);
        when(insumoRepository.save(entityToSave)).thenReturn(savedEntity);
        when(insumoDataMapper.toResponse(savedEntity)).thenReturn(response);

        InsumoResponseDTO result = insumoServiceUsecase.create(request);

        assertEquals(response, result);
        verify(insumoDataMapper).toEntity(request);
        verify(insumoRepository).save(entityToSave);
        verify(insumoDataMapper).toResponse(savedEntity);
    }

    @Test
    void shouldUpdateInsumo() {
        UUID id = UUID.randomUUID();
        InsumoRequestDTO request = new InsumoRequestDTO("Aditivo radiador", new BigDecimal("25.00"), 40);
        InsumoEntity existing = buildInsumoEntity(id, "Nome Antigo", "10.00", 1);
        InsumoResponseDTO response = buildInsumoResponse(existing);

        when(insumoRepository.findById(id)).thenReturn(Optional.of(existing));
        when(insumoRepository.save(existing)).thenReturn(existing);
        when(insumoDataMapper.toResponse(existing)).thenReturn(response);

        InsumoResponseDTO result = insumoServiceUsecase.update(id, request);

        assertEquals(response, result);
        verify(insumoRepository).findById(id);
        verify(insumoDataMapper).updateEntity(existing, request);
        verify(insumoRepository).save(existing);
        verify(insumoDataMapper).toResponse(existing);
    }

    @Test
    void shouldThrowWhenUpdateAndInsumoDoesNotExist() {
        UUID id = UUID.randomUUID();
        InsumoRequestDTO request = new InsumoRequestDTO("Aditivo radiador", new BigDecimal("25.00"), 40);
        when(insumoRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> insumoServiceUsecase.update(id, request));

        assertTrue(exception.getMessage().contains("Insumo not found"));
        verify(insumoRepository).findById(id);
        verify(insumoRepository, never()).save(any());
    }

    @Test
    void shouldDeleteInsumo() {
        UUID id = UUID.randomUUID();
        when(insumoRepository.existsById(id)).thenReturn(true);

        insumoServiceUsecase.delete(id);

        verify(insumoRepository).existsById(id);
        verify(insumoRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteAndInsumoDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(insumoRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> insumoServiceUsecase.delete(id));

        assertTrue(exception.getMessage().contains("Insumo not found"));
        verify(insumoRepository).existsById(id);
        verify(insumoRepository, never()).deleteById(id);
    }

    private InsumoEntity buildInsumoEntity(String nome, String precoUnitario, Integer quantidadeEstoque) {
        return buildInsumoEntity(UUID.randomUUID(), nome, precoUnitario, quantidadeEstoque);
    }

    private InsumoEntity buildInsumoEntity(UUID id, String nome, String precoUnitario, Integer quantidadeEstoque) {
        InsumoEntity insumo = new InsumoEntity();
        insumo.setId(id);
        insumo.setNome(nome);
        insumo.setPrecoUnitario(new BigDecimal(precoUnitario));
        insumo.setQuantidadeEstoque(quantidadeEstoque);
        return insumo;
    }

    private InsumoResponseDTO buildInsumoResponse(InsumoEntity insumo) {
        return new InsumoResponseDTO(
                insumo.getId(),
                insumo.getNome(),
                insumo.getPrecoUnitario(),
                insumo.getQuantidadeEstoque());
    }
}
