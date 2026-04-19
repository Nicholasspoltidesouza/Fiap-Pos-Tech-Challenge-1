package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;

public abstract class OrdemServicoRepository {
    public abstract List<OrdemServicoEntity> findAll();

    public abstract Optional<OrdemServicoEntity> findById(UUID id);

    public abstract OrdemServicoEntity save(OrdemServicoEntity ordemServico);

    public abstract boolean existsById(UUID id);

    public abstract void deleteById(UUID id);
}
