package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.postech.challenge.infrastructure.persistence.entity.VeiculoEntity;

@Repository
public class VeiculoRepositoryImpl implements VeiculoRepository {

    private final VeiculoJpaRepository veiculoJpaRepository;

    public VeiculoRepositoryImpl(VeiculoJpaRepository veiculoJpaRepository) {
        this.veiculoJpaRepository = veiculoJpaRepository;
    }

    @Override
    public List<VeiculoEntity> findAll() {
        return veiculoJpaRepository.findAll();
    }

    @Override
    public Optional<VeiculoEntity> findById(UUID id) {
        return veiculoJpaRepository.findById(id);
    }

    @Override
    public Optional<VeiculoEntity> findByPlaca(String placa) {
        return veiculoJpaRepository.findByPlaca(placa);
    }

    @Override
    public VeiculoEntity save(VeiculoEntity veiculo) {
        return veiculoJpaRepository.save(veiculo);
    }

    @Override
    public boolean existsById(UUID id) {
        return veiculoJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByPlaca(String placa) {
        return veiculoJpaRepository.existsByPlaca(placa);
    }

    @Override
    public void deleteById(UUID id) {
        veiculoJpaRepository.deleteById(id);
    }
}
