package com.postech.challenge.infrastructure.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.postech.challenge.application.gateway.OrcamentoNotificacaoGateway;
import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;

@Component
public class OrcamentoNotificacaoGatewayImpl extends OrcamentoNotificacaoGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcamentoNotificacaoGatewayImpl.class);

    @Override
    public void enviarOrcamento(OrdemServicoEntity ordemServico) {
        LOGGER.info(
                "Orcamento enviado para cliente. ordemId={}, clienteCpfCnpj={}, valor={}",
                ordemServico.getId(),
                ordemServico.getCliente().getCpfCnpj(),
                ordemServico.getValorOrcamento());
    }
}
