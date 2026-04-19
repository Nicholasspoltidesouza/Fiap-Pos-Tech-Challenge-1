package com.postech.challenge.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.postech.challenge.application.dto.ClienteRequestDTO;
import com.postech.challenge.application.dto.ClienteResponseDTO;
import com.postech.challenge.application.mapper.ClienteDataMapper;
import com.postech.challenge.application.validator.CpfCnpjValidator;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;
import com.postech.challenge.infrastructure.persistence.repository.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ClienteServiceUsecaseImpl extends ClienteServiceUsecase {

    private final ClienteRepository clienteRepository;
    private final ClienteDataMapper clienteDataMapper;

    public ClienteServiceUsecaseImpl(ClienteRepository clienteRepository, ClienteDataMapper clienteDataMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteDataMapper = clienteDataMapper;
    }

    @Override
    public List<ClienteResponseDTO> findAll() {
        return clienteRepository.findAll()
                .stream()
                .map(clienteDataMapper::toResponse)
                .toList();
    }

    @Override
    public ClienteResponseDTO findById(UUID id) {
        ClienteEntity cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente not found: " + id));
        return clienteDataMapper.toResponse(cliente);
    }

    @Override
    public ClienteResponseDTO create(ClienteRequestDTO request) {
        String normalizedCpfCnpj = normalizeAndValidateCpfCnpj(request.cpfCnpj());
        if (clienteRepository.existsByCpfCnpj(normalizedCpfCnpj)) {
            throw new IllegalArgumentException("CPF/CNPJ already registered: " + normalizedCpfCnpj);
        }

        ClienteEntity cliente = clienteDataMapper.toEntity(withNormalizedCpfCnpj(request, normalizedCpfCnpj));
        return clienteDataMapper.toResponse(clienteRepository.save(cliente));
    }

    @Override
    public ClienteResponseDTO update(UUID id, ClienteRequestDTO request) {
        ClienteEntity cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente not found: " + id));

        String normalizedCpfCnpj = normalizeAndValidateCpfCnpj(request.cpfCnpj());
        if (!cliente.getCpfCnpj().equals(normalizedCpfCnpj)
                && clienteRepository.existsByCpfCnpj(normalizedCpfCnpj)) {
            throw new IllegalArgumentException("CPF/CNPJ already registered: " + normalizedCpfCnpj);
        }

        clienteDataMapper.updateEntity(cliente, withNormalizedCpfCnpj(request, normalizedCpfCnpj));
        return clienteDataMapper.toResponse(clienteRepository.save(cliente));
    }

    @Override
    public void delete(UUID id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente not found: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private String normalizeAndValidateCpfCnpj(String cpfCnpj) {
        String normalized = CpfCnpjValidator.normalize(cpfCnpj);
        if (!CpfCnpjValidator.isValid(normalized)) {
            throw new IllegalArgumentException("Invalid CPF/CNPJ");
        }
        return normalized;
    }

    private ClienteRequestDTO withNormalizedCpfCnpj(ClienteRequestDTO request, String normalizedCpfCnpj) {
        return new ClienteRequestDTO(
                request.nome(),
                normalizedCpfCnpj);
    }
}
