package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.postech.challenge.infrastructure.persistence.entity.PecaEntity;

public interface PecaRepository {

    List<PecaEntity> findAll();

    Optional<PecaEntity> findById(UUID id);

    PecaEntity save(PecaEntity peca);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
