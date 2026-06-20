package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;

public interface InsumoRepository {

    List<InsumoEntity> findAll();

    Optional<InsumoEntity> findById(UUID id);

    InsumoEntity save(InsumoEntity insumo);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
