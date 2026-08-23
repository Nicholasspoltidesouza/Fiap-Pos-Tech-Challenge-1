package com.postech.challenge.application.dto;

import java.time.LocalDate;

public record DashboardOperacionalResponseDTO(
        LocalDate dataReferencia,
        long volumeDiarioOrdens,
        Double tempoMedioDiagnosticoMinutos,
        Double tempoMedioExecucaoMinutos,
        Double tempoMedioFinalizacaoMinutos,
        double falhasProcessamento,
        double errosIntegracao) {
}
