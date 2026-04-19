package com.postech.challenge.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;

@Repository
public class ClienteRepositoryImpl extends ClienteRepository {

    private final ClienteJpaRepository clienteJpaRepository;

    public ClienteRepositoryImpl(ClienteJpaRepository clienteJpaRepository) {
        this.clienteJpaRepository = clienteJpaRepository;
    }

    @Override
    public List<ClienteEntity> findAll() {
        return clienteJpaRepository.findAll();
    }

    @Override
    public Optional<ClienteEntity> findById(UUID id) {
        return clienteJpaRepository.findById(id);
    }

    @Override
    public ClienteEntity save(ClienteEntity cliente) {
        return clienteJpaRepository.save(cliente);
    }

    @Override
    public boolean existsById(UUID id) {
        return clienteJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByCpfCnpj(String cpfCnpj) {
        return clienteJpaRepository.existsByCpfCnpj(cpfCnpj);
    }

    @Override
    public void deleteById(UUID id) {
        clienteJpaRepository.deleteById(id);
    }
}
