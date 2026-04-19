package com.postech.challenge.presentation.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postech.challenge.application.dto.OrdemServicoRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoResponseDTO;
import com.postech.challenge.application.usecase.OrdemServicoServiceUsecase;
import com.postech.challenge.presentation.api.doc.OrdemServicoControllerApiDoc;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/ordens-servico")
@Tag(name = "Ordens de Servico", description = "Endpoints para gerenciamento de ordens de servico")
public class OrdemServicoControllerApi extends OrdemServicoControllerApiDoc {

    private final OrdemServicoServiceUsecase ordemServicoService;

    public OrdemServicoControllerApi(OrdemServicoServiceUsecase ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @GetMapping
    @Override
    public ResponseEntity<List<OrdemServicoResponseDTO>> findAll() {
        return ResponseEntity.ok(ordemServicoService.findAll());
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<OrdemServicoResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ordemServicoService.findById(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<OrdemServicoResponseDTO> create(@RequestBody OrdemServicoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordemServicoService.create(request));
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<OrdemServicoResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody OrdemServicoRequestDTO request) {
        return ResponseEntity.ok(ordemServicoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ordemServicoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
