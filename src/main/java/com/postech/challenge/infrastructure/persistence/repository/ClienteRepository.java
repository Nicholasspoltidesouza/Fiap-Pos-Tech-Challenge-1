package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;

public abstract class ClienteRepository {
    public abstract List<ClienteEntity> findAll();

    public abstract Optional<ClienteEntity> findById(UUID id);

    public abstract Optional<ClienteEntity> findByCpfCnpj(String cpfCnpj);

    public abstract ClienteEntity save(ClienteEntity cliente);

    public abstract boolean existsById(UUID id);

    public abstract boolean existsByCpfCnpj(String cpfCnpj);

    public abstract void deleteById(UUID id);
}
