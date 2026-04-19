package com.postech.challenge.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.postech.challenge.application.dto.OrdemServicoResponseDTO;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;
import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;
import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;
import com.postech.challenge.infrastructure.persistence.entity.ServicoEntity;
import com.postech.challenge.infrastructure.persistence.entity.StatusOrdemServico;
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
        LocalDateTime dataAbertura = LocalDateTime.now();
        LocalDateTime dataFinalizacao = dataAbertura.plusHours(2);

        OrdemServicoEntity entity = new OrdemServicoEntity();
        entity.setId(ordemId);
        entity.setCliente(buildCliente(clienteId));
        entity.setVeiculo(buildVeiculo(veiculoId));
        entity.setStatus(StatusOrdemServico.EM_ANDAMENTO);
        entity.setDataAbertura(dataAbertura);
        entity.setDataFinalizacao(dataFinalizacao);
        entity.setServicosSolicitados(List.of(buildServico(servicoId)));
        entity.setInsumosSolicitados(List.of(buildInsumo(insumoId)));

        OrdemServicoResponseDTO response = ordemServicoDataMapper.toResponse(entity);

        assertEquals(ordemId, response.id());
        assertEquals(clienteId, response.clienteId());
        assertEquals(veiculoId, response.veiculoId());
        assertEquals("EM_ANDAMENTO", response.status());
        assertEquals(dataAbertura, response.dataAbertura());
        assertEquals(dataFinalizacao, response.dataFinalizacao());
        assertEquals(List.of(servicoId), response.servicosSolicitadosIds());
        assertEquals(List.of(insumoId), response.insumosSolicitadosIds());
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

        OrdemServicoEntity entity = ordemServicoDataMapper.toEntity(
                cliente,
                veiculo,
                StatusOrdemServico.ABERTA,
                dataAbertura,
                dataFinalizacao,
                servicos,
                insumos
        );

        assertEquals(clienteId, entity.getCliente().getId());
        assertEquals(veiculoId, entity.getVeiculo().getId());
        assertEquals(StatusOrdemServico.ABERTA, entity.getStatus());
        assertEquals(dataAbertura, entity.getDataAbertura());
        assertEquals(dataFinalizacao, entity.getDataFinalizacao());
        assertEquals(1, entity.getServicosSolicitados().size());
        assertEquals(1, entity.getInsumosSolicitados().size());
    }

    @Test
    void shouldUpdateEntity() {
        OrdemServicoEntity entity = new OrdemServicoEntity();
        entity.setStatus(StatusOrdemServico.ABERTA);

        LocalDateTime dataAbertura = LocalDateTime.now();
        LocalDateTime dataFinalizacao = dataAbertura.plusHours(3);
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        ordemServicoDataMapper.updateEntity(
                entity,
                buildCliente(clienteId),
                buildVeiculo(veiculoId),
                StatusOrdemServico.CONCLUIDA,
                dataAbertura,
                dataFinalizacao,
                List.of(buildServico(UUID.randomUUID())),
                List.of(buildInsumo(UUID.randomUUID()))
        );

        assertEquals(clienteId, entity.getCliente().getId());
        assertEquals(veiculoId, entity.getVeiculo().getId());
        assertEquals(StatusOrdemServico.CONCLUIDA, entity.getStatus());
        assertEquals(dataAbertura, entity.getDataAbertura());
        assertEquals(dataFinalizacao, entity.getDataFinalizacao());
        assertEquals(1, entity.getServicosSolicitados().size());
        assertEquals(1, entity.getInsumosSolicitados().size());
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
}
