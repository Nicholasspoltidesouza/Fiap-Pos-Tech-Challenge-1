package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;

public interface MonitoramentoRepository {

    List<TempoMedioServicoProjection> findTempoMedioExecucaoPorServico();
}
