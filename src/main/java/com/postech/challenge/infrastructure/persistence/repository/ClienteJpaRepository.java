package com.postech.challenge.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, UUID> {
    Optional<ClienteEntity> findByCpfCnpj(String cpfCnpj);

    boolean existsByCpfCnpj(String cpfCnpj);
}
