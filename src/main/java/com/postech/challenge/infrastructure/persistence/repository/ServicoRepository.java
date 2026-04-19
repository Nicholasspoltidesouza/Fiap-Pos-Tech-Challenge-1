package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.postech.challenge.infrastructure.persistence.entity.ServicoEntity;

public abstract class ServicoRepository {
    public abstract List<ServicoEntity> findAll();

    public abstract Optional<ServicoEntity> findById(UUID id);

    public abstract ServicoEntity save(ServicoEntity servico);

    public abstract boolean existsById(UUID id);

    public abstract void deleteById(UUID id);
}
