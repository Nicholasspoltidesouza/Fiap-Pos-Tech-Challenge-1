package com.postech.challenge.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import com.postech.challenge.application.dto.TempoMedioServicoResponseDTO;
import com.postech.challenge.infrastructure.persistence.repository.MonitoramentoRepository;

@Service
public class MonitoramentoServiceUsecaseImpl extends MonitoramentoServiceUsecase {

    private final MonitoramentoRepository monitoramentoRepository;

    public MonitoramentoServiceUsecaseImpl(MonitoramentoRepository monitoramentoRepository) {
        this.monitoramentoRepository = monitoramentoRepository;
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
}
