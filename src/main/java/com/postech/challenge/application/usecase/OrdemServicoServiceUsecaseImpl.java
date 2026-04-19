package com.postech.challenge.application.usecase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.postech.challenge.application.dto.OrdemServicoRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoResponseDTO;
import com.postech.challenge.application.mapper.OrdemServicoDataMapper;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;
import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;
import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;
import com.postech.challenge.infrastructure.persistence.entity.ServicoEntity;
import com.postech.challenge.infrastructure.persistence.entity.StatusOrdemServico;
import com.postech.challenge.infrastructure.persistence.entity.VeiculoEntity;
import com.postech.challenge.infrastructure.persistence.repository.ClienteRepository;
import com.postech.challenge.infrastructure.persistence.repository.InsumoRepository;
import com.postech.challenge.infrastructure.persistence.repository.OrdemServicoRepository;
import com.postech.challenge.infrastructure.persistence.repository.ServicoRepository;
import com.postech.challenge.infrastructure.persistence.repository.VeiculoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class OrdemServicoServiceUsecaseImpl extends OrdemServicoServiceUsecase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final ServicoRepository servicoRepository;
    private final InsumoRepository insumoRepository;
    private final OrdemServicoDataMapper ordemServicoDataMapper;

    public OrdemServicoServiceUsecaseImpl(
            OrdemServicoRepository ordemServicoRepository,
            ClienteRepository clienteRepository,
            VeiculoRepository veiculoRepository,
            ServicoRepository servicoRepository,
            InsumoRepository insumoRepository,
            OrdemServicoDataMapper ordemServicoDataMapper) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.servicoRepository = servicoRepository;
        this.insumoRepository = insumoRepository;
        this.ordemServicoDataMapper = ordemServicoDataMapper;
    }

    @Override
    public List<OrdemServicoResponseDTO> findAll() {
        return ordemServicoRepository.findAll()
                .stream()
                .map(ordemServicoDataMapper::toResponse)
                .toList();
    }

    @Override
    public OrdemServicoResponseDTO findById(UUID id) {
        OrdemServicoEntity ordemServico = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrdemServico not found: " + id));
        return ordemServicoDataMapper.toResponse(ordemServico);
    }

    @Override
    public OrdemServicoResponseDTO create(OrdemServicoRequestDTO request) {
        ClienteEntity cliente = findClienteById(request.clienteId());
        VeiculoEntity veiculo = findVeiculoById(request.veiculoId());
        StatusOrdemServico status = parseStatus(request.status());
        List<ServicoEntity> servicos = findServicosByIds(request.servicosSolicitadosIds());
        List<InsumoEntity> insumos = findInsumosByIds(request.insumosSolicitadosIds());
        LocalDateTime dataAbertura = Optional.ofNullable(request.dataAbertura()).orElse(LocalDateTime.now());

        OrdemServicoEntity ordemServico = ordemServicoDataMapper.toEntity(
                cliente,
                veiculo,
                status,
                dataAbertura,
                request.dataFinalizacao(),
                servicos,
                insumos
        );

        return ordemServicoDataMapper.toResponse(ordemServicoRepository.save(ordemServico));
    }

    @Override
    public OrdemServicoResponseDTO update(UUID id, OrdemServicoRequestDTO request) {
        OrdemServicoEntity ordemServico = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrdemServico not found: " + id));

        ClienteEntity cliente = findClienteById(request.clienteId());
        VeiculoEntity veiculo = findVeiculoById(request.veiculoId());
        StatusOrdemServico status = parseStatus(request.status());
        List<ServicoEntity> servicos = findServicosByIds(request.servicosSolicitadosIds());
        List<InsumoEntity> insumos = findInsumosByIds(request.insumosSolicitadosIds());

        ordemServicoDataMapper.updateEntity(
                ordemServico,
                cliente,
                veiculo,
                status,
                request.dataAbertura(),
                request.dataFinalizacao(),
                servicos,
                insumos
        );

        return ordemServicoDataMapper.toResponse(ordemServicoRepository.save(ordemServico));
    }

    @Override
    public void delete(UUID id) {
        if (!ordemServicoRepository.existsById(id)) {
            throw new EntityNotFoundException("OrdemServico not found: " + id);
        }
        ordemServicoRepository.deleteById(id);
    }

    private ClienteEntity findClienteById(UUID clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente not found: " + clienteId));
    }

    private VeiculoEntity findVeiculoById(UUID veiculoId) {
        return veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new EntityNotFoundException("Veiculo not found: " + veiculoId));
    }

    private List<ServicoEntity> findServicosByIds(List<UUID> servicosIds) {
        if (servicosIds == null || servicosIds.isEmpty()) {
            return List.of();
        }
        return servicosIds.stream()
                .map(servicoId -> servicoRepository.findById(servicoId)
                        .orElseThrow(() -> new EntityNotFoundException("Servico not found: " + servicoId)))
                .toList();
    }

    private List<InsumoEntity> findInsumosByIds(List<UUID> insumosIds) {
        if (insumosIds == null || insumosIds.isEmpty()) {
            return List.of();
        }
        return insumosIds.stream()
                .map(insumoId -> insumoRepository.findById(insumoId)
                        .orElseThrow(() -> new EntityNotFoundException("Insumo not found: " + insumoId)))
                .toList();
    }

    private StatusOrdemServico parseStatus(String status) {
        try {
            return StatusOrdemServico.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException(
                    "Invalid status: " + status + ". Allowed values: RECEBIDA, EM_DIAGNOSTICO, AGUARDANDO_APROVACAO, EM_EXECUCAO, FINALIZADA, ENTREGUE");
        }
    }
}
