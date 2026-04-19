package com.postech.challenge.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.postech.challenge.application.dto.AcompanhamentoOrdemServicoResponseDTO;
import com.postech.challenge.application.dto.OrdemServicoCreateByClienteRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoResponseDTO;
import com.postech.challenge.application.mapper.OrdemServicoDataMapper;
import com.postech.challenge.application.validator.CpfCnpjValidator;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;
import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;
import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;
import com.postech.challenge.infrastructure.persistence.entity.PecaEntity;
import com.postech.challenge.infrastructure.persistence.entity.ServicoEntity;
import com.postech.challenge.infrastructure.persistence.entity.StatusOrdemServico;
import com.postech.challenge.infrastructure.persistence.entity.VeiculoEntity;
import com.postech.challenge.infrastructure.persistence.repository.ClienteRepository;
import com.postech.challenge.infrastructure.persistence.repository.InsumoRepository;
import com.postech.challenge.infrastructure.persistence.repository.OrdemServicoRepository;
import com.postech.challenge.infrastructure.persistence.repository.PecaRepository;
import com.postech.challenge.infrastructure.persistence.repository.ServicoRepository;
import com.postech.challenge.infrastructure.persistence.repository.VeiculoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class OrdemServicoServiceUsecaseImpl extends OrdemServicoServiceUsecase {

    private static final Pattern PLACA_PATTERN = Pattern.compile("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$");

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final ServicoRepository servicoRepository;
    private final InsumoRepository insumoRepository;
    private final PecaRepository pecaRepository;
    private final OrdemServicoDataMapper ordemServicoDataMapper;

    public OrdemServicoServiceUsecaseImpl(
            OrdemServicoRepository ordemServicoRepository,
            ClienteRepository clienteRepository,
            VeiculoRepository veiculoRepository,
            ServicoRepository servicoRepository,
            InsumoRepository insumoRepository,
            PecaRepository pecaRepository,
            OrdemServicoDataMapper ordemServicoDataMapper) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.servicoRepository = servicoRepository;
        this.insumoRepository = insumoRepository;
        this.pecaRepository = pecaRepository;
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
        StatusOrdemServico status = parseStatusOrDefault(request.status(), StatusOrdemServico.RECEBIDA);
        List<ServicoEntity> servicos = findServicosByIds(request.servicosSolicitadosIds());
        List<InsumoEntity> insumos = findInsumosByIds(request.insumosSolicitadosIds());
        List<PecaEntity> pecas = findPecasByIds(request.pecasSolicitadasIds());
        LocalDateTime dataAbertura = Optional.ofNullable(request.dataAbertura()).orElse(LocalDateTime.now());
        BigDecimal valorOrcamento = calculateOrcamento(servicos, pecas);

        OrdemServicoEntity ordemServico = ordemServicoDataMapper.toEntity(
                cliente,
                veiculo,
                status,
                dataAbertura,
                request.dataFinalizacao(),
                valorOrcamento,
                null,
                null,
                servicos,
                insumos,
                pecas
        );

        return ordemServicoDataMapper.toResponse(ordemServicoRepository.save(ordemServico));
    }

    @Override
    public OrdemServicoResponseDTO createByClienteCpfCnpj(OrdemServicoCreateByClienteRequestDTO request) {
        String normalizedCpfCnpj = normalizeAndValidateCpfCnpj(request.clienteCpfCnpj());
        ClienteEntity cliente = clienteRepository.findByCpfCnpj(normalizedCpfCnpj)
                .orElseThrow(() -> new EntityNotFoundException("Cliente not found by CPF/CNPJ: " + normalizedCpfCnpj));
        VeiculoEntity veiculo = resolveVeiculo(cliente, request);
        List<ServicoEntity> servicos = findServicosByIds(request.servicosSolicitadosIds());
        List<InsumoEntity> insumos = findInsumosByIds(request.insumosSolicitadosIds());
        List<PecaEntity> pecas = findPecasByIds(request.pecasSolicitadasIds());

        OrdemServicoEntity ordemServico = ordemServicoDataMapper.toEntity(
                cliente,
                veiculo,
                StatusOrdemServico.RECEBIDA,
                LocalDateTime.now(),
                null,
                calculateOrcamento(servicos, pecas),
                null,
                null,
                servicos,
                insumos,
                pecas
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
        List<PecaEntity> pecas = findPecasByIds(request.pecasSolicitadasIds());
        BigDecimal valorOrcamento = calculateOrcamento(servicos, pecas);

        ordemServicoDataMapper.updateEntity(
                ordemServico,
                cliente,
                veiculo,
                status,
                request.dataAbertura(),
                request.dataFinalizacao(),
                valorOrcamento,
                ordemServico.getOrcamentoAprovado(),
                ordemServico.getDataEnvioOrcamento(),
                servicos,
                insumos,
                pecas
        );

        return ordemServicoDataMapper.toResponse(ordemServicoRepository.save(ordemServico));
    }

    @Override
    public OrdemServicoResponseDTO adicionarPecas(UUID id, List<UUID> pecasIds) {
        OrdemServicoEntity ordemServico = getOrdemServicoById(id);
        List<PecaEntity> pecas = findPecasByIds(pecasIds);
        ordemServico.setPecasSolicitadas(pecas);
        ordemServico.setValorOrcamento(calculateOrcamento(ordemServico.getServicosSolicitados(), pecas));
        return ordemServicoDataMapper.toResponse(ordemServicoRepository.save(ordemServico));
    }

    @Override
    public OrdemServicoResponseDTO iniciarDiagnostico(UUID id) {
        OrdemServicoEntity ordemServico = getOrdemServicoById(id);
        ordemServico.setStatus(StatusOrdemServico.EM_DIAGNOSTICO);
        return ordemServicoDataMapper.toResponse(ordemServicoRepository.save(ordemServico));
    }

    @Override
    public OrdemServicoResponseDTO enviarOrcamento(UUID id) {
        OrdemServicoEntity ordemServico = getOrdemServicoById(id);
        ordemServico.setValorOrcamento(calculateOrcamento(ordemServico.getServicosSolicitados(), ordemServico.getPecasSolicitadas()));
        ordemServico.setDataEnvioOrcamento(LocalDateTime.now());
        ordemServico.setStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);
        return ordemServicoDataMapper.toResponse(ordemServicoRepository.save(ordemServico));
    }

    @Override
    public OrdemServicoResponseDTO aprovarOrcamento(UUID id, boolean aprovado) {
        OrdemServicoEntity ordemServico = getOrdemServicoById(id);
        ordemServico.setOrcamentoAprovado(aprovado);
        ordemServico.setStatus(aprovado ? StatusOrdemServico.EM_EXECUCAO : StatusOrdemServico.RECEBIDA);
        return ordemServicoDataMapper.toResponse(ordemServicoRepository.save(ordemServico));
    }

    @Override
    public OrdemServicoResponseDTO finalizar(UUID id) {
        OrdemServicoEntity ordemServico = getOrdemServicoById(id);
        ordemServico.setStatus(StatusOrdemServico.FINALIZADA);
        ordemServico.setDataFinalizacao(LocalDateTime.now());
        return ordemServicoDataMapper.toResponse(ordemServicoRepository.save(ordemServico));
    }

    @Override
    public OrdemServicoResponseDTO entregar(UUID id) {
        OrdemServicoEntity ordemServico = getOrdemServicoById(id);
        if (ordemServico.getStatus() != StatusOrdemServico.FINALIZADA) {
            throw new IllegalStateException("Ordem de servico must be FINALIZADA before ENTREGUE");
        }
        ordemServico.setStatus(StatusOrdemServico.ENTREGUE);
        return ordemServicoDataMapper.toResponse(ordemServicoRepository.save(ordemServico));
    }

    @Override
    public AcompanhamentoOrdemServicoResponseDTO consultarAcompanhamento(UUID id, String cpfCnpj) {
        OrdemServicoEntity ordemServico = getOrdemServicoById(id);
        String normalizedCpfCnpj = normalizeAndValidateCpfCnpj(cpfCnpj);
        String clienteCpfCnpj = CpfCnpjValidator.normalize(ordemServico.getCliente().getCpfCnpj());

        if (!clienteCpfCnpj.equals(normalizedCpfCnpj)) {
            throw new EntityNotFoundException("OrdemServico not found for informed CPF/CNPJ");
        }

        return new AcompanhamentoOrdemServicoResponseDTO(
                ordemServico.getId(),
                clienteCpfCnpj,
                ordemServico.getStatus().name(),
                ordemServico.getValorOrcamento(),
                ordemServico.getOrcamentoAprovado(),
                ordemServico.getDataAbertura(),
                ordemServico.getDataEnvioOrcamento(),
                ordemServico.getDataFinalizacao()
        );
    }

    @Override
    public void delete(UUID id) {
        if (!ordemServicoRepository.existsById(id)) {
            throw new EntityNotFoundException("OrdemServico not found: " + id);
        }
        ordemServicoRepository.deleteById(id);
    }

    private OrdemServicoEntity getOrdemServicoById(UUID id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrdemServico not found: " + id));
    }

    private ClienteEntity findClienteById(UUID clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente not found: " + clienteId));
    }

    private VeiculoEntity findVeiculoById(UUID veiculoId) {
        return veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new EntityNotFoundException("Veiculo not found: " + veiculoId));
    }

    private VeiculoEntity resolveVeiculo(ClienteEntity cliente, OrdemServicoCreateByClienteRequestDTO request) {
        String normalizedPlaca = normalizeAndValidatePlaca(request.veiculoPlaca());
        Optional<VeiculoEntity> existingVeiculo = veiculoRepository.findByPlaca(normalizedPlaca);

        if (existingVeiculo.isPresent()) {
            VeiculoEntity veiculo = existingVeiculo.get();
            if (!veiculo.getCliente().getId().equals(cliente.getId())) {
                throw new IllegalArgumentException("Placa already linked to another cliente");
            }
            return veiculo;
        }

        if (request.veiculoMarca() == null || request.veiculoModelo() == null || request.veiculoAno() == null) {
            throw new IllegalArgumentException("Veiculo data is required when plate does not exist");
        }

        VeiculoEntity novoVeiculo = new VeiculoEntity();
        novoVeiculo.setCliente(cliente);
        novoVeiculo.setPlaca(normalizedPlaca);
        novoVeiculo.setMarca(request.veiculoMarca());
        novoVeiculo.setModelo(request.veiculoModelo());
        novoVeiculo.setAno(request.veiculoAno());
        return veiculoRepository.save(novoVeiculo);
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

    private List<PecaEntity> findPecasByIds(List<UUID> pecasIds) {
        if (pecasIds == null || pecasIds.isEmpty()) {
            return List.of();
        }
        return pecasIds.stream()
                .map(pecaId -> pecaRepository.findById(pecaId)
                        .orElseThrow(() -> new EntityNotFoundException("Peca not found: " + pecaId)))
                .toList();
    }

    private BigDecimal calculateOrcamento(List<ServicoEntity> servicos, List<PecaEntity> pecas) {
        BigDecimal valorServicos = BigDecimal.valueOf(servicos.size()).multiply(BigDecimal.valueOf(150));
        BigDecimal valorPecas = pecas.stream()
                .map(PecaEntity::getPrecoUnitario)
                .map(valor -> valor == null ? BigDecimal.ZERO : valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return valorServicos.add(valorPecas);
    }

    private StatusOrdemServico parseStatusOrDefault(String status, StatusOrdemServico defaultValue) {
        if (status == null || status.isBlank()) {
            return defaultValue;
        }
        return parseStatus(status);
    }

    private StatusOrdemServico parseStatus(String status) {
        try {
            return StatusOrdemServico.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException(
                    "Invalid status: " + status + ". Allowed values: RECEBIDA, EM_DIAGNOSTICO, AGUARDANDO_APROVACAO, EM_EXECUCAO, FINALIZADA, ENTREGUE");
        }
    }

    private String normalizeAndValidateCpfCnpj(String cpfCnpj) {
        String normalized = CpfCnpjValidator.normalize(cpfCnpj);
        if (!CpfCnpjValidator.isValid(normalized)) {
            throw new IllegalArgumentException("Invalid CPF/CNPJ");
        }
        return normalized;
    }

    private String normalizeAndValidatePlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("Placa is required");
        }

        String normalized = placa.toUpperCase().replace("-", "").trim();
        if (!PLACA_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid placa format. Expected pattern AAA0A00");
        }
        return normalized;
    }
}
