package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.postech.challenge.infrastructure.persistence.entity.VeiculoEntity;

public abstract class VeiculoRepository {
    public abstract List<VeiculoEntity> findAll();

    public abstract Optional<VeiculoEntity> findById(UUID id);

    public abstract VeiculoEntity save(VeiculoEntity veiculo);

    public abstract boolean existsById(UUID id);

    public abstract void deleteById(UUID id);
}
