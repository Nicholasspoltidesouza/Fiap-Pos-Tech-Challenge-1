package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;

public interface MonitoramentoJpaRepository extends Repository<OrdemServicoEntity, UUID> {

    @Query(value = """
            SELECT
                s.id AS servicoId,
                s.nome AS servicoNome,
                COUNT(os.id) AS quantidadeOrdensConcluidas,
                AVG(EXTRACT(EPOCH FROM (os.data_finalizacao - os.data_abertura)) / 60.0) AS tempoMedioMinutos
            FROM tb_ordem_servico os
            INNER JOIN tb_ordem_servico_servico oss ON oss.ordem_servico_id = os.id
            INNER JOIN tb_servico s ON s.id = oss.servico_id
            WHERE os.status IN ('FINALIZADA', 'ENTREGUE')
              AND os.data_abertura IS NOT NULL
              AND os.data_finalizacao IS NOT NULL
            GROUP BY s.id, s.nome
            ORDER BY tempoMedioMinutos DESC
            """, nativeQuery = true)
    List<TempoMedioServicoProjection> findTempoMedioExecucaoPorServico();
}
