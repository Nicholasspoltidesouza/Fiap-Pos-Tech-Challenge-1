package com.postech.challenge.presentation.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.postech.challenge.application.dto.VeiculoRequestDTO;
import com.postech.challenge.application.dto.VeiculoResponseDTO;
import com.postech.challenge.application.usecase.VeiculoServiceUsecase;

@ExtendWith(MockitoExtension.class)
class VeiculoControllerApiTest {

    @Mock
    private VeiculoServiceUsecase veiculoServiceUsecase;

    @Test
    void shouldFindAll() {
        VeiculoControllerApi controller = new VeiculoControllerApi(veiculoServiceUsecase);
        List<VeiculoResponseDTO> veiculos = List.of(
                new VeiculoResponseDTO(UUID.randomUUID(), "Toyota", "Corolla", "ABC1D23", 2022, UUID.randomUUID()));
        when(veiculoServiceUsecase.findAll()).thenReturn(veiculos);

        ResponseEntity<List<VeiculoResponseDTO>> response = controller.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(veiculos, response.getBody());
    }

    @Test
    void shouldFindById() {
        VeiculoControllerApi controller = new VeiculoControllerApi(veiculoServiceUsecase);
        UUID id = UUID.randomUUID();
        VeiculoResponseDTO veiculo = new VeiculoResponseDTO(id, "Toyota", "Corolla", "ABC1D23", 2022, UUID.randomUUID());
        when(veiculoServiceUsecase.findById(id)).thenReturn(veiculo);

        ResponseEntity<VeiculoResponseDTO> response = controller.findById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(veiculo, response.getBody());
    }

    @Test
    void shouldCreate() {
        VeiculoControllerApi controller = new VeiculoControllerApi(veiculoServiceUsecase);
        VeiculoRequestDTO request = new VeiculoRequestDTO("Toyota", "Corolla", "ABC1D23", 2022, UUID.randomUUID());
        VeiculoResponseDTO created = new VeiculoResponseDTO(UUID.randomUUID(), "Toyota", "Corolla", "ABC1D23", 2022, request.clienteId());
        when(veiculoServiceUsecase.create(request)).thenReturn(created);

        ResponseEntity<VeiculoResponseDTO> response = controller.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(created, response.getBody());
    }

    @Test
    void shouldUpdate() {
        VeiculoControllerApi controller = new VeiculoControllerApi(veiculoServiceUsecase);
        UUID id = UUID.randomUUID();
        VeiculoRequestDTO request = new VeiculoRequestDTO("Honda", "Civic", "XYZ1A23", 2021, UUID.randomUUID());
        VeiculoResponseDTO updated = new VeiculoResponseDTO(id, "Honda", "Civic", "XYZ1A23", 2021, request.clienteId());
        when(veiculoServiceUsecase.update(id, request)).thenReturn(updated);

        ResponseEntity<VeiculoResponseDTO> response = controller.update(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
    }

    @Test
    void shouldDelete() {
        VeiculoControllerApi controller = new VeiculoControllerApi(veiculoServiceUsecase);
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = controller.delete(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(veiculoServiceUsecase).delete(id);
    }
}
