package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class MonitoramentoRepositoryImpl implements MonitoramentoRepository {

    private final MonitoramentoJpaRepository monitoramentoJpaRepository;

    public MonitoramentoRepositoryImpl(MonitoramentoJpaRepository monitoramentoJpaRepository) {
        this.monitoramentoJpaRepository = monitoramentoJpaRepository;
    }

    @Override
    public List<TempoMedioServicoProjection> findTempoMedioExecucaoPorServico() {
        return monitoramentoJpaRepository.findTempoMedioExecucaoPorServico();
    }
}
