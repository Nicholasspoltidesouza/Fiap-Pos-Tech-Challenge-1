package com.postech.challenge.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.postech.challenge.application.dto.AcompanhamentoOrdemServicoResponseDTO;
import com.postech.challenge.application.dto.OrdemServicoCreateByClienteRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoResponseDTO;
import com.postech.challenge.application.gateway.NotificacaoOrdemServicoGateway;
import com.postech.challenge.application.mapper.OrdemServicoDataMapper;
import com.postech.challenge.domain.model.OrdemServico;
import com.postech.challenge.domain.model.StatusOrdemServico;
import com.postech.challenge.domain.model.vo.CpfCnpj;
import com.postech.challenge.domain.model.vo.Placa;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;
import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;
import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;
import com.postech.challenge.infrastructure.persistence.entity.PecaEntity;
import com.postech.challenge.infrastructure.persistence.entity.ServicoEntity;
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

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final ServicoRepository servicoRepository;
    private final InsumoRepository insumoRepository;
    private final PecaRepository pecaRepository;
    private final OrdemServicoDataMapper ordemServicoDataMapper;
    private final NotificacaoOrdemServicoGateway notificacaoGateway;

    public OrdemServicoServiceUsecaseImpl(
            OrdemServicoRepository ordemServicoRepository,
            ClienteRepository clienteRepository,
            VeiculoRepository veiculoRepository,
            ServicoRepository servicoRepository,
            InsumoRepository insumoRepository,
            PecaRepository pecaRepository,
            OrdemServicoDataMapper ordemServicoDataMapper,
            NotificacaoOrdemServicoGateway notificacaoGateway) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.servicoRepository = servicoRepository;
        this.insumoRepository = insumoRepository;
        this.pecaRepository = pecaRepository;
        this.ordemServicoDataMapper = ordemServicoDataMapper;
        this.notificacaoGateway = notificacaoGateway;
    }

    @Override
    public List<OrdemServicoResponseDTO> findAll() {
        return ordemServicoRepository.findAll()
                .stream()
                .map(ordemServicoDataMapper::toResponse)
                .toList();
    }

    @Override
    public List<OrdemServicoResponseDTO> listarAtivas() {
        return ordemServicoRepository.findAtivasOrdenadas()
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
        BigDecimal valorOrcamento = calcularOrcamento(servicos, pecas);

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
        String normalizedCpfCnpj = CpfCnpj.of(request.clienteCpfCnpj()).value();
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
                calcularOrcamento(servicos, pecas),
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
        BigDecimal valorOrcamento = calcularOrcamento(servicos, pecas);

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

        OrdemServico ordem = toDomain(ordemServico);
        ordem.atualizarPecas(precosDasPecas(pecas));
        ordemServico.setValorOrcamento(ordem.getValorOrcamento());

        return ordemServicoDataMapper.toResponse(ordemServicoRepository.save(ordemServico));
    }

    @Override
    public OrdemServicoResponseDTO iniciarDiagnostico(UUID id) {
        return aplicarTransicao(id, OrdemServico::iniciarDiagnostico);
    }

    @Override
    public OrdemServicoResponseDTO enviarOrcamento(UUID id) {
        return aplicarTransicao(id, OrdemServico::enviarOrcamento);
    }

    @Override
    public OrdemServicoResponseDTO aprovarOrcamento(UUID id, boolean aprovado) {
        OrdemServicoEntity ordemServico = getOrdemServicoById(id);
        OrdemServico ordem = toDomain(ordemServico);
        ordem.aprovarOrcamento(aprovado);

        if (aprovado) {
            consumirEstoquePecas(ordemServico.getPecasSolicitadas());
            consumirEstoqueInsumos(ordemServico.getInsumosSolicitados());
        }

        applyDomain(ordemServico, ordem);
        OrdemServicoEntity atualizada = ordemServicoRepository.save(ordemServico);
        notificacaoGateway.notificarAtualizacaoStatus(atualizada);
        return ordemServicoDataMapper.toResponse(atualizada);
    }

    @Override
    public OrdemServicoResponseDTO finalizar(UUID id) {
        return aplicarTransicao(id, OrdemServico::finalizar);
    }

    @Override
    public OrdemServicoResponseDTO entregar(UUID id) {
        return aplicarTransicao(id, OrdemServico::entregar);
    }

    @Override
    public AcompanhamentoOrdemServicoResponseDTO consultarAcompanhamento(UUID id, String cpfCnpj) {
        OrdemServicoEntity ordemServico = getOrdemServicoById(id);
        String normalizedCpfCnpj = CpfCnpj.of(cpfCnpj).value();
        String clienteCpfCnpj = CpfCnpj.normalize(ordemServico.getCliente().getCpfCnpj());

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

    private OrdemServicoResponseDTO aplicarTransicao(UUID id, java.util.function.Consumer<OrdemServico> transicao) {
        OrdemServicoEntity ordemServico = getOrdemServicoById(id);
        OrdemServico ordem = toDomain(ordemServico);
        transicao.accept(ordem);
        applyDomain(ordemServico, ordem);
        OrdemServicoEntity atualizada = ordemServicoRepository.save(ordemServico);
        notificacaoGateway.notificarAtualizacaoStatus(atualizada);
        return ordemServicoDataMapper.toResponse(atualizada);
    }

    private OrdemServico toDomain(OrdemServicoEntity entity) {
        return OrdemServico.reconstituir(
                entity.getId(),
                entity.getStatus(),
                entity.getDataAbertura(),
                entity.getDataFinalizacao(),
                entity.getDataEnvioOrcamento(),
                entity.getValorOrcamento(),
                entity.getOrcamentoAprovado(),
                entity.getServicosSolicitados().size(),
                precosDasPecas(entity.getPecasSolicitadas()));
    }

    private void applyDomain(OrdemServicoEntity entity, OrdemServico ordem) {
        entity.setStatus(ordem.getStatus());
        entity.setDataFinalizacao(ordem.getDataFinalizacao());
        entity.setDataEnvioOrcamento(ordem.getDataEnvioOrcamento());
        entity.setValorOrcamento(ordem.getValorOrcamento());
        entity.setOrcamentoAprovado(ordem.getOrcamentoAprovado());
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
        String normalizedPlaca = Placa.of(request.veiculoPlaca()).value();
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

    private BigDecimal calcularOrcamento(List<ServicoEntity> servicos, List<PecaEntity> pecas) {
        return OrdemServico.calcularOrcamento(servicos.size(), precosDasPecas(pecas));
    }

    private List<BigDecimal> precosDasPecas(List<PecaEntity> pecas) {
        return pecas.stream()
                .map(PecaEntity::getPrecoUnitario)
                .toList();
    }

    private void consumirEstoquePecas(List<PecaEntity> pecas) {
        for (PecaEntity peca : pecas) {
            int estoqueAtual = Optional.ofNullable(peca.getQuantidadeEstoque()).orElse(0);
            if (estoqueAtual <= 0) {
                throw new IllegalStateException("Insufficient stock for peca: " + peca.getNome());
            }
            peca.setQuantidadeEstoque(estoqueAtual - 1);
            pecaRepository.save(peca);
        }
    }

    private void consumirEstoqueInsumos(List<InsumoEntity> insumos) {
        for (InsumoEntity insumo : insumos) {
            int estoqueAtual = Optional.ofNullable(insumo.getQuantidadeEstoque()).orElse(0);
            if (estoqueAtual <= 0) {
                throw new IllegalStateException("Insufficient stock for insumo: " + insumo.getNome());
            }
            insumo.setQuantidadeEstoque(estoqueAtual - 1);
            insumoRepository.save(insumo);
        }
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
}
