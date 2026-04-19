package com.postech.challenge.infrastructure.persistence.repository;

import java.util.UUID;

public interface TempoMedioServicoProjection {
    UUID getServicoId();

    String getServicoNome();

    Long getQuantidadeOrdensConcluidas();

    Double getTempoMedioMinutos();
}
