package com.postech.challenge.application.usecase;

import java.util.List;
import java.util.UUID;

import com.postech.challenge.application.dto.OrdemServicoRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoResponseDTO;

public abstract class OrdemServicoServiceUsecase {

    public abstract List<OrdemServicoResponseDTO> findAll();

    public abstract OrdemServicoResponseDTO findById(UUID id);

    public abstract OrdemServicoResponseDTO create(OrdemServicoRequestDTO request);

    public abstract OrdemServicoResponseDTO update(UUID id, OrdemServicoRequestDTO request);

    public abstract void delete(UUID id);
}
