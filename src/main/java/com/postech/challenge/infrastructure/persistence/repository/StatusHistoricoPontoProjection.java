package com.postech.challenge.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import com.postech.challenge.domain.model.StatusOrdemServico;

public interface StatusHistoricoPontoProjection {

    UUID getOrdemServicoId();

    StatusOrdemServico getStatus();

    LocalDateTime getAlteradoEm();
}
