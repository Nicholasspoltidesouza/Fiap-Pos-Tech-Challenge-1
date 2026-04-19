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

import com.postech.challenge.application.dto.VeiculoRequestDTO;
import com.postech.challenge.application.dto.VeiculoResponseDTO;
import com.postech.challenge.application.usecase.VeiculoServiceUsecase;
import com.postech.challenge.presentation.api.doc.VeiculoControllerApiDoc;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/veiculos")
@Tag(name = "Veiculos", description = "Endpoints para gerenciamento de veiculos")
public class VeiculoControllerApi extends VeiculoControllerApiDoc {

    private final VeiculoServiceUsecase veiculoService;

    public VeiculoControllerApi(VeiculoServiceUsecase veiculoService) {
        this.veiculoService = veiculoService;
    }

    @GetMapping
    @Override
    public ResponseEntity<List<VeiculoResponseDTO>> findAll() {
        return ResponseEntity.ok(veiculoService.findAll());
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<VeiculoResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(veiculoService.findById(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<VeiculoResponseDTO> create(@RequestBody VeiculoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(veiculoService.create(request));
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<VeiculoResponseDTO> update(@PathVariable UUID id, @RequestBody VeiculoRequestDTO request) {
        return ResponseEntity.ok(veiculoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        veiculoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
