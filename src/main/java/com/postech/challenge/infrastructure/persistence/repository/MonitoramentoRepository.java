package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;

public abstract class MonitoramentoRepository {
    public abstract List<TempoMedioServicoProjection> findTempoMedioExecucaoPorServico();
}
