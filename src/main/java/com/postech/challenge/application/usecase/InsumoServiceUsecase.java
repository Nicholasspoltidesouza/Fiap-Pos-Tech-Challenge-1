package com.postech.challenge.application.usecase;

import java.util.List;
import java.util.UUID;

import com.postech.challenge.application.dto.InsumoRequestDTO;
import com.postech.challenge.application.dto.InsumoResponseDTO;

public abstract class InsumoServiceUsecase {

    public abstract List<InsumoResponseDTO> findAll();

    public abstract InsumoResponseDTO findById(UUID id);

    public abstract InsumoResponseDTO create(InsumoRequestDTO request);

    public abstract InsumoResponseDTO update(UUID id, InsumoRequestDTO request);

    public abstract void delete(UUID id);
}
