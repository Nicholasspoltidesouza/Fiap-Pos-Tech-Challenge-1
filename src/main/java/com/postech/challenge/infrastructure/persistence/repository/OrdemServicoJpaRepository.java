package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;

public interface OrdemServicoJpaRepository extends JpaRepository<OrdemServicoEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"cliente", "veiculo", "servicosSolicitados", "insumosSolicitados", "pecasSolicitadas"})
    List<OrdemServicoEntity> findAll();

    @Override
    @EntityGraph(attributePaths = {"cliente", "veiculo", "servicosSolicitados", "insumosSolicitados", "pecasSolicitadas"})
    Optional<OrdemServicoEntity> findById(UUID id);
}
