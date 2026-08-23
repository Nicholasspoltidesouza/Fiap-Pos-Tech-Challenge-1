package com.postech.challenge.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoStatusHistoricoEntity;

public interface OrdemServicoStatusHistoricoJpaRepository
        extends JpaRepository<OrdemServicoStatusHistoricoEntity, UUID> {
}
