package com.postech.challenge.application.gateway;

import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;

public abstract class OrcamentoNotificacaoGateway {
    public abstract void enviarOrcamento(OrdemServicoEntity ordemServico);
}
