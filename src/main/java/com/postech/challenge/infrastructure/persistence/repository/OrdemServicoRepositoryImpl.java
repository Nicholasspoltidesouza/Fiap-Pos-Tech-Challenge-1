package com.postech.challenge.infrastructure.persistence.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;

@Repository
public class OrdemServicoRepositoryImpl implements OrdemServicoRepository {

    private static final Comparator<OrdemServicoEntity> ORDENACAO_LISTAGEM =
            Comparator.comparingInt((OrdemServicoEntity ordem) -> ordem.getStatus().prioridadeListagem())
                    .reversed()
                    .thenComparing(OrdemServicoEntity::getDataAbertura);

    private final OrdemServicoJpaRepository ordemServicoJpaRepository;

    public OrdemServicoRepositoryImpl(OrdemServicoJpaRepository ordemServicoJpaRepository) {
        this.ordemServicoJpaRepository = ordemServicoJpaRepository;
    }

    @Override
    public List<OrdemServicoEntity> findAll() {
        return ordemServicoJpaRepository.findAll();
    }

    @Override
    public List<OrdemServicoEntity> findAtivasOrdenadas() {
        return ordemServicoJpaRepository.findAtivas()
                .stream()
                .sorted(ORDENACAO_LISTAGEM)
                .toList();
    }

    @Override
    public Optional<OrdemServicoEntity> findById(UUID id) {
        return ordemServicoJpaRepository.findById(id);
    }

    @Override
    public OrdemServicoEntity save(OrdemServicoEntity ordemServico) {
        return ordemServicoJpaRepository.save(ordemServico);
    }

    @Override
    public boolean existsById(UUID id) {
        return ordemServicoJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        ordemServicoJpaRepository.deleteById(id);
    }
}
