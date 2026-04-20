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

import com.postech.challenge.application.dto.InsumoRequestDTO;
import com.postech.challenge.application.dto.InsumoResponseDTO;
import com.postech.challenge.application.usecase.InsumoServiceUsecase;

@ExtendWith(MockitoExtension.class)
class InsumoControllerApiTest {

    @Mock
    private InsumoServiceUsecase insumoServiceUsecase;

    @Test
    void shouldFindAll() {
        InsumoControllerApi controller = new InsumoControllerApi(insumoServiceUsecase);
        List<InsumoResponseDTO> insumos = List.of(
                new InsumoResponseDTO(UUID.randomUUID(), "Oleo", BigDecimal.valueOf(40), 10));
        when(insumoServiceUsecase.findAll()).thenReturn(insumos);

        ResponseEntity<List<InsumoResponseDTO>> response = controller.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(insumos, response.getBody());
    }

    @Test
    void shouldFindById() {
        InsumoControllerApi controller = new InsumoControllerApi(insumoServiceUsecase);
        UUID id = UUID.randomUUID();
        InsumoResponseDTO insumo = new InsumoResponseDTO(id, "Oleo", BigDecimal.valueOf(40), 10);
        when(insumoServiceUsecase.findById(id)).thenReturn(insumo);

        ResponseEntity<InsumoResponseDTO> response = controller.findById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(insumo, response.getBody());
    }

    @Test
    void shouldCreate() {
        InsumoControllerApi controller = new InsumoControllerApi(insumoServiceUsecase);
        InsumoRequestDTO request = new InsumoRequestDTO("Oleo", BigDecimal.valueOf(40), 10);
        InsumoResponseDTO created = new InsumoResponseDTO(UUID.randomUUID(), "Oleo", BigDecimal.valueOf(40), 10);
        when(insumoServiceUsecase.create(request)).thenReturn(created);

        ResponseEntity<InsumoResponseDTO> response = controller.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(created, response.getBody());
    }

    @Test
    void shouldUpdate() {
        InsumoControllerApi controller = new InsumoControllerApi(insumoServiceUsecase);
        UUID id = UUID.randomUUID();
        InsumoRequestDTO request = new InsumoRequestDTO("Oleo Premium", BigDecimal.valueOf(60), 5);
        InsumoResponseDTO updated = new InsumoResponseDTO(id, "Oleo Premium", BigDecimal.valueOf(60), 5);
        when(insumoServiceUsecase.update(id, request)).thenReturn(updated);

        ResponseEntity<InsumoResponseDTO> response = controller.update(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
    }

    @Test
    void shouldDelete() {
        InsumoControllerApi controller = new InsumoControllerApi(insumoServiceUsecase);
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = controller.delete(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(insumoServiceUsecase).delete(id);
    }
}
