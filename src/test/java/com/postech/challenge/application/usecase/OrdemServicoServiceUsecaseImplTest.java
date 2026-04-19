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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.challenge.application.dto.OrdemServicoRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoResponseDTO;
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
