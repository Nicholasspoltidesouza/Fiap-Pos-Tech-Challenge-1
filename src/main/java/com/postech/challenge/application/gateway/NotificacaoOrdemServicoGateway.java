package com.postech.challenge.application.gateway;

import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;

public interface NotificacaoOrdemServicoGateway {

    void notificarAtualizacaoStatus(OrdemServicoEntity ordemServico);
}
