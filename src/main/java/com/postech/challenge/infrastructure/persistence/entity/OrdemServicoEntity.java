package com.postech.challenge.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.postech.challenge.domain.model.StatusOrdemServico;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_ordem_servico")
@BatchSize(size = 20)
public class OrdemServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private VeiculoEntity veiculo;

    @ManyToMany
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(
            name = "tb_ordem_servico_servico",
            joinColumns = @JoinColumn(name = "ordem_servico_id"),
            inverseJoinColumns = @JoinColumn(name = "servico_id"))
    private List<ServicoEntity> servicosSolicitados = new ArrayList<>();

    @ManyToMany
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(
            name = "tb_ordem_servico_insumo",
            joinColumns = @JoinColumn(name = "ordem_servico_id"),
            inverseJoinColumns = @JoinColumn(name = "insumo_id"))
    private List<InsumoEntity> insumosSolicitados = new ArrayList<>();

    @ManyToMany
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(
            name = "tb_ordem_servico_peca",
            joinColumns = @JoinColumn(name = "ordem_servico_id"),
            inverseJoinColumns = @JoinColumn(name = "peca_id"))
    private List<PecaEntity> pecasSolicitadas = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusOrdemServico status;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_finalizacao")
    private LocalDateTime dataFinalizacao;

    @Column(name = "valor_orcamento", precision = 10, scale = 2)
    private BigDecimal valorOrcamento;

    @Column(name = "orcamento_aprovado")
    private Boolean orcamentoAprovado;

    @Column(name = "data_envio_orcamento")
    private LocalDateTime dataEnvioOrcamento;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ClienteEntity getCliente() {
        return cliente;
    }

    public void setCliente(ClienteEntity cliente) {
        this.cliente = cliente;
    }

    public VeiculoEntity getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(VeiculoEntity veiculo) {
        this.veiculo = veiculo;
    }

    public List<ServicoEntity> getServicosSolicitados() {
        return servicosSolicitados;
    }

    public void setServicosSolicitados(List<ServicoEntity> servicosSolicitados) {
        this.servicosSolicitados = servicosSolicitados;
    }

    public List<InsumoEntity> getInsumosSolicitados() {
        return insumosSolicitados;
    }

    public void setInsumosSolicitados(List<InsumoEntity> insumosSolicitados) {
        this.insumosSolicitados = insumosSolicitados;
    }

    public List<PecaEntity> getPecasSolicitadas() {
        return pecasSolicitadas;
    }

    public void setPecasSolicitadas(List<PecaEntity> pecasSolicitadas) {
        this.pecasSolicitadas = pecasSolicitadas;
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public void setStatus(StatusOrdemServico status) {
        this.status = status;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(LocalDateTime dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public BigDecimal getValorOrcamento() {
        return valorOrcamento;
    }

    public void setValorOrcamento(BigDecimal valorOrcamento) {
        this.valorOrcamento = valorOrcamento;
    }

    public Boolean getOrcamentoAprovado() {
        return orcamentoAprovado;
    }

    public void setOrcamentoAprovado(Boolean orcamentoAprovado) {
        this.orcamentoAprovado = orcamentoAprovado;
    }

    public LocalDateTime getDataEnvioOrcamento() {
        return dataEnvioOrcamento;
    }

    public void setDataEnvioOrcamento(LocalDateTime dataEnvioOrcamento) {
        this.dataEnvioOrcamento = dataEnvioOrcamento;
    }
}