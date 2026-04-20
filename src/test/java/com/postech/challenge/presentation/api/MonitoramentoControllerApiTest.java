package com.postech.challenge.presentation.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.postech.challenge.application.dto.TempoMedioServicoResponseDTO;
import com.postech.challenge.application.usecase.MonitoramentoServiceUsecase;

@ExtendWith(MockitoExtension.class)
class MonitoramentoControllerApiTest {

    @Mock
    private MonitoramentoServiceUsecase monitoramentoServiceUsecase;

    @Test
    void shouldFindTempoMedioExecucaoPorServico() {
        MonitoramentoControllerApi controller = new MonitoramentoControllerApi(monitoramentoServiceUsecase);
        List<TempoMedioServicoResponseDTO> responseDTO = List.of(
                new TempoMedioServicoResponseDTO(UUID.randomUUID(), "Troca de oleo", 3L, 120.0));
        when(monitoramentoServiceUsecase.findTempoMedioExecucaoPorServico()).thenReturn(responseDTO);

        ResponseEntity<List<TempoMedioServicoResponseDTO>> response = controller.findTempoMedioExecucaoPorServico();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }
}
