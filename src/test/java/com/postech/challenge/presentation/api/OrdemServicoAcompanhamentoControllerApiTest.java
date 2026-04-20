package com.postech.challenge.presentation.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.postech.challenge.application.dto.AcompanhamentoOrdemServicoResponseDTO;
import com.postech.challenge.application.usecase.OrdemServicoServiceUsecase;

@ExtendWith(MockitoExtension.class)
class OrdemServicoAcompanhamentoControllerApiTest {

    @Mock
    private OrdemServicoServiceUsecase ordemServicoServiceUsecase;

    @Test
    void shouldAcompanharOrdem() {
        OrdemServicoAcompanhamentoControllerApi controller = new OrdemServicoAcompanhamentoControllerApi(ordemServicoServiceUsecase);
        UUID id = UUID.randomUUID();
        String cpfCnpj = "52998224725";
        AcompanhamentoOrdemServicoResponseDTO dto = new AcompanhamentoOrdemServicoResponseDTO(
                id,
                cpfCnpj,
                "EM_EXECUCAO",
                BigDecimal.valueOf(250),
                true,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(12),
                null);
        when(ordemServicoServiceUsecase.consultarAcompanhamento(id, cpfCnpj)).thenReturn(dto);

        ResponseEntity<AcompanhamentoOrdemServicoResponseDTO> response = controller.acompanharOrdem(id, cpfCnpj);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(ordemServicoServiceUsecase).consultarAcompanhamento(id, cpfCnpj);
    }
}
