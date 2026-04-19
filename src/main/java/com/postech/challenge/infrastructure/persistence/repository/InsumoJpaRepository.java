package com.postech.challenge.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;

public interface InsumoJpaRepository extends JpaRepository<InsumoEntity, UUID> {
}
