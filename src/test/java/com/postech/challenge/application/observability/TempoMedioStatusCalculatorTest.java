package com.postech.challenge.application.observability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.postech.challenge.application.dto.StatusHistoricoPonto;
import com.postech.challenge.domain.model.StatusOrdemServico;

class TempoMedioStatusCalculatorTest {

    private final TempoMedioStatusCalculator calculator = new TempoMedioStatusCalculator();

    @Test
    void shouldAverageTrackedStatusesAndIgnoreUntrackedOnes() {
        UUID orderId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.of(2026, 8, 23, 8, 0);

        Map<StatusOrdemServico, Double> averages = calculator.averageMinutes(
                List.of(
                        new StatusHistoricoPonto(orderId, StatusOrdemServico.RECEBIDA, start),
                        new StatusHistoricoPonto(orderId, StatusOrdemServico.EM_DIAGNOSTICO, start.plusMinutes(30)),
                        new StatusHistoricoPonto(orderId, StatusOrdemServico.EM_EXECUCAO, start.plusMinutes(90)),
                        new StatusHistoricoPonto(orderId, StatusOrdemServico.FINALIZADA, start.plusMinutes(150))),
                start.plusMinutes(180));

        assertEquals(60.0, averages.get(StatusOrdemServico.EM_DIAGNOSTICO));
        assertEquals(60.0, averages.get(StatusOrdemServico.EM_EXECUCAO));
        assertEquals(30.0, averages.get(StatusOrdemServico.FINALIZADA));
        assertNull(averages.get(StatusOrdemServico.RECEBIDA));
    }

    @Test
    void shouldReturnNullWhenThereIsNoSample() {
        Map<StatusOrdemServico, Double> averages = calculator.averageMinutes(List.of(), LocalDateTime.now());

        assertNull(averages.get(StatusOrdemServico.EM_DIAGNOSTICO));
        assertNull(averages.get(StatusOrdemServico.EM_EXECUCAO));
        assertNull(averages.get(StatusOrdemServico.FINALIZADA));
    }
}
