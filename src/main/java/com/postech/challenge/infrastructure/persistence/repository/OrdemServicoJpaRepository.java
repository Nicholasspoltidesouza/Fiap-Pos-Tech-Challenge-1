package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;

public interface OrdemServicoJpaRepository extends JpaRepository<OrdemServicoEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"cliente", "veiculo"})
    List<OrdemServicoEntity> findAll();

    @Override
    @EntityGraph(attributePaths = {"cliente", "veiculo"})
    Optional<OrdemServicoEntity> findById(UUID id);

    @EntityGraph(attributePaths = {"cliente", "veiculo"})
    @Query("""
            select o from OrdemServicoEntity o
            where o.status not in (
                com.postech.challenge.domain.model.StatusOrdemServico.FINALIZADA,
                com.postech.challenge.domain.model.StatusOrdemServico.ENTREGUE
            )
            """)
    List<OrdemServicoEntity> findAtivas();
}
