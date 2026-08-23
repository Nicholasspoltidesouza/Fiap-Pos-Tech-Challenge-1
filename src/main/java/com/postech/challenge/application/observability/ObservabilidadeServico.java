package com.postech.challenge.application.observability;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

import com.postech.challenge.domain.model.StatusOrdemServico;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Component
public class ObservabilidadeServico {

    private final MeterRegistry meterRegistry;
    private final Counter ordensCriadas;
    private final Counter falhasProcessamento;
    private final Counter errosIntegracao;
    private final AtomicLong volumeDiario = new AtomicLong();
    private final AtomicReference<Double> tempoMedioDiagnosticoMinutos = new AtomicReference<>(0.0);
    private final AtomicReference<Double> tempoMedioExecucaoMinutos = new AtomicReference<>(0.0);
    private final AtomicReference<Double> tempoMedioFinalizacaoMinutos = new AtomicReference<>(0.0);

    public ObservabilidadeServico(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.ordensCriadas = Counter.builder("oficina.os.created")
                .description("Work orders created")
                .register(meterRegistry);
        this.falhasProcessamento = Counter.builder("oficina.os.processing.failures")
                .description("Work order processing failures")
                .register(meterRegistry);
        this.errosIntegracao = Counter.builder("oficina.integration.errors")
                .description("Downstream integration errors")
                .register(meterRegistry);
        Gauge.builder("oficina.os.dashboard.volume.daily", volumeDiario, AtomicLong::get)
                .description("Work orders opened today")
                .register(meterRegistry);
        Gauge.builder("oficina.os.dashboard.minutes", tempoMedioDiagnosticoMinutos, ObservabilidadeServico::gaugeValue)
                .description("Average minutes in a work-order status")
                .tag("status", "em_diagnostico")
                .register(meterRegistry);
        Gauge.builder("oficina.os.dashboard.minutes", tempoMedioExecucaoMinutos, ObservabilidadeServico::gaugeValue)
                .description("Average minutes in a work-order status")
                .tag("status", "em_execucao")
                .register(meterRegistry);
        Gauge.builder("oficina.os.dashboard.minutes", tempoMedioFinalizacaoMinutos, ObservabilidadeServico::gaugeValue)
                .description("Average minutes in a work-order status")
                .tag("status", "finalizada")
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

    public void registrarDuracaoStatus(StatusOrdemServico status, Duration duration) {
        Timer.builder("oficina.os.status.duration")
                .description("Time spent in a work-order status")
                .tag("status", status.name().toLowerCase(Locale.ROOT))
                .register(meterRegistry)
                .record(duration);
    }

    public void atualizarDashboard(
            long volumeDiarioOrdens,
            Double diagnosticoMinutos,
            Double execucaoMinutos,
            Double finalizacaoMinutos) {
        volumeDiario.set(volumeDiarioOrdens);
        tempoMedioDiagnosticoMinutos.set(diagnosticoMinutos == null ? 0.0 : diagnosticoMinutos);
        tempoMedioExecucaoMinutos.set(execucaoMinutos == null ? 0.0 : execucaoMinutos);
        tempoMedioFinalizacaoMinutos.set(finalizacaoMinutos == null ? 0.0 : finalizacaoMinutos);
    }

    public double contarCriacoes() {
        return ordensCriadas.count();
    }

    public double contarFalhasProcessamento() {
        return falhasProcessamento.count();
    }

    public double contarErrosIntegracao() {
        return errosIntegracao.count();
    }

    private static double gaugeValue(AtomicReference<Double> value) {
        Double current = value.get();
        return current == null ? 0.0 : current;
    }
}
