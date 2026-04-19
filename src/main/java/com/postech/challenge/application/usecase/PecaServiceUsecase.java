package com.postech.challenge.application.usecase;

import java.util.List;
import java.util.UUID;

import com.postech.challenge.application.dto.PecaRequestDTO;
import com.postech.challenge.application.dto.PecaResponseDTO;

public abstract class PecaServiceUsecase {

    public abstract List<PecaResponseDTO> findAll();

    public abstract PecaResponseDTO findById(UUID id);

    public abstract PecaResponseDTO create(PecaRequestDTO request);

    public abstract PecaResponseDTO update(UUID id, PecaRequestDTO request);

    public abstract void delete(UUID id);
}
