package com.postech.challenge.application.usecase;

import java.util.List;
import java.util.UUID;

import com.postech.challenge.application.dto.AcompanhamentoOrdemServicoResponseDTO;
import com.postech.challenge.application.dto.OrdemServicoCreateByClienteRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoResponseDTO;

public abstract class OrdemServicoServiceUsecase {

    public abstract List<OrdemServicoResponseDTO> findAll();

    public abstract List<OrdemServicoResponseDTO> listarAtivas();

    public abstract OrdemServicoResponseDTO findById(UUID id);

    public abstract OrdemServicoResponseDTO create(OrdemServicoRequestDTO request);

    public abstract OrdemServicoResponseDTO createByClienteCpfCnpj(OrdemServicoCreateByClienteRequestDTO request);

    public abstract OrdemServicoResponseDTO update(UUID id, OrdemServicoRequestDTO request);

    public abstract OrdemServicoResponseDTO adicionarPecas(UUID id, List<UUID> pecasIds);

    public abstract OrdemServicoResponseDTO iniciarDiagnostico(UUID id);

    public abstract OrdemServicoResponseDTO enviarOrcamento(UUID id);

    public abstract OrdemServicoResponseDTO aprovarOrcamento(UUID id, boolean aprovado);

    public abstract OrdemServicoResponseDTO finalizar(UUID id);

    public abstract OrdemServicoResponseDTO entregar(UUID id);

    public abstract AcompanhamentoOrdemServicoResponseDTO consultarAcompanhamento(UUID id, String cpfCnpj);

    public abstract void delete(UUID id);
}
