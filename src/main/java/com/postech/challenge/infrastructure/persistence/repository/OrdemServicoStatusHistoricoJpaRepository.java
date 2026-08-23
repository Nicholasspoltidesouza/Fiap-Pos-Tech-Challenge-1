package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoStatusHistoricoEntity;

public interface OrdemServicoStatusHistoricoJpaRepository
        extends JpaRepository<OrdemServicoStatusHistoricoEntity, UUID> {

    Optional<OrdemServicoStatusHistoricoEntity> findFirstByOrdemServico_IdOrderByAlteradoEmDesc(UUID ordemServicoId);

    @Query("""
            select h.ordemServico.id as ordemServicoId,
                   h.status as status,
                   h.alteradoEm as alteradoEm
            from OrdemServicoStatusHistoricoEntity h
            """)
    List<StatusHistoricoPontoProjection> findAllPontos();
}
