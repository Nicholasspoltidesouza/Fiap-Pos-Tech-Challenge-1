package com.postech.challenge.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.challenge.application.dto.OrdemServicoCreateByClienteRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoResponseDTO;
import com.postech.challenge.application.gateway.OrcamentoNotificacaoGateway;
import com.postech.challenge.application.mapper.OrdemServicoDataMapper;
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

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceUsecaseImplTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private VeiculoRepository veiculoRepository;
    @Mock
    private ServicoRepository servicoRepository;
    @Mock
    private InsumoRepository insumoRepository;
    @Mock
    private PecaRepository pecaRepository;
    @Mock
    private OrdemServicoDataMapper ordemServicoDataMapper;
    @Mock
    private OrcamentoNotificacaoGateway orcamentoNotificacaoGateway;

    @InjectMocks
    private OrdemServicoServiceUsecaseImpl ordemServicoService;

    @Test
    void shouldFindAllOrdensServico() {
        OrdemServicoEntity entity = buildOrdemEntity(UUID.randomUUID());
        OrdemServicoResponseDTO response = buildResponse(entity.getId());

        when(ordemServicoRepository.findAll()).thenReturn(List.of(entity));
        when(ordemServicoDataMapper.toResponse(entity)).thenReturn(response);

        List<OrdemServicoResponseDTO> result = ordemServicoService.findAll();

        assertEquals(1, result.size());
        assertEquals(response, result.getFirst());
        verify(ordemServicoRepository).findAll();
    }

    @Test
    void shouldFindOrdemById() {
        UUID id = UUID.randomUUID();
        OrdemServicoEntity entity = buildOrdemEntity(id);
        OrdemServicoResponseDTO response = buildResponse(id);

        when(ordemServicoRepository.findById(id)).thenReturn(Optional.of(entity));
        when(ordemServicoDataMapper.toResponse(entity)).thenReturn(response);

        OrdemServicoResponseDTO result = ordemServicoService.findById(id);

        assertEquals(response, result);
        verify(ordemServicoRepository).findById(id);
    }

    @Test
    void shouldThrowWhenFindByIdAndOrdemDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(ordemServicoRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> ordemServicoService.findById(id));

        assertTrue(exception.getMessage().contains("OrdemServico not found"));
    }

    @Test
    void shouldCreateOrdemServico() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        UUID insumoId = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();
        OrdemServicoRequestDTO request = buildRequest(clienteId, veiculoId, servicoId, insumoId, pecaId, "RECEBIDA");

        ClienteEntity cliente = buildCliente(clienteId);
        VeiculoEntity veiculo = buildVeiculo(veiculoId);
        ServicoEntity servico = buildServico(servicoId);
        InsumoEntity insumo = buildInsumo(insumoId);
        PecaEntity peca = buildPeca(pecaId);
        OrdemServicoEntity entityToSave = buildOrdemEntity(UUID.randomUUID());
        OrdemServicoEntity savedEntity = buildOrdemEntity(UUID.randomUUID());
        OrdemServicoResponseDTO response = buildResponse(savedEntity.getId());

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(insumoRepository.findById(insumoId)).thenReturn(Optional.of(insumo));
        when(pecaRepository.findById(pecaId)).thenReturn(Optional.of(peca));
        when(ordemServicoDataMapper.toEntity(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(entityToSave);
        when(ordemServicoRepository.save(entityToSave)).thenReturn(savedEntity);
        when(ordemServicoDataMapper.toResponse(savedEntity)).thenReturn(response);

        OrdemServicoResponseDTO result = ordemServicoService.create(request);

        assertEquals(response, result);
        verify(clienteRepository).findById(clienteId);
        verify(veiculoRepository).findById(veiculoId);
        verify(servicoRepository).findById(servicoId);
        verify(insumoRepository).findById(insumoId);
        verify(pecaRepository).findById(pecaId);
        verify(ordemServicoRepository).save(entityToSave);
    }

    @Test
    void shouldThrowWhenCreateAndStatusIsInvalid() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        OrdemServicoRequestDTO request = buildRequest(
                clienteId, veiculoId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "INVALIDO");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(buildCliente(clienteId)));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(buildVeiculo(veiculoId)));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ordemServicoService.create(request));

        assertTrue(exception.getMessage().contains("Invalid status"));
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    void shouldCreateWithDefaultStatusWhenStatusIsNull() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        OrdemServicoRequestDTO request = new OrdemServicoRequestDTO(
                clienteId,
                veiculoId,
                null,
                LocalDateTime.now(),
                null,
                List.of(),
                List.of(),
                List.of());
        ClienteEntity cliente = buildCliente(clienteId);
        VeiculoEntity veiculo = buildVeiculo(veiculoId);
        OrdemServicoEntity entityToSave = buildOrdemEntity(UUID.randomUUID());
        OrdemServicoEntity savedEntity = buildOrdemEntity(UUID.randomUUID());
        OrdemServicoResponseDTO response = buildResponse(savedEntity.getId());

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
        when(ordemServicoDataMapper.toEntity(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(entityToSave);
        when(ordemServicoRepository.save(entityToSave)).thenReturn(savedEntity);
        when(ordemServicoDataMapper.toResponse(savedEntity)).thenReturn(response);

        OrdemServicoResponseDTO result = ordemServicoService.create(request);

        assertEquals(response, result);
        ArgumentCaptor<StatusOrdemServico> statusCaptor = ArgumentCaptor.forClass(StatusOrdemServico.class);
        verify(ordemServicoDataMapper).toEntity(
                any(), any(), statusCaptor.capture(), any(), any(), any(), any(), any(), any(), any(), any());
        assertEquals(StatusOrdemServico.RECEBIDA, statusCaptor.getValue());
    }

    @Test
    void shouldCreateWithoutResolvingItemsWhenListsAreNull() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        OrdemServicoRequestDTO request = new OrdemServicoRequestDTO(
                clienteId,
                veiculoId,
                "RECEBIDA",
                LocalDateTime.now(),
                null,
                null,
                null,
                null);
        ClienteEntity cliente = buildCliente(clienteId);
        VeiculoEntity veiculo = buildVeiculo(veiculoId);
        OrdemServicoEntity entityToSave = buildOrdemEntity(UUID.randomUUID());
        OrdemServicoEntity savedEntity = buildOrdemEntity(UUID.randomUUID());
        OrdemServicoResponseDTO response = buildResponse(savedEntity.getId());

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
        when(ordemServicoDataMapper.toEntity(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(entityToSave);
        when(ordemServicoRepository.save(entityToSave)).thenReturn(savedEntity);
        when(ordemServicoDataMapper.toResponse(savedEntity)).thenReturn(response);

        OrdemServicoResponseDTO result = ordemServicoService.create(request);

        assertEquals(response, result);
        verify(servicoRepository, never()).findById(any());
        verify(insumoRepository, never()).findById(any());
        verify(pecaRepository, never()).findById(any());
    }

    @Test
    void shouldThrowWhenCreateAndServicoNotFound() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        OrdemServicoRequestDTO request = buildRequest(clienteId, veiculoId, servicoId, UUID.randomUUID(), UUID.randomUUID(), "RECEBIDA");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(buildCliente(clienteId)));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(buildVeiculo(veiculoId)));
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> ordemServicoService.create(request));

        assertTrue(exception.getMessage().contains("Servico not found"));
    }

    @Test
    void shouldThrowWhenCreateAndInsumoNotFound() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        UUID insumoId = UUID.randomUUID();
        OrdemServicoRequestDTO request = buildRequest(clienteId, veiculoId, servicoId, insumoId, UUID.randomUUID(), "RECEBIDA");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(buildCliente(clienteId)));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(buildVeiculo(veiculoId)));
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(buildServico(servicoId)));
        when(insumoRepository.findById(insumoId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> ordemServicoService.create(request));

        assertTrue(exception.getMessage().contains("Insumo not found"));
    }

    @Test
    void shouldThrowWhenCreateAndPecaNotFound() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        UUID insumoId = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();
        OrdemServicoRequestDTO request = buildRequest(clienteId, veiculoId, servicoId, insumoId, pecaId, "RECEBIDA");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(buildCliente(clienteId)));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(buildVeiculo(veiculoId)));
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(buildServico(servicoId)));
        when(insumoRepository.findById(insumoId)).thenReturn(Optional.of(buildInsumo(insumoId)));
        when(pecaRepository.findById(pecaId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> ordemServicoService.create(request));

        assertTrue(exception.getMessage().contains("Peca not found"));
    }

    @Test
    void shouldUpdateOrdemServico() {
        UUID ordemId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        UUID insumoId = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();
        OrdemServicoRequestDTO request = buildRequest(clienteId, veiculoId, servicoId, insumoId, pecaId, "FINALIZADA");

        OrdemServicoEntity existing = buildOrdemEntity(ordemId);
        ClienteEntity cliente = buildCliente(clienteId);
        VeiculoEntity veiculo = buildVeiculo(veiculoId);
        ServicoEntity servico = buildServico(servicoId);
        InsumoEntity insumo = buildInsumo(insumoId);
        PecaEntity peca = buildPeca(pecaId);
        OrdemServicoResponseDTO response = buildResponse(ordemId);

        when(ordemServicoRepository.findById(ordemId)).thenReturn(Optional.of(existing));
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(insumoRepository.findById(insumoId)).thenReturn(Optional.of(insumo));
        when(pecaRepository.findById(pecaId)).thenReturn(Optional.of(peca));
        when(ordemServicoRepository.save(existing)).thenReturn(existing);
        when(ordemServicoDataMapper.toResponse(existing)).thenReturn(response);

        OrdemServicoResponseDTO result = ordemServicoService.update(ordemId, request);

        assertEquals(response, result);
        verify(ordemServicoRepository).findById(ordemId);
        verify(ordemServicoDataMapper).updateEntity(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
        verify(ordemServicoRepository).save(existing);
    }

    @Test
    void shouldThrowWhenUpdateAndOrdemDoesNotExist() {
        UUID id = UUID.randomUUID();
        OrdemServicoRequestDTO request = buildRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "RECEBIDA");
        when(ordemServicoRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> ordemServicoService.update(id, request));

        assertTrue(exception.getMessage().contains("OrdemServico not found"));
    }

    @Test
    void shouldDeleteOrdemServico() {
        UUID id = UUID.randomUUID();
        when(ordemServicoRepository.existsById(id)).thenReturn(true);

        ordemServicoService.delete(id);

        verify(ordemServicoRepository).existsById(id);
        verify(ordemServicoRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteAndOrdemDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(ordemServicoRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> ordemServicoService.delete(id));

        assertTrue(exception.getMessage().contains("OrdemServico not found"));
        verify(ordemServicoRepository, never()).deleteById(id);
    }

    @Test
    void shouldEnviarOrcamentoWhenStatusIsEmDiagnostico() {
        UUID ordemId = UUID.randomUUID();
        OrdemServicoEntity ordem = buildOrdemEntity(ordemId);
        ordem.setStatus(StatusOrdemServico.EM_DIAGNOSTICO);
        OrdemServicoResponseDTO response = buildResponse(ordemId);

        when(ordemServicoRepository.findById(ordemId)).thenReturn(Optional.of(ordem));
        when(ordemServicoRepository.save(ordem)).thenReturn(ordem);
        when(ordemServicoDataMapper.toResponse(ordem)).thenReturn(response);

        OrdemServicoResponseDTO result = ordemServicoService.enviarOrcamento(ordemId);

        assertEquals(response, result);
        assertEquals(StatusOrdemServico.AGUARDANDO_APROVACAO, ordem.getStatus());
        verify(ordemServicoRepository).save(ordem);
        verify(orcamentoNotificacaoGateway).enviarOrcamento(ordem);
    }

    @Test
    void shouldAprovarOrcamentoAndConsumeStock() {
        UUID ordemId = UUID.randomUUID();
        OrdemServicoEntity ordem = buildOrdemEntity(ordemId);
        ordem.setStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);
        PecaEntity peca = buildPeca(UUID.randomUUID());
        peca.setQuantidadeEstoque(2);
        InsumoEntity insumo = buildInsumo(UUID.randomUUID());
        insumo.setQuantidadeEstoque(3);
        ordem.setPecasSolicitadas(List.of(peca));
        ordem.setInsumosSolicitados(List.of(insumo));
        OrdemServicoResponseDTO response = buildResponse(ordemId);

        when(ordemServicoRepository.findById(ordemId)).thenReturn(Optional.of(ordem));
        when(pecaRepository.save(peca)).thenReturn(peca);
        when(insumoRepository.save(insumo)).thenReturn(insumo);
        when(ordemServicoRepository.save(ordem)).thenReturn(ordem);
        when(ordemServicoDataMapper.toResponse(ordem)).thenReturn(response);

        OrdemServicoResponseDTO result = ordemServicoService.aprovarOrcamento(ordemId, true);

        assertEquals(response, result);
        assertEquals(StatusOrdemServico.EM_EXECUCAO, ordem.getStatus());
        assertEquals(1, peca.getQuantidadeEstoque());
        assertEquals(2, insumo.getQuantidadeEstoque());
        verify(pecaRepository).save(peca);
        verify(insumoRepository).save(insumo);
        verify(ordemServicoRepository).save(ordem);
    }

    @Test
    void shouldThrowWhenAprovarOrcamentoAndPecaStockIsInsufficient() {
        UUID ordemId = UUID.randomUUID();
        OrdemServicoEntity ordem = buildOrdemEntity(ordemId);
        ordem.setStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);
        PecaEntity peca = buildPeca(UUID.randomUUID());
        peca.setQuantidadeEstoque(0);
        ordem.setPecasSolicitadas(List.of(peca));

        when(ordemServicoRepository.findById(ordemId)).thenReturn(Optional.of(ordem));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> ordemServicoService.aprovarOrcamento(ordemId, true));

        assertTrue(exception.getMessage().contains("Insufficient stock for peca"));
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenAprovarOrcamentoAndInsumoStockIsInsufficient() {
        UUID ordemId = UUID.randomUUID();
        OrdemServicoEntity ordem = buildOrdemEntity(ordemId);
        ordem.setStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);
        PecaEntity peca = buildPeca(UUID.randomUUID());
        peca.setQuantidadeEstoque(2);
        InsumoEntity insumo = buildInsumo(UUID.randomUUID());
        insumo.setQuantidadeEstoque(0);
        ordem.setPecasSolicitadas(List.of(peca));
        ordem.setInsumosSolicitados(List.of(insumo));

        when(ordemServicoRepository.findById(ordemId)).thenReturn(Optional.of(ordem));
        when(pecaRepository.save(peca)).thenReturn(peca);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> ordemServicoService.aprovarOrcamento(ordemId, true));

        assertTrue(exception.getMessage().contains("Insufficient stock for insumo"));
        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    void shouldAprovarOrcamentoFalseAndReturnToRecebida() {
        UUID ordemId = UUID.randomUUID();
        OrdemServicoEntity ordem = buildOrdemEntity(ordemId);
        ordem.setStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);
        OrdemServicoResponseDTO response = buildResponse(ordemId);

        when(ordemServicoRepository.findById(ordemId)).thenReturn(Optional.of(ordem));
        when(ordemServicoRepository.save(ordem)).thenReturn(ordem);
        when(ordemServicoDataMapper.toResponse(ordem)).thenReturn(response);

        OrdemServicoResponseDTO result = ordemServicoService.aprovarOrcamento(ordemId, false);

        assertEquals(response, result);
        assertEquals(StatusOrdemServico.RECEBIDA, ordem.getStatus());
        verify(ordemServicoRepository).save(ordem);
    }

    @Test
    void shouldAdicionarPecasAndRecalculateOrcamento() {
        UUID ordemId = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();
        OrdemServicoEntity ordem = buildOrdemEntity(ordemId);
        ordem.setServicosSolicitados(List.of(buildServico(UUID.randomUUID()), buildServico(UUID.randomUUID())));
        PecaEntity peca = buildPeca(pecaId);
        peca.setPrecoUnitario(BigDecimal.valueOf(120));
        OrdemServicoResponseDTO response = buildResponse(ordemId);

        when(ordemServicoRepository.findById(ordemId)).thenReturn(Optional.of(ordem));
        when(pecaRepository.findById(pecaId)).thenReturn(Optional.of(peca));
        when(ordemServicoRepository.save(ordem)).thenReturn(ordem);
        when(ordemServicoDataMapper.toResponse(ordem)).thenReturn(response);

        OrdemServicoResponseDTO result = ordemServicoService.adicionarPecas(ordemId, List.of(pecaId));

        assertEquals(response, result);
        assertEquals(1, ordem.getPecasSolicitadas().size());
        assertEquals(BigDecimal.valueOf(420), ordem.getValorOrcamento());
    }

    @Test
    void shouldCreateByClienteCpfCnpjWithExistingVeiculo() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();
        UUID insumoId = UUID.randomUUID();
        ClienteEntity cliente = buildCliente(clienteId);
        cliente.setCpfCnpj("52998224725");
        VeiculoEntity veiculo = buildVeiculo(veiculoId);
        veiculo.setCliente(cliente);
        veiculo.setPlaca("ABC1D23");
        ServicoEntity servico = buildServico(servicoId);
        PecaEntity peca = buildPeca(pecaId);
        InsumoEntity insumo = buildInsumo(insumoId);
        OrdemServicoEntity entityToSave = buildOrdemEntity(UUID.randomUUID());
        OrdemServicoEntity saved = buildOrdemEntity(UUID.randomUUID());
        OrdemServicoResponseDTO response = buildResponse(saved.getId());
        OrdemServicoCreateByClienteRequestDTO request = buildCreateByClienteRequest(
                "529.982.247-25",
                "ABC-1D23",
                servicoId,
                insumoId,
                pecaId);

        when(clienteRepository.findByCpfCnpj("52998224725")).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findByPlaca("ABC1D23")).thenReturn(Optional.of(veiculo));
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(insumoRepository.findById(insumoId)).thenReturn(Optional.of(insumo));
        when(pecaRepository.findById(pecaId)).thenReturn(Optional.of(peca));
        when(ordemServicoDataMapper.toEntity(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(entityToSave);
        when(ordemServicoRepository.save(entityToSave)).thenReturn(saved);
        when(ordemServicoDataMapper.toResponse(saved)).thenReturn(response);

        OrdemServicoResponseDTO result = ordemServicoService.createByClienteCpfCnpj(request);

        assertEquals(response, result);
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void shouldCreateByClienteCpfCnpjAndCreateVeiculoWhenNotExists() {
        UUID clienteId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();
        UUID insumoId = UUID.randomUUID();
        ClienteEntity cliente = buildCliente(clienteId);
        cliente.setCpfCnpj("52998224725");
        VeiculoEntity novoVeiculo = buildVeiculo(UUID.randomUUID());
        ServicoEntity servico = buildServico(servicoId);
        PecaEntity peca = buildPeca(pecaId);
        InsumoEntity insumo = buildInsumo(insumoId);
        OrdemServicoEntity entityToSave = buildOrdemEntity(UUID.randomUUID());
        OrdemServicoEntity saved = buildOrdemEntity(UUID.randomUUID());
        OrdemServicoResponseDTO response = buildResponse(saved.getId());
        OrdemServicoCreateByClienteRequestDTO request = buildCreateByClienteRequest(
                "52998224725",
                "ABC1D23",
                servicoId,
                insumoId,
                pecaId);

        when(clienteRepository.findByCpfCnpj("52998224725")).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findByPlaca("ABC1D23")).thenReturn(Optional.empty());
        when(veiculoRepository.save(any(VeiculoEntity.class))).thenReturn(novoVeiculo);
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(insumoRepository.findById(insumoId)).thenReturn(Optional.of(insumo));
        when(pecaRepository.findById(pecaId)).thenReturn(Optional.of(peca));
        when(ordemServicoDataMapper.toEntity(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(entityToSave);
        when(ordemServicoRepository.save(entityToSave)).thenReturn(saved);
        when(ordemServicoDataMapper.toResponse(saved)).thenReturn(response);

        OrdemServicoResponseDTO result = ordemServicoService.createByClienteCpfCnpj(request);

        assertEquals(response, result);
        verify(veiculoRepository).save(any(VeiculoEntity.class));
    }

    @Test
    void shouldThrowWhenCreateByClienteCpfCnpjAndVeiculoFromAnotherCliente() {
        UUID clienteId = UUID.randomUUID();
        ClienteEntity cliente = buildCliente(clienteId);
        cliente.setCpfCnpj("52998224725");
        ClienteEntity outroCliente = buildCliente(UUID.randomUUID());
        VeiculoEntity veiculo = buildVeiculo(UUID.randomUUID());
        veiculo.setCliente(outroCliente);
        veiculo.setPlaca("ABC1D23");
        OrdemServicoCreateByClienteRequestDTO request = buildCreateByClienteRequest(
                "52998224725",
                "ABC1D23",
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID());

        when(clienteRepository.findByCpfCnpj("52998224725")).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findByPlaca("ABC1D23")).thenReturn(Optional.of(veiculo));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ordemServicoService.createByClienteCpfCnpj(request));

        assertTrue(exception.getMessage().contains("Placa already linked"));
    }

    @Test
    void shouldThrowWhenCreateByClienteCpfCnpjAndPlateFormatIsInvalid() {
        ClienteEntity cliente = buildCliente(UUID.randomUUID());
        cliente.setCpfCnpj("52998224725");
        OrdemServicoCreateByClienteRequestDTO request = buildCreateByClienteRequest(
                "52998224725",
                "PLACAINVALIDA",
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID());

        when(clienteRepository.findByCpfCnpj("52998224725")).thenReturn(Optional.of(cliente));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ordemServicoService.createByClienteCpfCnpj(request));

        assertTrue(exception.getMessage().contains("Invalid placa format"));
    }

    @Test
    void shouldThrowWhenCreateByClienteCpfCnpjAndPlacaIsBlank() {
        ClienteEntity cliente = buildCliente(UUID.randomUUID());
        cliente.setCpfCnpj("52998224725");
        OrdemServicoCreateByClienteRequestDTO request = buildCreateByClienteRequest(
                "52998224725",
                "   ",
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID());

        when(clienteRepository.findByCpfCnpj("52998224725")).thenReturn(Optional.of(cliente));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ordemServicoService.createByClienteCpfCnpj(request));

        assertTrue(exception.getMessage().contains("Placa is required"));
    }

    @Test
    void shouldThrowWhenCreateByClienteCpfCnpjAndVeiculoDataMissingForNewPlate() {
        ClienteEntity cliente = buildCliente(UUID.randomUUID());
        cliente.setCpfCnpj("52998224725");
        OrdemServicoCreateByClienteRequestDTO request = new OrdemServicoCreateByClienteRequestDTO(
                "52998224725",
                "ABC1D23",
                null,
                "Corolla",
                2022,
                List.of(UUID.randomUUID()),
                List.of(UUID.randomUUID()),
                List.of(UUID.randomUUID()));

        when(clienteRepository.findByCpfCnpj("52998224725")).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findByPlaca("ABC1D23")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ordemServicoService.createByClienteCpfCnpj(request));

        assertTrue(exception.getMessage().contains("Veiculo data is required"));
    }

    @Test
    void shouldThrowWhenCreateByClienteCpfCnpjAndCpfCnpjIsInvalid() {
        OrdemServicoCreateByClienteRequestDTO request = buildCreateByClienteRequest(
                "11111111111",
                "ABC1D23",
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ordemServicoService.createByClienteCpfCnpj(request));

        assertTrue(exception.getMessage().contains("Invalid CPF/CNPJ"));
        verify(clienteRepository, never()).findByCpfCnpj(any());
    }

    @Test
    void shouldThrowWhenIniciarDiagnosticoWithInvalidStatus() {
        UUID ordemId = UUID.randomUUID();
        OrdemServicoEntity ordem = buildOrdemEntity(ordemId);
        ordem.setStatus(StatusOrdemServico.EM_EXECUCAO);
        when(ordemServicoRepository.findById(ordemId)).thenReturn(Optional.of(ordem));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> ordemServicoService.iniciarDiagnostico(ordemId));

        assertTrue(exception.getMessage().contains("Cannot iniciar diagnostico"));
    }

    @Test
    void shouldThrowWhenEntregarWithoutFinalizadaStatus() {
        UUID ordemId = UUID.randomUUID();
        OrdemServicoEntity ordem = buildOrdemEntity(ordemId);
        ordem.setStatus(StatusOrdemServico.EM_EXECUCAO);
        when(ordemServicoRepository.findById(ordemId)).thenReturn(Optional.of(ordem));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> ordemServicoService.entregar(ordemId));

        assertTrue(exception.getMessage().contains("Cannot entregar ordem"));
    }

    @Test
    void shouldConsultarAcompanhamentoSuccessfully() {
        UUID ordemId = UUID.randomUUID();
        OrdemServicoEntity ordem = buildOrdemEntity(ordemId);
        ClienteEntity cliente = buildCliente(UUID.randomUUID());
        cliente.setCpfCnpj("52998224725");
        ordem.setCliente(cliente);
        when(ordemServicoRepository.findById(ordemId)).thenReturn(Optional.of(ordem));

        var response = ordemServicoService.consultarAcompanhamento(ordemId, "529.982.247-25");

        assertEquals(ordemId, response.ordemServicoId());
        assertEquals("52998224725", response.clienteCpfCnpj());
    }

    @Test
    void shouldThrowWhenConsultarAcompanhamentoCpfDoesNotMatch() {
        UUID ordemId = UUID.randomUUID();
        OrdemServicoEntity ordem = buildOrdemEntity(ordemId);
        ClienteEntity cliente = buildCliente(UUID.randomUUID());
        cliente.setCpfCnpj("52998224725");
        ordem.setCliente(cliente);
        when(ordemServicoRepository.findById(ordemId)).thenReturn(Optional.of(ordem));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> ordemServicoService.consultarAcompanhamento(ordemId, "11144477735"));

        assertTrue(exception.getMessage().contains("not found for informed CPF/CNPJ"));
    }

    private OrdemServicoRequestDTO buildRequest(
            UUID clienteId,
            UUID veiculoId,
            UUID servicoId,
            UUID insumoId,
            UUID pecaId,
            String status) {
        return new OrdemServicoRequestDTO(
                clienteId,
                veiculoId,
                status,
                LocalDateTime.now(),
                null,
                List.of(servicoId),
                List.of(insumoId),
                List.of(pecaId));
    }

    private OrdemServicoCreateByClienteRequestDTO buildCreateByClienteRequest(
            String cpfCnpj,
            String placa,
            UUID servicoId,
            UUID insumoId,
            UUID pecaId) {
        return new OrdemServicoCreateByClienteRequestDTO(
                cpfCnpj,
                placa,
                "Toyota",
                "Corolla",
                2022,
                List.of(servicoId),
                List.of(insumoId),
                List.of(pecaId));
    }

    private OrdemServicoEntity buildOrdemEntity(UUID id) {
        OrdemServicoEntity ordem = new OrdemServicoEntity();
        ordem.setId(id);
        ordem.setCliente(buildCliente(UUID.randomUUID()));
        ordem.setVeiculo(buildVeiculo(UUID.randomUUID()));
        ordem.setStatus(StatusOrdemServico.RECEBIDA);
        ordem.setDataAbertura(LocalDateTime.now());
        ordem.setDataFinalizacao(null);
        ordem.setServicosSolicitados(List.of(buildServico(UUID.randomUUID())));
        ordem.setInsumosSolicitados(List.of(buildInsumo(UUID.randomUUID())));
        ordem.setPecasSolicitadas(List.of(buildPeca(UUID.randomUUID())));
        return ordem;
    }

    private OrdemServicoResponseDTO buildResponse(UUID id) {
        return new OrdemServicoResponseDTO(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "RECEBIDA",
                LocalDateTime.now(),
                null,
                BigDecimal.valueOf(300),
                null,
                null,
                List.of(UUID.randomUUID()),
                List.of(UUID.randomUUID()),
                List.of(UUID.randomUUID()));
    }

    private ClienteEntity buildCliente(UUID id) {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setId(id);
        return cliente;
    }

    private VeiculoEntity buildVeiculo(UUID id) {
        VeiculoEntity veiculo = new VeiculoEntity();
        veiculo.setId(id);
        return veiculo;
    }

    private ServicoEntity buildServico(UUID id) {
        ServicoEntity servico = new ServicoEntity();
        servico.setId(id);
        return servico;
    }

    private InsumoEntity buildInsumo(UUID id) {
        InsumoEntity insumo = new InsumoEntity();
        insumo.setId(id);
        return insumo;
    }

    private PecaEntity buildPeca(UUID id) {
        PecaEntity peca = new PecaEntity();
        peca.setId(id);
        peca.setPrecoUnitario(BigDecimal.TEN);
        return peca;
    }
}
