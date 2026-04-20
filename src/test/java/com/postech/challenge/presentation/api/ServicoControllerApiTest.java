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

import com.postech.challenge.application.dto.ServicoRequestDTO;
import com.postech.challenge.application.dto.ServicoResponseDTO;
import com.postech.challenge.application.usecase.ServicoServiceUsecase;

@ExtendWith(MockitoExtension.class)
class ServicoControllerApiTest {

    @Mock
    private ServicoServiceUsecase servicoServiceUsecase;

    @Test
    void shouldFindAll() {
        ServicoControllerApi controller = new ServicoControllerApi(servicoServiceUsecase);
        List<ServicoResponseDTO> servicos = List.of(new ServicoResponseDTO(UUID.randomUUID(), "Troca de oleo", "Servico completo"));
        when(servicoServiceUsecase.findAll()).thenReturn(servicos);

        ResponseEntity<List<ServicoResponseDTO>> response = controller.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(servicos, response.getBody());
    }

    @Test
    void shouldFindById() {
        ServicoControllerApi controller = new ServicoControllerApi(servicoServiceUsecase);
        UUID id = UUID.randomUUID();
        ServicoResponseDTO servico = new ServicoResponseDTO(id, "Alinhamento", "Geometria");
        when(servicoServiceUsecase.findById(id)).thenReturn(servico);

        ResponseEntity<ServicoResponseDTO> response = controller.findById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(servico, response.getBody());
    }

    @Test
    void shouldCreate() {
        ServicoControllerApi controller = new ServicoControllerApi(servicoServiceUsecase);
        ServicoRequestDTO request = new ServicoRequestDTO("Troca de oleo", "Servico completo");
        ServicoResponseDTO created = new ServicoResponseDTO(UUID.randomUUID(), "Troca de oleo", "Servico completo");
        when(servicoServiceUsecase.create(request)).thenReturn(created);

        ResponseEntity<ServicoResponseDTO> response = controller.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(created, response.getBody());
    }

    @Test
    void shouldUpdate() {
        ServicoControllerApi controller = new ServicoControllerApi(servicoServiceUsecase);
        UUID id = UUID.randomUUID();
        ServicoRequestDTO request = new ServicoRequestDTO("Revisao", "Revisao geral");
        ServicoResponseDTO updated = new ServicoResponseDTO(id, "Revisao", "Revisao geral");
        when(servicoServiceUsecase.update(id, request)).thenReturn(updated);

        ResponseEntity<ServicoResponseDTO> response = controller.update(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
    }

    @Test
    void shouldDelete() {
        ServicoControllerApi controller = new ServicoControllerApi(servicoServiceUsecase);
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = controller.delete(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(servicoServiceUsecase).delete(id);
    }
}
