package com.postech.challenge.infrastructure.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;

@SpringBootTest
class ClienteJpaRepositoryIntegrationTest {

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;

    @Test
    void shouldFindClienteByCpfCnpj() {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setNome("Cliente Integracao");
        cliente.setCpfCnpj("52998224725");
        clienteJpaRepository.save(cliente);

        boolean exists = clienteJpaRepository.existsByCpfCnpj("52998224725");

        assertTrue(exists);
        assertTrue(clienteJpaRepository.findByCpfCnpj("52998224725").isPresent());
    }
}
