package com.postech.challenge.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.challenge.application.dto.TempoMedioServicoResponseDTO;
import com.postech.challenge.infrastructure.persistence.repository.MonitoramentoRepository;
import com.postech.challenge.infrastructure.persistence.repository.TempoMedioServicoProjection;

@ExtendWith(MockitoExtension.class)
class MonitoramentoServiceUsecaseImplTest {

    @Mock
    private MonitoramentoRepository monitoramentoRepository;

    @InjectMocks
    private MonitoramentoServiceUsecaseImpl monitoramentoServiceUsecase;

    @Test
    void shouldReturnTempoMedioExecucaoPorServico() {
        UUID servicoId = UUID.randomUUID();
        TempoMedioServicoProjection projection = new TempoMedioServicoProjection() {
            @Override
            public UUID getServicoId() {
                return servicoId;
            }

            @Override
            public String getServicoNome() {
                return "Troca de oleo";
            }

            @Override
            public Long getQuantidadeOrdensConcluidas() {
                return 12L;
            }

            @Override
            public Double getTempoMedioMinutos() {
                return 95.5;
            }
        };

        when(monitoramentoRepository.findTempoMedioExecucaoPorServico()).thenReturn(List.of(projection));

        List<TempoMedioServicoResponseDTO> result = monitoramentoServiceUsecase.findTempoMedioExecucaoPorServico();

        assertEquals(1, result.size());
        assertEquals(servicoId, result.getFirst().servicoId());
        assertEquals("Troca de oleo", result.getFirst().servicoNome());
        assertEquals(12L, result.getFirst().quantidadeOrdensConcluidas());
        assertEquals(95.5, result.getFirst().tempoMedioMinutos());
        verify(monitoramentoRepository).findTempoMedioExecucaoPorServico();
    }
}
