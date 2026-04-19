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

import com.postech.challenge.application.dto.ClienteRequestDTO;
import com.postech.challenge.application.dto.ClienteResponseDTO;
import com.postech.challenge.application.mapper.ClienteDataMapper;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;
import com.postech.challenge.infrastructure.persistence.repository.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ClienteServiceUsecaseImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteDataMapper clienteDataMapper;

    @InjectMocks
    private ClienteServiceUsecaseImpl clienteServiceUsecase;

    @Test
    void shouldFindAllClientes() {
        ClienteEntity cliente = buildClienteEntity("Joao Silva", "123.456.789-00");
        ClienteResponseDTO response = new ClienteResponseDTO(cliente.getId(), cliente.getNome(), cliente.getCpfCnpj());

        when(clienteRepository.findAll()).thenReturn(List.of(cliente));
        when(clienteDataMapper.toResponse(cliente)).thenReturn(response);

        List<ClienteResponseDTO> result = clienteServiceUsecase.findAll();

        assertEquals(1, result.size());
        assertEquals(response, result.getFirst());
        verify(clienteRepository).findAll();
        verify(clienteDataMapper).toResponse(cliente);
    }

    @Test
    void shouldFindClienteById() {
        UUID id = UUID.randomUUID();
        ClienteEntity cliente = buildClienteEntity(id, "Maria Oliveira", "234.567.890-11");
        ClienteResponseDTO response = new ClienteResponseDTO(id, "Maria Oliveira", "234.567.890-11");

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(clienteDataMapper.toResponse(cliente)).thenReturn(response);

        ClienteResponseDTO result = clienteServiceUsecase.findById(id);

        assertEquals(response, result);
        verify(clienteRepository).findById(id);
        verify(clienteDataMapper).toResponse(cliente);
    }

    @Test
    void shouldThrowWhenFindByIdAndClienteDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> clienteServiceUsecase.findById(id));

        assertTrue(exception.getMessage().contains("Cliente not found"));
        verify(clienteRepository).findById(id);
    }

    @Test
    void shouldCreateCliente() {
        ClienteRequestDTO request = new ClienteRequestDTO("Carlos Pereira", "345.678.901-22");
        ClienteEntity entityToSave = buildClienteEntity("Carlos Pereira", "345.678.901-22");
        ClienteEntity savedEntity = buildClienteEntity("Carlos Pereira", "345.678.901-22");
        ClienteResponseDTO response = new ClienteResponseDTO(
                savedEntity.getId(),
                savedEntity.getNome(),
                savedEntity.getCpfCnpj());

        when(clienteRepository.existsByCpfCnpj(request.cpfCnpj())).thenReturn(false);
        when(clienteDataMapper.toEntity(request)).thenReturn(entityToSave);
        when(clienteRepository.save(entityToSave)).thenReturn(savedEntity);
        when(clienteDataMapper.toResponse(savedEntity)).thenReturn(response);

        ClienteResponseDTO result = clienteServiceUsecase.create(request);

        assertEquals(response, result);
        verify(clienteRepository).existsByCpfCnpj(request.cpfCnpj());
        verify(clienteDataMapper).toEntity(request);
        verify(clienteRepository).save(entityToSave);
        verify(clienteDataMapper).toResponse(savedEntity);
    }

    @Test
    void shouldThrowWhenCreateAndCpfCnpjAlreadyExists() {
        ClienteRequestDTO request = new ClienteRequestDTO("Joao Silva", "123.456.789-00");
        when(clienteRepository.existsByCpfCnpj(request.cpfCnpj())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clienteServiceUsecase.create(request));

        assertTrue(exception.getMessage().contains("CPF/CNPJ already registered"));
        verify(clienteRepository).existsByCpfCnpj(request.cpfCnpj());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void shouldUpdateClienteWhenCpfCnpjIsSame() {
        UUID id = UUID.randomUUID();
        ClienteRequestDTO request = new ClienteRequestDTO("Joao Atualizado", "123.456.789-00");
        ClienteEntity existing = buildClienteEntity(id, "Joao Silva", "123.456.789-00");
        ClienteResponseDTO response = new ClienteResponseDTO(id, "Joao Atualizado", "123.456.789-00");

        when(clienteRepository.findById(id)).thenReturn(Optional.of(existing));
        when(clienteRepository.save(existing)).thenReturn(existing);
        when(clienteDataMapper.toResponse(existing)).thenReturn(response);

        ClienteResponseDTO result = clienteServiceUsecase.update(id, request);

        assertEquals(response, result);
        verify(clienteRepository).findById(id);
        verify(clienteDataMapper).updateEntity(existing, request);
        verify(clienteRepository).save(existing);
        verify(clienteDataMapper).toResponse(existing);
        verify(clienteRepository, never()).existsByCpfCnpj(request.cpfCnpj());
    }

    @Test
    void shouldThrowWhenUpdateAndClienteDoesNotExist() {
        UUID id = UUID.randomUUID();
        ClienteRequestDTO request = new ClienteRequestDTO("Novo Nome", "123.456.789-00");
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> clienteServiceUsecase.update(id, request));

        assertTrue(exception.getMessage().contains("Cliente not found"));
        verify(clienteRepository).findById(id);
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUpdateAndCpfCnpjBelongsToAnotherCliente() {
        UUID id = UUID.randomUUID();
        ClienteRequestDTO request = new ClienteRequestDTO("Joao Atualizado", "999.999.999-99");
        ClienteEntity existing = buildClienteEntity(id, "Joao Silva", "123.456.789-00");

        when(clienteRepository.findById(id)).thenReturn(Optional.of(existing));
        when(clienteRepository.existsByCpfCnpj(request.cpfCnpj())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clienteServiceUsecase.update(id, request));

        assertTrue(exception.getMessage().contains("CPF/CNPJ already registered"));
        verify(clienteRepository).findById(id);
        verify(clienteRepository).existsByCpfCnpj(request.cpfCnpj());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void shouldDeleteCliente() {
        UUID id = UUID.randomUUID();
        when(clienteRepository.existsById(id)).thenReturn(true);

        clienteServiceUsecase.delete(id);

        verify(clienteRepository).existsById(id);
        verify(clienteRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteAndClienteDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(clienteRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> clienteServiceUsecase.delete(id));

        assertTrue(exception.getMessage().contains("Cliente not found"));
        verify(clienteRepository).existsById(id);
        verify(clienteRepository, never()).deleteById(id);
    }

    private ClienteEntity buildClienteEntity(String nome, String cpfCnpj) {
        return buildClienteEntity(UUID.randomUUID(), nome, cpfCnpj);
    }

    private ClienteEntity buildClienteEntity(UUID id, String nome, String cpfCnpj) {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setId(id);
        cliente.setNome(nome);
        cliente.setCpfCnpj(cpfCnpj);
        return cliente;
    }
}
