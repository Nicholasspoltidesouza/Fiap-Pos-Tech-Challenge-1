package com.postech.challenge.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postech.challenge.infrastructure.persistence.entity.PecaEntity;

public interface PecaJpaRepository extends JpaRepository<PecaEntity, UUID> {
}
