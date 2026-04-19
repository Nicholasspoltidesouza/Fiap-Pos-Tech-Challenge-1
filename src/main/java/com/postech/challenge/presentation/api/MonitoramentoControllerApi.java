package com.postech.challenge.presentation.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postech.challenge.application.dto.TempoMedioServicoResponseDTO;
import com.postech.challenge.application.usecase.MonitoramentoServiceUsecase;
import com.postech.challenge.presentation.api.doc.MonitoramentoControllerApiDoc;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/monitoramento")
@Tag(name = "Monitoramento", description = "Endpoints de monitoramento operacional")
public class MonitoramentoControllerApi extends MonitoramentoControllerApiDoc {

    private final MonitoramentoServiceUsecase monitoramentoService;

    public MonitoramentoControllerApi(MonitoramentoServiceUsecase monitoramentoService) {
        this.monitoramentoService = monitoramentoService;
    }

    @GetMapping("/servicos/tempo-medio")
    @Override
    public ResponseEntity<List<TempoMedioServicoResponseDTO>> findTempoMedioExecucaoPorServico() {
        return ResponseEntity.ok(monitoramentoService.findTempoMedioExecucaoPorServico());
    }
}
