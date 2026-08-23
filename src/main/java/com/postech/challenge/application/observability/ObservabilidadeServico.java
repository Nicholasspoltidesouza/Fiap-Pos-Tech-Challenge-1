package com.postech.challenge.application.observability;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class ObservabilidadeServico {

    private final Counter ordensCriadas;
    private final Counter falhasProcessamento;
    private final Counter errosIntegracao;

    public ObservabilidadeServico(MeterRegistry meterRegistry) {
        this.ordensCriadas = Counter.builder("oficina.os.created")
                .description("Work orders created")
                .register(meterRegistry);
        this.falhasProcessamento = Counter.builder("oficina.os.processing.failures")
                .description("Work order processing failures")
                .register(meterRegistry);
        this.errosIntegracao = Counter.builder("oficina.integration.errors")
                .description("Downstream integration errors")
                .register(meterRegistry);
    }

    public void registrarCriacao() {
        ordensCriadas.increment();
    }

    public void registrarFalhaProcessamento() {
        falhasProcessamento.increment();
    }

    public void registrarErroIntegracao() {
        errosIntegracao.increment();
    }
}
