package com.postech.challenge.application.usecase;

import java.util.List;
import java.util.UUID;

import com.postech.challenge.application.dto.ClienteRequestDTO;
import com.postech.challenge.application.dto.ClienteResponseDTO;
public abstract class ClienteServiceUsecase {

    public abstract List<ClienteResponseDTO> findAll();

    public abstract ClienteResponseDTO findById(UUID id);

    public abstract ClienteResponseDTO create(ClienteRequestDTO request);

    public abstract ClienteResponseDTO update(UUID id, ClienteRequestDTO request);

    public abstract void delete(UUID id);
}
