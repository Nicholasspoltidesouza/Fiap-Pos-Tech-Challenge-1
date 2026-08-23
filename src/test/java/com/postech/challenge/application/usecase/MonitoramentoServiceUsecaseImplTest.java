package com.postech.challenge.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.challenge.application.dto.DashboardOperacionalResponseDTO;
import com.postech.challenge.application.dto.StatusHistoricoPonto;
import com.postech.challenge.application.dto.TempoMedioServicoResponseDTO;
import com.postech.challenge.application.observability.ObservabilidadeServico;
import com.postech.challenge.domain.model.StatusOrdemServico;
import com.postech.challenge.infrastructure.persistence.repository.MonitoramentoRepository;
import com.postech.challenge.infrastructure.persistence.repository.TempoMedioServicoProjection;

@ExtendWith(MockitoExtension.class)
class MonitoramentoServiceUsecaseImplTest {

    @Mock
    private MonitoramentoRepository monitoramentoRepository;

    @Mock
    private ObservabilidadeServico observabilidadeServico;

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

    @Test
    void shouldReturnOperationalDashboard() {
        UUID orderId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().minusHours(3);
        when(monitoramentoRepository.countOrdensAbertasDesde(LocalDate.now().atStartOfDay())).thenReturn(4L);
        when(monitoramentoRepository.findHistoricoStatus()).thenReturn(List.of(
                new StatusHistoricoPonto(orderId, StatusOrdemServico.EM_DIAGNOSTICO, start),
                new StatusHistoricoPonto(orderId, StatusOrdemServico.EM_EXECUCAO, start.plusHours(1)),
                new StatusHistoricoPonto(orderId, StatusOrdemServico.FINALIZADA, start.plusHours(2))));
        when(observabilidadeServico.contarFalhasProcessamento()).thenReturn(2.0);
        when(observabilidadeServico.contarErrosIntegracao()).thenReturn(1.0);

        DashboardOperacionalResponseDTO dashboard = monitoramentoServiceUsecase.getDashboardOperacional();

        assertEquals(LocalDate.now(), dashboard.dataReferencia());
        assertEquals(4L, dashboard.volumeDiarioOrdens());
        assertEquals(60.0, dashboard.tempoMedioDiagnosticoMinutos(), 0.01);
        assertEquals(60.0, dashboard.tempoMedioExecucaoMinutos(), 0.01);
        assertTrue(dashboard.tempoMedioFinalizacaoMinutos() >= 0);
        assertEquals(2.0, dashboard.falhasProcessamento());
        assertEquals(1.0, dashboard.errosIntegracao());
        verify(observabilidadeServico).atualizarDashboard(
                4L,
                dashboard.tempoMedioDiagnosticoMinutos(),
                dashboard.tempoMedioExecucaoMinutos(),
                dashboard.tempoMedioFinalizacaoMinutos());
    }
}
