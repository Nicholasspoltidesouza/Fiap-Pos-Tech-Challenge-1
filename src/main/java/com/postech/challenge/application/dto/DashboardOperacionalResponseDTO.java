package com.postech.challenge.application.dto;

import java.util.List;

public record DashboardOperacionalResponseDTO(
        List<VolumeDiarioOrdemServicoResponseDTO> volumeDiario,
        List<TempoMedioStatusResponseDTO> tempoMedioPorStatus,
        List<TempoMedioServicoResponseDTO> tempoMedioPorServico,
        double errosIntegracao
) {
}
