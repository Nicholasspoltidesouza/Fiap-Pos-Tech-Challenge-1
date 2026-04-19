package com.postech.challenge.application.usecase;

import java.util.List;
import java.util.UUID;

import com.postech.challenge.application.dto.ServicoRequestDTO;
import com.postech.challenge.application.dto.ServicoResponseDTO;

public abstract class ServicoServiceUsecase {

    public abstract List<ServicoResponseDTO> findAll();

    public abstract ServicoResponseDTO findById(UUID id);

    public abstract ServicoResponseDTO create(ServicoRequestDTO request);

    public abstract ServicoResponseDTO update(UUID id, ServicoRequestDTO request);

    public abstract void delete(UUID id);
}
