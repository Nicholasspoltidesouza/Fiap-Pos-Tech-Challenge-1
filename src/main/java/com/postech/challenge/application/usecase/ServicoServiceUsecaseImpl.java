package com.postech.challenge.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.postech.challenge.application.dto.ServicoRequestDTO;
import com.postech.challenge.application.dto.ServicoResponseDTO;
import com.postech.challenge.application.mapper.ServicoDataMapper;
import com.postech.challenge.infrastructure.persistence.entity.ServicoEntity;
import com.postech.challenge.infrastructure.persistence.repository.ServicoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ServicoServiceUsecaseImpl extends ServicoServiceUsecase {

    private final ServicoRepository servicoRepository;
    private final ServicoDataMapper servicoDataMapper;

    public ServicoServiceUsecaseImpl(ServicoRepository servicoRepository, ServicoDataMapper servicoDataMapper) {
        this.servicoRepository = servicoRepository;
        this.servicoDataMapper = servicoDataMapper;
    }

    @Override
    public List<ServicoResponseDTO> findAll() {
        return servicoRepository.findAll()
                .stream()
                .map(servicoDataMapper::toResponse)
                .toList();
    }

    @Override
    public ServicoResponseDTO findById(UUID id) {
        ServicoEntity servico = servicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servico not found: " + id));
        return servicoDataMapper.toResponse(servico);
    }

    @Override
    public ServicoResponseDTO create(ServicoRequestDTO request) {
        ServicoEntity servico = servicoDataMapper.toEntity(request);
        return servicoDataMapper.toResponse(servicoRepository.save(servico));
    }

    @Override
    public ServicoResponseDTO update(UUID id, ServicoRequestDTO request) {
        ServicoEntity servico = servicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servico not found: " + id));

        servicoDataMapper.updateEntity(servico, request);
        return servicoDataMapper.toResponse(servicoRepository.save(servico));
    }

    @Override
    public void delete(UUID id) {
        if (!servicoRepository.existsById(id)) {
            throw new EntityNotFoundException("Servico not found: " + id);
        }
        servicoRepository.deleteById(id);
    }
}
