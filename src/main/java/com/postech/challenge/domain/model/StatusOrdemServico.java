package com.postech.challenge.domain.model;

public enum StatusOrdemServico {
    RECEBIDA(1),
    EM_DIAGNOSTICO(3),
    AGUARDANDO_APROVACAO(4),
    EM_EXECUCAO(5),
    FINALIZADA(0),
    ENTREGUE(0);

    private final int prioridadeListagem;

    StatusOrdemServico(int prioridadeListagem) {
        this.prioridadeListagem = prioridadeListagem;
    }

    public int prioridadeListagem() {
        return prioridadeListagem;
    }

    public boolean isEncerrada() {
        return this == FINALIZADA || this == ENTREGUE;
    }
}
