package com.postech.challenge.presentation.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postech.challenge.application.dto.AcompanhamentoOrdemServicoResponseDTO;
import com.postech.challenge.application.usecase.OrdemServicoServiceUsecase;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/public/ordens-servico")
@Tag(name = "Acompanhamento de Ordem de Servico", description = "Endpoints para acompanhamento de ordens pelo cliente")
public class OrdemServicoAcompanhamentoControllerApi {

    private final OrdemServicoServiceUsecase ordemServicoServiceUsecase;

    public OrdemServicoAcompanhamentoControllerApi(OrdemServicoServiceUsecase ordemServicoServiceUsecase) {
        this.ordemServicoServiceUsecase = ordemServicoServiceUsecase;
    }

    @GetMapping("/{id}/acompanhamento")
    public ResponseEntity<AcompanhamentoOrdemServicoResponseDTO> acompanharOrdem(
            @PathVariable UUID id,
            @RequestParam String cpfCnpj) {
        return ResponseEntity.ok(ordemServicoServiceUsecase.consultarAcompanhamento(id, cpfCnpj));
    }
}
