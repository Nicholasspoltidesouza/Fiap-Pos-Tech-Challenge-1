package com.postech.challenge.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.postech.challenge.application.dto.VeiculoRequestDTO;
import com.postech.challenge.application.dto.VeiculoResponseDTO;
import com.postech.challenge.application.mapper.VeiculoDataMapper;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;
import com.postech.challenge.infrastructure.persistence.entity.VeiculoEntity;
import com.postech.challenge.infrastructure.persistence.repository.ClienteRepository;
import com.postech.challenge.infrastructure.persistence.repository.VeiculoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class VeiculoServiceUsecaseImpl extends VeiculoServiceUsecase {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoDataMapper veiculoDataMapper;

    public VeiculoServiceUsecaseImpl(
            VeiculoRepository veiculoRepository,
            ClienteRepository clienteRepository,
            VeiculoDataMapper veiculoDataMapper) {
        this.veiculoRepository = veiculoRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoDataMapper = veiculoDataMapper;
    }

    @Override
    public List<VeiculoResponseDTO> findAll() {
        return veiculoRepository.findAll()
                .stream()
                .map(veiculoDataMapper::toResponse)
                .toList();
    }

    @Override
    public VeiculoResponseDTO findById(UUID id) {
        VeiculoEntity veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Veiculo not found: " + id));
        return veiculoDataMapper.toResponse(veiculo);
    }

    @Override
    public VeiculoResponseDTO create(VeiculoRequestDTO request) {
        ClienteEntity cliente = findClienteById(request.clienteId());
        VeiculoEntity veiculo = veiculoDataMapper.toEntity(request, cliente);
        return veiculoDataMapper.toResponse(veiculoRepository.save(veiculo));
    }

    @Override
    public VeiculoResponseDTO update(UUID id, VeiculoRequestDTO request) {
        VeiculoEntity veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Veiculo not found: " + id));

        ClienteEntity cliente = findClienteById(request.clienteId());
        veiculoDataMapper.updateEntity(veiculo, request, cliente);
        return veiculoDataMapper.toResponse(veiculoRepository.save(veiculo));
    }

    @Override
    public void delete(UUID id) {
        if (!veiculoRepository.existsById(id)) {
            throw new EntityNotFoundException("Veiculo not found: " + id);
        }
        veiculoRepository.deleteById(id);
    }

    private ClienteEntity findClienteById(UUID clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente not found: " + clienteId));
    }
}
