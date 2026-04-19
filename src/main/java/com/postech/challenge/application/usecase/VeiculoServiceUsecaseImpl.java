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
    private static final String PLACA_PATTERN = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$";


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
        String placaNormalizada = normalizeAndValidatePlaca(request.placa());
        if (veiculoRepository.existsByPlaca(placaNormalizada)) {
            throw new IllegalArgumentException("Placa already registered: " + placaNormalizada);
        }

        ClienteEntity cliente = findClienteById(request.clienteId());
        VeiculoRequestDTO normalizedRequest = withNormalizedPlaca(request, placaNormalizada);
        VeiculoEntity veiculo = veiculoDataMapper.toEntity(normalizedRequest, cliente);
        return veiculoDataMapper.toResponse(veiculoRepository.save(veiculo));
    }

    @Override
    public VeiculoResponseDTO update(UUID id, VeiculoRequestDTO request) {
        VeiculoEntity veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Veiculo not found: " + id));

        String placaNormalizada = normalizeAndValidatePlaca(request.placa());
        if (!placaNormalizada.equals(veiculo.getPlaca())
                && veiculoRepository.existsByPlaca(placaNormalizada)) {
            throw new IllegalArgumentException("Placa already registered: " + placaNormalizada);
        }

        ClienteEntity cliente = findClienteById(request.clienteId());
        VeiculoRequestDTO normalizedRequest = withNormalizedPlaca(request, placaNormalizada);
        veiculoDataMapper.updateEntity(veiculo, normalizedRequest, cliente);
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

    private VeiculoRequestDTO withNormalizedPlaca(VeiculoRequestDTO request, String placaNormalizada) {
        return new VeiculoRequestDTO(
                request.marca(),
                request.modelo(),
                placaNormalizada,
                request.ano(),
                request.clienteId());
    }

    private String normalizeAndValidatePlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("Placa is required");
        }

        String placaNormalizada = placa.toUpperCase().replace("-", "").trim();
        if (!placaNormalizada.matches(PLACA_PATTERN)) {
            throw new IllegalArgumentException(
                    "Invalid placa format: " + placa + ". Use format like ABC1234 or ABC1D23");
        }

        return placaNormalizada;
    }
}
