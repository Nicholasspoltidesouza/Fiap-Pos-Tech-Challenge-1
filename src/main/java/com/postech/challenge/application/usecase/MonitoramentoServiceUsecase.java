package com.postech.challenge.application.usecase;

import java.util.List;

import com.postech.challenge.application.dto.DashboardOperacionalResponseDTO;
import com.postech.challenge.application.dto.TempoMedioServicoResponseDTO;

public abstract class MonitoramentoServiceUsecase {
    public abstract List<TempoMedioServicoResponseDTO> findTempoMedioExecucaoPorServico();

    public abstract DashboardOperacionalResponseDTO getDashboardOperacional();
}
