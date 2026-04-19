package com.postech.challenge.application.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.postech.challenge.application.dto.OrdemServicoResponseDTO;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;
import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;
import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;
import com.postech.challenge.infrastructure.persistence.entity.PecaEntity;
import com.postech.challenge.infrastructure.persistence.entity.ServicoEntity;
import com.postech.challenge.infrastructure.persistence.entity.StatusOrdemServico;
import com.postech.challenge.infrastructure.persistence.entity.VeiculoEntity;

@Component
public class OrdemServicoDataMapper {

    public OrdemServicoResponseDTO toResponse(OrdemServicoEntity ordemServico) {
        return new OrdemServicoResponseDTO(
                ordemServico.getId(),
                ordemServico.getCliente().getId(),
                ordemServico.getVeiculo().getId(),
                ordemServico.getStatus().name(),
                ordemServico.getDataAbertura(),
                ordemServico.getDataFinalizacao(),
                ordemServico.getValorOrcamento(),
                ordemServico.getOrcamentoAprovado(),
                ordemServico.getDataEnvioOrcamento(),
                ordemServico.getServicosSolicitados().stream().map(ServicoEntity::getId).toList(),
                ordemServico.getInsumosSolicitados().stream().map(InsumoEntity::getId).toList(),
                ordemServico.getPecasSolicitadas().stream().map(PecaEntity::getId).toList()
        );
    }

    public OrdemServicoEntity toEntity(
            ClienteEntity cliente,
            VeiculoEntity veiculo,
            StatusOrdemServico status,
            LocalDateTime dataAbertura,
            LocalDateTime dataFinalizacao,
            BigDecimal valorOrcamento,
            Boolean orcamentoAprovado,
            LocalDateTime dataEnvioOrcamento,
            List<ServicoEntity> servicosSolicitados,
            List<InsumoEntity> insumosSolicitados,
            List<PecaEntity> pecasSolicitadas) {
        OrdemServicoEntity ordemServico = new OrdemServicoEntity();
        ordemServico.setCliente(cliente);
        ordemServico.setVeiculo(veiculo);
        ordemServico.setStatus(status);
        ordemServico.setDataAbertura(dataAbertura);
        ordemServico.setDataFinalizacao(dataFinalizacao);
        ordemServico.setValorOrcamento(valorOrcamento);
        ordemServico.setOrcamentoAprovado(orcamentoAprovado);
        ordemServico.setDataEnvioOrcamento(dataEnvioOrcamento);
        ordemServico.setServicosSolicitados(servicosSolicitados);
        ordemServico.setInsumosSolicitados(insumosSolicitados);
        ordemServico.setPecasSolicitadas(pecasSolicitadas);
        return ordemServico;
    }

    public void updateEntity(
            OrdemServicoEntity ordemServico,
            ClienteEntity cliente,
            VeiculoEntity veiculo,
            StatusOrdemServico status,
            LocalDateTime dataAbertura,
            LocalDateTime dataFinalizacao,
            BigDecimal valorOrcamento,
            Boolean orcamentoAprovado,
            LocalDateTime dataEnvioOrcamento,
            List<ServicoEntity> servicosSolicitados,
            List<InsumoEntity> insumosSolicitados,
            List<PecaEntity> pecasSolicitadas) {
        ordemServico.setCliente(cliente);
        ordemServico.setVeiculo(veiculo);
        ordemServico.setStatus(status);
        ordemServico.setDataAbertura(dataAbertura);
        ordemServico.setDataFinalizacao(dataFinalizacao);
        ordemServico.setValorOrcamento(valorOrcamento);
        ordemServico.setOrcamentoAprovado(orcamentoAprovado);
        ordemServico.setDataEnvioOrcamento(dataEnvioOrcamento);
        ordemServico.setServicosSolicitados(servicosSolicitados);
        ordemServico.setInsumosSolicitados(insumosSolicitados);
        ordemServico.setPecasSolicitadas(pecasSolicitadas);
    }
}
