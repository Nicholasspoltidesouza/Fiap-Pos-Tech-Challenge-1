package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.postech.challenge.infrastructure.persistence.entity.VeiculoEntity;

public interface VeiculoRepository {

    List<VeiculoEntity> findAll();

    Optional<VeiculoEntity> findById(UUID id);

    Optional<VeiculoEntity> findByPlaca(String placa);

    VeiculoEntity save(VeiculoEntity veiculo);

    boolean existsById(UUID id);

    boolean existsByPlaca(String placa);

    void deleteById(UUID id);
}
