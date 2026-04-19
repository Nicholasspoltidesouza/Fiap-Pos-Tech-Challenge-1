package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.postech.challenge.infrastructure.persistence.entity.PecaEntity;

public abstract class PecaRepository {
    public abstract List<PecaEntity> findAll();

    public abstract Optional<PecaEntity> findById(UUID id);

    public abstract PecaEntity save(PecaEntity peca);

    public abstract boolean existsById(UUID id);

    public abstract void deleteById(UUID id);
}
