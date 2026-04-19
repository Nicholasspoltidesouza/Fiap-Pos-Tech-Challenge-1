package com.postech.challenge.application.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.postech.challenge.application.dto.OrdemServicoResponseDTO;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;
import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;
import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;
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
                ordemServico.getServicosSolicitados().stream().map(ServicoEntity::getId).toList(),
                ordemServico.getInsumosSolicitados().stream().map(InsumoEntity::getId).toList()
        );
    }

    public OrdemServicoEntity toEntity(
            ClienteEntity cliente,
            VeiculoEntity veiculo,
            StatusOrdemServico status,
            LocalDateTime dataAbertura,
            LocalDateTime dataFinalizacao,
            List<ServicoEntity> servicosSolicitados,
            List<InsumoEntity> insumosSolicitados) {
        OrdemServicoEntity ordemServico = new OrdemServicoEntity();
        ordemServico.setCliente(cliente);
        ordemServico.setVeiculo(veiculo);
        ordemServico.setStatus(status);
        ordemServico.setDataAbertura(dataAbertura);
        ordemServico.setDataFinalizacao(dataFinalizacao);
        ordemServico.setServicosSolicitados(servicosSolicitados);
        ordemServico.setInsumosSolicitados(insumosSolicitados);
        return ordemServico;
    }

    public void updateEntity(
            OrdemServicoEntity ordemServico,
            ClienteEntity cliente,
            VeiculoEntity veiculo,
            StatusOrdemServico status,
            LocalDateTime dataAbertura,
            LocalDateTime dataFinalizacao,
            List<ServicoEntity> servicosSolicitados,
            List<InsumoEntity> insumosSolicitados) {
        ordemServico.setCliente(cliente);
        ordemServico.setVeiculo(veiculo);
        ordemServico.setStatus(status);
        ordemServico.setDataAbertura(dataAbertura);
        ordemServico.setDataFinalizacao(dataFinalizacao);
        ordemServico.setServicosSolicitados(servicosSolicitados);
        ordemServico.setInsumosSolicitados(insumosSolicitados);
    }
}
