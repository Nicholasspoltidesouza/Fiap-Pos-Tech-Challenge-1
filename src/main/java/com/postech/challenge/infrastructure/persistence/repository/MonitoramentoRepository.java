package com.postech.challenge.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.postech.challenge.application.dto.StatusHistoricoPonto;

public interface MonitoramentoRepository {

    List<TempoMedioServicoProjection> findTempoMedioExecucaoPorServico();

    long countOrdensAbertasDesde(LocalDateTime inicio);

    List<StatusHistoricoPonto> findHistoricoStatus();
}
