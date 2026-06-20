package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.postech.challenge.infrastructure.persistence.entity.ServicoEntity;

public interface ServicoRepository {

    List<ServicoEntity> findAll();

    Optional<ServicoEntity> findById(UUID id);

    ServicoEntity save(ServicoEntity servico);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
