package com.postech.challenge.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.postech.challenge.application.dto.ClienteRequestDTO;
import com.postech.challenge.application.dto.ClienteResponseDTO;
import com.postech.challenge.application.mapper.ClienteDataMapper;
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
        if (clienteRepository.existsByCpfCnpj(request.cpfCnpj())) {
            throw new IllegalArgumentException("CPF/CNPJ already registered: " + request.cpfCnpj());
        }

        ClienteEntity cliente = clienteDataMapper.toEntity(request);
        return clienteDataMapper.toResponse(clienteRepository.save(cliente));
    }

    @Override
    public ClienteResponseDTO update(UUID id, ClienteRequestDTO request) {
        ClienteEntity cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente not found: " + id));

        if (!cliente.getCpfCnpj().equals(request.cpfCnpj())
                && clienteRepository.existsByCpfCnpj(request.cpfCnpj())) {
            throw new IllegalArgumentException("CPF/CNPJ already registered: " + request.cpfCnpj());
        }

        clienteDataMapper.updateEntity(cliente, request);
        return clienteDataMapper.toResponse(clienteRepository.save(cliente));
    }

    @Override
    public void delete(UUID id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente not found: " + id);
        }
        clienteRepository.deleteById(id);
    }
}
