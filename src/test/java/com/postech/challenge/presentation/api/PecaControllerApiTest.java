package com.postech.challenge.presentation.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.postech.challenge.application.dto.PecaRequestDTO;
import com.postech.challenge.application.dto.PecaResponseDTO;
import com.postech.challenge.application.usecase.PecaServiceUsecase;

@ExtendWith(MockitoExtension.class)
class PecaControllerApiTest {

    @Mock
    private PecaServiceUsecase pecaServiceUsecase;

    @Test
    void shouldFindAll() {
        PecaControllerApi controller = new PecaControllerApi(pecaServiceUsecase);
        List<PecaResponseDTO> pecas = List.of(
                new PecaResponseDTO(UUID.randomUUID(), "Filtro", BigDecimal.valueOf(80), 5));
        when(pecaServiceUsecase.findAll()).thenReturn(pecas);

        ResponseEntity<List<PecaResponseDTO>> response = controller.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pecas, response.getBody());
    }

    @Test
    void shouldFindById() {
        PecaControllerApi controller = new PecaControllerApi(pecaServiceUsecase);
        UUID id = UUID.randomUUID();
        PecaResponseDTO peca = new PecaResponseDTO(id, "Filtro", BigDecimal.valueOf(80), 5);
        when(pecaServiceUsecase.findById(id)).thenReturn(peca);

        ResponseEntity<PecaResponseDTO> response = controller.findById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(peca, response.getBody());
    }

    @Test
    void shouldCreate() {
        PecaControllerApi controller = new PecaControllerApi(pecaServiceUsecase);
        PecaRequestDTO request = new PecaRequestDTO("Filtro", BigDecimal.valueOf(80), 5);
        PecaResponseDTO created = new PecaResponseDTO(UUID.randomUUID(), "Filtro", BigDecimal.valueOf(80), 5);
        when(pecaServiceUsecase.create(request)).thenReturn(created);

        ResponseEntity<PecaResponseDTO> response = controller.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(created, response.getBody());
    }

    @Test
    void shouldUpdate() {
        PecaControllerApi controller = new PecaControllerApi(pecaServiceUsecase);
        UUID id = UUID.randomUUID();
        PecaRequestDTO request = new PecaRequestDTO("Filtro Premium", BigDecimal.valueOf(120), 3);
        PecaResponseDTO updated = new PecaResponseDTO(id, "Filtro Premium", BigDecimal.valueOf(120), 3);
        when(pecaServiceUsecase.update(id, request)).thenReturn(updated);

        ResponseEntity<PecaResponseDTO> response = controller.update(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
    }

    @Test
    void shouldDelete() {
        PecaControllerApi controller = new PecaControllerApi(pecaServiceUsecase);
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = controller.delete(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(pecaServiceUsecase).delete(id);
    }
}
