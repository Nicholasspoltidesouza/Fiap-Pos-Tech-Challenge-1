package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.postech.challenge.infrastructure.persistence.entity.PecaEntity;

@Repository
public class PecaRepositoryImpl extends PecaRepository {

    private final PecaJpaRepository pecaJpaRepository;

    public PecaRepositoryImpl(PecaJpaRepository pecaJpaRepository) {
        this.pecaJpaRepository = pecaJpaRepository;
    }

    @Override
    public List<PecaEntity> findAll() {
        return pecaJpaRepository.findAll();
    }

    @Override
    public Optional<PecaEntity> findById(UUID id) {
        return pecaJpaRepository.findById(id);
    }

    @Override
    public PecaEntity save(PecaEntity peca) {
        return pecaJpaRepository.save(peca);
    }

    @Override
    public boolean existsById(UUID id) {
        return pecaJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        pecaJpaRepository.deleteById(id);
    }
}
