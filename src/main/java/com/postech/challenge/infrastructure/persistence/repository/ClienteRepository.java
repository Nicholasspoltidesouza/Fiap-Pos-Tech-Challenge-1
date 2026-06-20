package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;

public interface ClienteRepository {

    List<ClienteEntity> findAll();

    Optional<ClienteEntity> findById(UUID id);

    Optional<ClienteEntity> findByCpfCnpj(String cpfCnpj);

    ClienteEntity save(ClienteEntity cliente);

    boolean existsById(UUID id);

    boolean existsByCpfCnpj(String cpfCnpj);

    void deleteById(UUID id);
}
