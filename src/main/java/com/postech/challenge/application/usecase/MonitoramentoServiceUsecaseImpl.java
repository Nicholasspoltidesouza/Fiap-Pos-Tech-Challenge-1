package com.postech.challenge.application.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.postech.challenge.application.dto.DashboardOperacionalResponseDTO;
import com.postech.challenge.application.dto.TempoMedioServicoResponseDTO;
import com.postech.challenge.application.observability.ObservabilidadeServico;
import com.postech.challenge.application.observability.TempoMedioStatusCalculator;
import com.postech.challenge.domain.model.StatusOrdemServico;
import com.postech.challenge.infrastructure.persistence.repository.MonitoramentoRepository;

@Service
public class MonitoramentoServiceUsecaseImpl extends MonitoramentoServiceUsecase {

    private final MonitoramentoRepository monitoramentoRepository;
    private final ObservabilidadeServico observabilidadeServico;
    private final TempoMedioStatusCalculator tempoMedioStatusCalculator = new TempoMedioStatusCalculator();

    public MonitoramentoServiceUsecaseImpl(
            MonitoramentoRepository monitoramentoRepository,
            ObservabilidadeServico observabilidadeServico) {
        this.monitoramentoRepository = monitoramentoRepository;
        this.observabilidadeServico = observabilidadeServico;
    }

    @Override
    public List<TempoMedioServicoResponseDTO> findTempoMedioExecucaoPorServico() {
        return monitoramentoRepository.findTempoMedioExecucaoPorServico()
                .stream()
                .map(item -> new TempoMedioServicoResponseDTO(
                        item.getServicoId(),
                        item.getServicoNome(),
                        item.getQuantidadeOrdensConcluidas(),
                        item.getTempoMedioMinutos()))
                .toList();
    }

    @Override
    public DashboardOperacionalResponseDTO getDashboardOperacional() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        long volumeDiario = monitoramentoRepository.countOrdensAbertasDesde(startOfDay);
        Map<StatusOrdemServico, Double> averages = tempoMedioStatusCalculator.averageMinutes(
                monitoramentoRepository.findHistoricoStatus(),
                LocalDateTime.now());
        Double diagnostico = averages.get(StatusOrdemServico.EM_DIAGNOSTICO);
        Double execucao = averages.get(StatusOrdemServico.EM_EXECUCAO);
        Double finalizacao = averages.get(StatusOrdemServico.FINALIZADA);
        observabilidadeServico.atualizarDashboard(volumeDiario, diagnostico, execucao, finalizacao);
        return new DashboardOperacionalResponseDTO(
                today,
                volumeDiario,
                diagnostico,
                execucao,
                finalizacao,
                observabilidadeServico.contarFalhasProcessamento(),
                observabilidadeServico.contarErrosIntegracao());
    }
}
