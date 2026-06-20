package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;

public interface OrdemServicoRepository {

    List<OrdemServicoEntity> findAll();

    List<OrdemServicoEntity> findAtivasOrdenadas();

    Optional<OrdemServicoEntity> findById(UUID id);

    OrdemServicoEntity save(OrdemServicoEntity ordemServico);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
