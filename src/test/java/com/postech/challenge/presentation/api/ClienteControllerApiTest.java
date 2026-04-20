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

import com.postech.challenge.application.dto.ClienteRequestDTO;
import com.postech.challenge.application.dto.ClienteResponseDTO;
import com.postech.challenge.application.usecase.ClienteServiceUsecase;

@ExtendWith(MockitoExtension.class)
class ClienteControllerApiTest {

    @Mock
    private ClienteServiceUsecase clienteServiceUsecase;

    @Test
    void shouldFindAll() {
        ClienteControllerApi controller = new ClienteControllerApi(clienteServiceUsecase);
        List<ClienteResponseDTO> clientes = List.of(new ClienteResponseDTO(UUID.randomUUID(), "Joao", "52998224725"));
        when(clienteServiceUsecase.findAll()).thenReturn(clientes);

        ResponseEntity<List<ClienteResponseDTO>> response = controller.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(clientes, response.getBody());
    }

    @Test
    void shouldFindById() {
        ClienteControllerApi controller = new ClienteControllerApi(clienteServiceUsecase);
        UUID id = UUID.randomUUID();
        ClienteResponseDTO cliente = new ClienteResponseDTO(id, "Maria", "11144477735");
        when(clienteServiceUsecase.findById(id)).thenReturn(cliente);

        ResponseEntity<ClienteResponseDTO> response = controller.findById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cliente, response.getBody());
    }

    @Test
    void shouldCreate() {
        ClienteControllerApi controller = new ClienteControllerApi(clienteServiceUsecase);
        ClienteRequestDTO request = new ClienteRequestDTO("Carlos", "12345678909");
        ClienteResponseDTO created = new ClienteResponseDTO(UUID.randomUUID(), "Carlos", "12345678909");
        when(clienteServiceUsecase.create(request)).thenReturn(created);

        ResponseEntity<ClienteResponseDTO> response = controller.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(created, response.getBody());
    }

    @Test
    void shouldUpdate() {
        ClienteControllerApi controller = new ClienteControllerApi(clienteServiceUsecase);
        UUID id = UUID.randomUUID();
        ClienteRequestDTO request = new ClienteRequestDTO("Carlos Atualizado", "12345678909");
        ClienteResponseDTO updated = new ClienteResponseDTO(id, "Carlos Atualizado", "12345678909");
        when(clienteServiceUsecase.update(id, request)).thenReturn(updated);

        ResponseEntity<ClienteResponseDTO> response = controller.update(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
    }

    @Test
    void shouldDelete() {
        ClienteControllerApi controller = new ClienteControllerApi(clienteServiceUsecase);
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = controller.delete(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(clienteServiceUsecase).delete(id);
    }
}
