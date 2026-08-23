package com.postech.challenge.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.postech.challenge.application.dto.StatusHistoricoPonto;

@Repository
public class MonitoramentoRepositoryImpl implements MonitoramentoRepository {

    private final MonitoramentoJpaRepository monitoramentoJpaRepository;
    private final OrdemServicoJpaRepository ordemServicoJpaRepository;
    private final OrdemServicoStatusHistoricoJpaRepository statusHistoricoJpaRepository;

    public MonitoramentoRepositoryImpl(
            MonitoramentoJpaRepository monitoramentoJpaRepository,
            OrdemServicoJpaRepository ordemServicoJpaRepository,
            OrdemServicoStatusHistoricoJpaRepository statusHistoricoJpaRepository) {
        this.monitoramentoJpaRepository = monitoramentoJpaRepository;
        this.ordemServicoJpaRepository = ordemServicoJpaRepository;
        this.statusHistoricoJpaRepository = statusHistoricoJpaRepository;
    }

    @Override
    public List<TempoMedioServicoProjection> findTempoMedioExecucaoPorServico() {
        return monitoramentoJpaRepository.findTempoMedioExecucaoPorServico();
    }

    @Override
    public long countOrdensAbertasDesde(LocalDateTime inicio) {
        return ordemServicoJpaRepository.countByDataAberturaGreaterThanEqual(inicio);
    }

    @Override
    public List<StatusHistoricoPonto> findHistoricoStatus() {
        return statusHistoricoJpaRepository.findAllPontos()
                .stream()
                .map(item -> new StatusHistoricoPonto(
                        item.getOrdemServicoId(),
                        item.getStatus(),
                        item.getAlteradoEm()))
                .toList();
    }
}
