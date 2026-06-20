package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;

@Repository
public class InsumoRepositoryImpl implements InsumoRepository {

    private final InsumoJpaRepository insumoJpaRepository;

    public InsumoRepositoryImpl(InsumoJpaRepository insumoJpaRepository) {
        this.insumoJpaRepository = insumoJpaRepository;
    }

    @Override
    public List<InsumoEntity> findAll() {
        return insumoJpaRepository.findAll();
    }

    @Override
    public Optional<InsumoEntity> findById(UUID id) {
        return insumoJpaRepository.findById(id);
    }

    @Override
    public InsumoEntity save(InsumoEntity insumo) {
        return insumoJpaRepository.save(insumo);
    }

    @Override
    public boolean existsById(UUID id) {
        return insumoJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        insumoJpaRepository.deleteById(id);
    }
}
