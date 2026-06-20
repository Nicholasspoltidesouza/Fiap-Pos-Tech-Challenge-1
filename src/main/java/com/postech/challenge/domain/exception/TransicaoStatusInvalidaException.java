package com.postech.challenge.domain.exception;

import java.util.List;

import com.postech.challenge.domain.model.StatusOrdemServico;

public class TransicaoStatusInvalidaException extends DomainException {

    public TransicaoStatusInvalidaException(
            String acao,
            StatusOrdemServico statusAtual,
            List<StatusOrdemServico> statusPermitidos) {
        super("Cannot " + acao + " when status is " + statusAtual + ". Allowed: " + statusPermitidos);
    }
}
