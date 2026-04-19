package com.postech.challenge.application.usecase;

import java.util.List;
import java.util.UUID;

import com.postech.challenge.application.dto.VeiculoRequestDTO;
import com.postech.challenge.application.dto.VeiculoResponseDTO;

public abstract class VeiculoServiceUsecase {

    public abstract List<VeiculoResponseDTO> findAll();

    public abstract VeiculoResponseDTO findById(UUID id);

    public abstract VeiculoResponseDTO create(VeiculoRequestDTO request);

    public abstract VeiculoResponseDTO update(UUID id, VeiculoRequestDTO request);

    public abstract void delete(UUID id);
}
