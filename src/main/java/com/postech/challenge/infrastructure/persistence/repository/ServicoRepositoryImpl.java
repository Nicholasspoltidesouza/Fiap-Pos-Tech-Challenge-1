package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.postech.challenge.infrastructure.persistence.entity.ServicoEntity;

@Repository
public class ServicoRepositoryImpl implements ServicoRepository {

    private final ServicoJpaRepository servicoJpaRepository;

    public ServicoRepositoryImpl(ServicoJpaRepository servicoJpaRepository) {
        this.servicoJpaRepository = servicoJpaRepository;
    }

    @Override
    public List<ServicoEntity> findAll() {
        return servicoJpaRepository.findAll();
    }

    @Override
    public Optional<ServicoEntity> findById(UUID id) {
        return servicoJpaRepository.findById(id);
    }

    @Override
    public ServicoEntity save(ServicoEntity servico) {
        return servicoJpaRepository.save(servico);
    }

    @Override
    public boolean existsById(UUID id) {
        return servicoJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        servicoJpaRepository.deleteById(id);
    }
}
