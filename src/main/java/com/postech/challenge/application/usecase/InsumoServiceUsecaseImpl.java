package com.postech.challenge.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.postech.challenge.application.dto.InsumoRequestDTO;
import com.postech.challenge.application.dto.InsumoResponseDTO;
import com.postech.challenge.application.mapper.InsumoDataMapper;
import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;
import com.postech.challenge.infrastructure.persistence.repository.InsumoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class InsumoServiceUsecaseImpl extends InsumoServiceUsecase {

    private final InsumoRepository insumoRepository;
    private final InsumoDataMapper insumoDataMapper;

    public InsumoServiceUsecaseImpl(InsumoRepository insumoRepository, InsumoDataMapper insumoDataMapper) {
        this.insumoRepository = insumoRepository;
        this.insumoDataMapper = insumoDataMapper;
    }

    @Override
    public List<InsumoResponseDTO> findAll() {
        return insumoRepository.findAll()
                .stream()
                .map(insumoDataMapper::toResponse)
                .toList();
    }

    @Override
    public InsumoResponseDTO findById(UUID id) {
        InsumoEntity insumo = insumoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo not found: " + id));
        return insumoDataMapper.toResponse(insumo);
    }

    @Override
    public InsumoResponseDTO create(InsumoRequestDTO request) {
        InsumoEntity insumo = insumoDataMapper.toEntity(request);
        return insumoDataMapper.toResponse(insumoRepository.save(insumo));
    }

    @Override
    public InsumoResponseDTO update(UUID id, InsumoRequestDTO request) {
        InsumoEntity insumo = insumoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo not found: " + id));

        insumoDataMapper.updateEntity(insumo, request);
        return insumoDataMapper.toResponse(insumoRepository.save(insumo));
    }

    @Override
    public void delete(UUID id) {
        if (!insumoRepository.existsById(id)) {
            throw new EntityNotFoundException("Insumo not found: " + id);
        }
        insumoRepository.deleteById(id);
    }
}
