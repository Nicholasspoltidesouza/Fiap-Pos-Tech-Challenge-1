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

import com.postech.challenge.application.dto.VeiculoRequestDTO;
import com.postech.challenge.application.dto.VeiculoResponseDTO;
import com.postech.challenge.application.mapper.VeiculoDataMapper;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;
import com.postech.challenge.infrastructure.persistence.entity.VeiculoEntity;
import com.postech.challenge.infrastructure.persistence.repository.ClienteRepository;
import com.postech.challenge.infrastructure.persistence.repository.VeiculoRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceUsecaseImplTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeiculoDataMapper veiculoDataMapper;

    @InjectMocks
    private VeiculoServiceUsecaseImpl veiculoServiceUsecase;

    @Test
    void shouldFindAllVeiculos() {
        VeiculoEntity veiculo = buildVeiculoEntity("Toyota", "Corolla", "ABC1D23", 2020, UUID.randomUUID());
        VeiculoResponseDTO response = buildVeiculoResponse(veiculo);

        when(veiculoRepository.findAll()).thenReturn(List.of(veiculo));
        when(veiculoDataMapper.toResponse(veiculo)).thenReturn(response);

        List<VeiculoResponseDTO> result = veiculoServiceUsecase.findAll();

        assertEquals(1, result.size());
        assertEquals(response, result.getFirst());
        verify(veiculoRepository).findAll();
        verify(veiculoDataMapper).toResponse(veiculo);
    }

    @Test
    void shouldFindVeiculoById() {
        UUID id = UUID.randomUUID();
        VeiculoEntity veiculo = buildVeiculoEntity(id, "Honda", "Civic", "DEF2G34", 2019, UUID.randomUUID());
        VeiculoResponseDTO response = buildVeiculoResponse(veiculo);

        when(veiculoRepository.findById(id)).thenReturn(Optional.of(veiculo));
        when(veiculoDataMapper.toResponse(veiculo)).thenReturn(response);

        VeiculoResponseDTO result = veiculoServiceUsecase.findById(id);

        assertEquals(response, result);
        verify(veiculoRepository).findById(id);
        verify(veiculoDataMapper).toResponse(veiculo);
    }

    @Test
    void shouldThrowWhenFindByIdAndVeiculoDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(veiculoRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> veiculoServiceUsecase.findById(id));

        assertTrue(exception.getMessage().contains("Veiculo not found"));
        verify(veiculoRepository).findById(id);
    }

    @Test
    void shouldCreateVeiculo() {
        UUID clienteId = UUID.randomUUID();
        VeiculoRequestDTO request = new VeiculoRequestDTO("Chevrolet", "Onix", "GHI3J45", 2022, clienteId);
        ClienteEntity cliente = buildClienteEntity(clienteId);
        VeiculoEntity entityToSave = buildVeiculoEntity("Chevrolet", "Onix", "GHI3J45", 2022, clienteId);
        VeiculoEntity savedEntity = buildVeiculoEntity("Chevrolet", "Onix", "GHI3J45", 2022, clienteId);
        VeiculoResponseDTO response = buildVeiculoResponse(savedEntity);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.existsByPlaca("GHI3J45")).thenReturn(false);
        when(veiculoDataMapper.toEntity(request, cliente)).thenReturn(entityToSave);
        when(veiculoRepository.save(entityToSave)).thenReturn(savedEntity);
        when(veiculoDataMapper.toResponse(savedEntity)).thenReturn(response);

        VeiculoResponseDTO result = veiculoServiceUsecase.create(request);

        assertEquals(response, result);
        verify(clienteRepository).findById(clienteId);
        verify(veiculoDataMapper).toEntity(request, cliente);
        verify(veiculoRepository).save(entityToSave);
        verify(veiculoDataMapper).toResponse(savedEntity);
    }

    @Test
    void shouldThrowWhenCreateAndClienteDoesNotExist() {
        UUID clienteId = UUID.randomUUID();
        VeiculoRequestDTO request = new VeiculoRequestDTO("Chevrolet", "Onix", "GHI3J45", 2022, clienteId);
        when(veiculoRepository.existsByPlaca("GHI3J45")).thenReturn(false);
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> veiculoServiceUsecase.create(request));

        assertTrue(exception.getMessage().contains("Cliente not found"));
        verify(clienteRepository).findById(clienteId);
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void shouldUpdateVeiculo() {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        VeiculoRequestDTO request = new VeiculoRequestDTO("Hyundai", "HB20", "JKL4M56", 2023, clienteId);
        VeiculoEntity existing = buildVeiculoEntity(id, "Fiat", "Argo", "ZZZ9Z99", 2018, UUID.randomUUID());
        ClienteEntity cliente = buildClienteEntity(clienteId);
        VeiculoResponseDTO response = new VeiculoResponseDTO(id, "Hyundai", "HB20", "JKL4M56", 2023, clienteId);

        when(veiculoRepository.findById(id)).thenReturn(Optional.of(existing));
        when(veiculoRepository.existsByPlaca("JKL4M56")).thenReturn(false);
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.save(existing)).thenReturn(existing);
        when(veiculoDataMapper.toResponse(existing)).thenReturn(response);

        VeiculoResponseDTO result = veiculoServiceUsecase.update(id, request);

        assertEquals(response, result);
        verify(veiculoRepository).findById(id);
        verify(clienteRepository).findById(clienteId);
        verify(veiculoDataMapper).updateEntity(existing, request, cliente);
        verify(veiculoRepository).save(existing);
        verify(veiculoDataMapper).toResponse(existing);
    }

    @Test
    void shouldThrowWhenUpdateAndVeiculoDoesNotExist() {
        UUID id = UUID.randomUUID();
        VeiculoRequestDTO request = new VeiculoRequestDTO("Hyundai", "HB20", "JKL4M56", 2023, UUID.randomUUID());
        when(veiculoRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> veiculoServiceUsecase.update(id, request));

        assertTrue(exception.getMessage().contains("Veiculo not found"));
        verify(veiculoRepository).findById(id);
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUpdateAndClienteDoesNotExist() {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        VeiculoRequestDTO request = new VeiculoRequestDTO("Hyundai", "HB20", "JKL4M56", 2023, clienteId);
        VeiculoEntity existing = buildVeiculoEntity(id, "Fiat", "Argo", "ZZZ9Z99", 2018, UUID.randomUUID());

        when(veiculoRepository.findById(id)).thenReturn(Optional.of(existing));
        when(veiculoRepository.existsByPlaca("JKL4M56")).thenReturn(false);
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> veiculoServiceUsecase.update(id, request));

        assertTrue(exception.getMessage().contains("Cliente not found"));
        verify(veiculoRepository).findById(id);
        verify(clienteRepository).findById(clienteId);
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreateAndPlacaIsInvalid() {
        VeiculoRequestDTO request = new VeiculoRequestDTO("Toyota", "Corolla", "INVALIDA", 2020, UUID.randomUUID());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> veiculoServiceUsecase.create(request));

        assertTrue(exception.getMessage().contains("Invalid placa format"));
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreateAndPlacaAlreadyExists() {
        UUID clienteId = UUID.randomUUID();
        VeiculoRequestDTO request = new VeiculoRequestDTO("Toyota", "Corolla", "ABC1D23", 2020, clienteId);
        when(veiculoRepository.existsByPlaca("ABC1D23")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> veiculoServiceUsecase.create(request));

        assertTrue(exception.getMessage().contains("Placa already registered"));
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void shouldDeleteVeiculo() {
        UUID id = UUID.randomUUID();
        when(veiculoRepository.existsById(id)).thenReturn(true);

        veiculoServiceUsecase.delete(id);

        verify(veiculoRepository).existsById(id);
        verify(veiculoRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteAndVeiculoDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(veiculoRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> veiculoServiceUsecase.delete(id));

        assertTrue(exception.getMessage().contains("Veiculo not found"));
        verify(veiculoRepository).existsById(id);
        verify(veiculoRepository, never()).deleteById(id);
    }

    private VeiculoEntity buildVeiculoEntity(String marca, String modelo, String placa, Integer ano, UUID clienteId) {
        return buildVeiculoEntity(UUID.randomUUID(), marca, modelo, placa, ano, clienteId);
    }

    private VeiculoEntity buildVeiculoEntity(UUID id, String marca, String modelo, String placa, Integer ano, UUID clienteId) {
        VeiculoEntity veiculo = new VeiculoEntity();
        veiculo.setId(id);
        veiculo.setMarca(marca);
        veiculo.setModelo(modelo);
        veiculo.setPlaca(placa);
        veiculo.setAno(ano);
        veiculo.setCliente(buildClienteEntity(clienteId));
        return veiculo;
    }

    private ClienteEntity buildClienteEntity(UUID id) {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setId(id);
        return cliente;
    }

    private VeiculoResponseDTO buildVeiculoResponse(VeiculoEntity veiculo) {
        return new VeiculoResponseDTO(
                veiculo.getId(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getPlaca(),
                veiculo.getAno(),
                veiculo.getCliente().getId());
    }
}
