package com.postech.challenge.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.postech.challenge.domain.exception.TransicaoStatusInvalidaException;

public class OrdemServico {

    private static final BigDecimal VALOR_POR_SERVICO = BigDecimal.valueOf(150);

    private final UUID id;
    private StatusOrdemServico status;
    private final LocalDateTime dataAbertura;
    private LocalDateTime dataFinalizacao;
    private LocalDateTime dataEnvioOrcamento;
    private BigDecimal valorOrcamento;
    private Boolean orcamentoAprovado;
    private int quantidadeServicos;
    private List<BigDecimal> precosPecas;

    private OrdemServico(
            UUID id,
            StatusOrdemServico status,
            LocalDateTime dataAbertura,
            LocalDateTime dataFinalizacao,
            LocalDateTime dataEnvioOrcamento,
            BigDecimal valorOrcamento,
            Boolean orcamentoAprovado,
            int quantidadeServicos,
            List<BigDecimal> precosPecas) {
        this.id = id;
        this.status = status;
        this.dataAbertura = dataAbertura;
        this.dataFinalizacao = dataFinalizacao;
        this.dataEnvioOrcamento = dataEnvioOrcamento;
        this.valorOrcamento = valorOrcamento;
        this.orcamentoAprovado = orcamentoAprovado;
        this.quantidadeServicos = quantidadeServicos;
        this.precosPecas = new ArrayList<>(precosPecas == null ? List.of() : precosPecas);
    }

    public static OrdemServico reconstituir(
            UUID id,
            StatusOrdemServico status,
            LocalDateTime dataAbertura,
            LocalDateTime dataFinalizacao,
            LocalDateTime dataEnvioOrcamento,
            BigDecimal valorOrcamento,
            Boolean orcamentoAprovado,
            int quantidadeServicos,
            List<BigDecimal> precosPecas) {
        return new OrdemServico(
                id,
                status,
                dataAbertura,
                dataFinalizacao,
                dataEnvioOrcamento,
                valorOrcamento,
                orcamentoAprovado,
                quantidadeServicos,
                precosPecas);
    }

    public void iniciarDiagnostico() {
        garantirStatus("iniciar diagnostico", StatusOrdemServico.RECEBIDA);
        this.status = StatusOrdemServico.EM_DIAGNOSTICO;
    }

    public void enviarOrcamento() {
        garantirStatus("enviar orcamento", StatusOrdemServico.EM_DIAGNOSTICO);
        this.valorOrcamento = calcularOrcamento();
        this.dataEnvioOrcamento = LocalDateTime.now();
        this.status = StatusOrdemServico.AGUARDANDO_APROVACAO;
    }

    public void aprovarOrcamento(boolean aprovado) {
        garantirStatus("aprovar orcamento", StatusOrdemServico.AGUARDANDO_APROVACAO);
        this.orcamentoAprovado = aprovado;
        this.status = aprovado ? StatusOrdemServico.EM_EXECUCAO : StatusOrdemServico.RECEBIDA;
    }

    public void finalizar() {
        garantirStatus("finalizar ordem", StatusOrdemServico.EM_EXECUCAO);
        this.status = StatusOrdemServico.FINALIZADA;
        this.dataFinalizacao = LocalDateTime.now();
    }

    public void entregar() {
        garantirStatus("entregar ordem", StatusOrdemServico.FINALIZADA);
        this.status = StatusOrdemServico.ENTREGUE;
    }

    public void atualizarPecas(List<BigDecimal> precosPecas) {
        this.precosPecas = new ArrayList<>(precosPecas == null ? List.of() : precosPecas);
        this.valorOrcamento = calcularOrcamento();
    }

    public BigDecimal calcularOrcamento() {
        return calcularOrcamento(quantidadeServicos, precosPecas);
    }

    public static BigDecimal calcularOrcamento(int quantidadeServicos, List<BigDecimal> precosPecas) {
        BigDecimal valorServicos = VALOR_POR_SERVICO.multiply(BigDecimal.valueOf(quantidadeServicos));
        BigDecimal valorPecas = (precosPecas == null ? List.<BigDecimal>of() : precosPecas).stream()
                .map(valor -> valor == null ? BigDecimal.ZERO : valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return valorServicos.add(valorPecas);
    }

    private void garantirStatus(String acao, StatusOrdemServico... permitidos) {
        List<StatusOrdemServico> permitidosLista = List.of(permitidos);
        if (!permitidosLista.contains(this.status)) {
            throw new TransicaoStatusInvalidaException(acao, this.status, permitidosLista);
        }
    }

    public UUID getId() {
        return id;
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public LocalDateTime getDataEnvioOrcamento() {
        return dataEnvioOrcamento;
    }

    public BigDecimal getValorOrcamento() {
        return valorOrcamento;
    }

    public Boolean getOrcamentoAprovado() {
        return orcamentoAprovado;
    }
}
