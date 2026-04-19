package com.postech.challenge.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.postech.challenge.application.dto.PecaRequestDTO;
import com.postech.challenge.application.dto.PecaResponseDTO;
import com.postech.challenge.application.mapper.PecaDataMapper;
import com.postech.challenge.infrastructure.persistence.entity.PecaEntity;
import com.postech.challenge.infrastructure.persistence.repository.PecaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PecaServiceUsecaseImpl extends PecaServiceUsecase {

    private final PecaRepository pecaRepository;
    private final PecaDataMapper pecaDataMapper;

    public PecaServiceUsecaseImpl(PecaRepository pecaRepository, PecaDataMapper pecaDataMapper) {
        this.pecaRepository = pecaRepository;
        this.pecaDataMapper = pecaDataMapper;
    }

    @Override
    public List<PecaResponseDTO> findAll() {
        return pecaRepository.findAll()
                .stream()
                .map(pecaDataMapper::toResponse)
                .toList();
    }

    @Override
    public PecaResponseDTO findById(UUID id) {
        PecaEntity peca = pecaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Peca not found: " + id));
        return pecaDataMapper.toResponse(peca);
    }

    @Override
    public PecaResponseDTO create(PecaRequestDTO request) {
        PecaEntity peca = pecaDataMapper.toEntity(request);
        return pecaDataMapper.toResponse(pecaRepository.save(peca));
    }

    @Override
    public PecaResponseDTO update(UUID id, PecaRequestDTO request) {
        PecaEntity peca = pecaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Peca not found: " + id));

        pecaDataMapper.updateEntity(peca, request);
        return pecaDataMapper.toResponse(pecaRepository.save(peca));
    }

    @Override
    public void delete(UUID id) {
        if (!pecaRepository.existsById(id)) {
            throw new EntityNotFoundException("Peca not found: " + id);
        }
        pecaRepository.deleteById(id);
    }
}
