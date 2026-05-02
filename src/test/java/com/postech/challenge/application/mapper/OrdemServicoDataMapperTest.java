package com.postech.challenge.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.postech.challenge.application.dto.OrdemServicoResponseDTO;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;
import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;
import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;
import com.postech.challenge.infrastructure.persistence.entity.PecaEntity;
import com.postech.challenge.infrastructure.persistence.entity.ServicoEntity;
import com.postech.challenge.domain.model.StatusOrdemServico;
import com.postech.challenge.infrastructure.persistence.entity.VeiculoEntity;

class OrdemServicoDataMapperTest {

    private final OrdemServicoDataMapper ordemServicoDataMapper = new OrdemServicoDataMapper();

    @Test
    void shouldMapEntityToResponse() {
        UUID ordemId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();
        UUID insumoId = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();
        LocalDateTime dataAbertura = LocalDateTime.now();
        LocalDateTime dataFinalizacao = dataAbertura.plusHours(2);

        OrdemServicoEntity entity = new OrdemServicoEntity();
        entity.setId(ordemId);
        entity.setCliente(buildCliente(clienteId));
        entity.setVeiculo(buildVeiculo(veiculoId));
        entity.setStatus(StatusOrdemServico.EM_EXECUCAO);
        entity.setDataAbertura(dataAbertura);
        entity.setDataFinalizacao(dataFinalizacao);
        entity.setValorOrcamento(BigDecimal.valueOf(250));
        entity.setOrcamentoAprovado(Boolean.TRUE);
        entity.setDataEnvioOrcamento(dataAbertura.plusMinutes(30));
        entity.setServicosSolicitados(List.of(buildServico(servicoId)));
        entity.setInsumosSolicitados(List.of(buildInsumo(insumoId)));
        entity.setPecasSolicitadas(List.of(buildPeca(pecaId)));

        OrdemServicoResponseDTO response = ordemServicoDataMapper.toResponse(entity);

        assertEquals(ordemId, response.id());
        assertEquals(clienteId, response.clienteId());
        assertEquals(veiculoId, response.veiculoId());
        assertEquals("EM_EXECUCAO", response.status());
        assertEquals(dataAbertura, response.dataAbertura());
        assertEquals(dataFinalizacao, response.dataFinalizacao());
        assertEquals(BigDecimal.valueOf(250), response.valorOrcamento());
        assertEquals(Boolean.TRUE, response.orcamentoAprovado());
        assertEquals(dataAbertura.plusMinutes(30), response.dataEnvioOrcamento());
        assertEquals(List.of(servicoId), response.servicosSolicitadosIds());
        assertEquals(List.of(insumoId), response.insumosSolicitadosIds());
        assertEquals(List.of(pecaId), response.pecasSolicitadasIds());
    }

    @Test
    void shouldMapToEntity() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        LocalDateTime dataAbertura = LocalDateTime.now();
        LocalDateTime dataFinalizacao = dataAbertura.plusDays(1);

        ClienteEntity cliente = buildCliente(clienteId);
        VeiculoEntity veiculo = buildVeiculo(veiculoId);
        List<ServicoEntity> servicos = List.of(buildServico(UUID.randomUUID()));
        List<InsumoEntity> insumos = List.of(buildInsumo(UUID.randomUUID()));
        List<PecaEntity> pecas = List.of(buildPeca(UUID.randomUUID()));

        OrdemServicoEntity entity = ordemServicoDataMapper.toEntity(
                cliente,
                veiculo,
                StatusOrdemServico.RECEBIDA,
                dataAbertura,
                dataFinalizacao,
                BigDecimal.valueOf(120),
                Boolean.FALSE,
                dataAbertura.plusMinutes(5),
                servicos,
                insumos,
                pecas
        );

        assertEquals(clienteId, entity.getCliente().getId());
        assertEquals(veiculoId, entity.getVeiculo().getId());
        assertEquals(StatusOrdemServico.RECEBIDA, entity.getStatus());
        assertEquals(dataAbertura, entity.getDataAbertura());
        assertEquals(dataFinalizacao, entity.getDataFinalizacao());
        assertEquals(BigDecimal.valueOf(120), entity.getValorOrcamento());
        assertEquals(Boolean.FALSE, entity.getOrcamentoAprovado());
        assertEquals(dataAbertura.plusMinutes(5), entity.getDataEnvioOrcamento());
        assertEquals(1, entity.getServicosSolicitados().size());
        assertEquals(1, entity.getInsumosSolicitados().size());
        assertEquals(1, entity.getPecasSolicitadas().size());
    }

    @Test
    void shouldUpdateEntity() {
        OrdemServicoEntity entity = new OrdemServicoEntity();
        entity.setStatus(StatusOrdemServico.RECEBIDA);

        LocalDateTime dataAbertura = LocalDateTime.now();
        LocalDateTime dataFinalizacao = dataAbertura.plusHours(3);
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        ordemServicoDataMapper.updateEntity(
                entity,
                buildCliente(clienteId),
                buildVeiculo(veiculoId),
                StatusOrdemServico.FINALIZADA,
                dataAbertura,
                dataFinalizacao,
                BigDecimal.valueOf(999),
                Boolean.TRUE,
                dataAbertura.plusMinutes(10),
                List.of(buildServico(UUID.randomUUID())),
                List.of(buildInsumo(UUID.randomUUID())),
                List.of(buildPeca(UUID.randomUUID()))
        );

        assertEquals(clienteId, entity.getCliente().getId());
        assertEquals(veiculoId, entity.getVeiculo().getId());
        assertEquals(StatusOrdemServico.FINALIZADA, entity.getStatus());
        assertEquals(dataAbertura, entity.getDataAbertura());
        assertEquals(dataFinalizacao, entity.getDataFinalizacao());
        assertEquals(BigDecimal.valueOf(999), entity.getValorOrcamento());
        assertEquals(Boolean.TRUE, entity.getOrcamentoAprovado());
        assertEquals(dataAbertura.plusMinutes(10), entity.getDataEnvioOrcamento());
        assertEquals(1, entity.getServicosSolicitados().size());
        assertEquals(1, entity.getInsumosSolicitados().size());
        assertEquals(1, entity.getPecasSolicitadas().size());
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
        return peca;
    }
}
