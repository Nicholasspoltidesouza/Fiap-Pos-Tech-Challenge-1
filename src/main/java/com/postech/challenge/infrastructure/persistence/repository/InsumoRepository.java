package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;

public abstract class InsumoRepository {
    public abstract List<InsumoEntity> findAll();

    public abstract Optional<InsumoEntity> findById(UUID id);

    public abstract InsumoEntity save(InsumoEntity insumo);

    public abstract boolean existsById(UUID id);

    public abstract void deleteById(UUID id);
}
