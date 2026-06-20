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
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postech.challenge.application.dto.PecaRequestDTO;
import com.postech.challenge.application.dto.PecaResponseDTO;
import com.postech.challenge.application.usecase.PecaServiceUsecase;
import com.postech.challenge.presentation.api.doc.PecaControllerApiDoc;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/pecas")
@Tag(name = "Pecas", description = "Endpoints para gerenciamento de pecas")
public class PecaControllerApi extends PecaControllerApiDoc {

    private final PecaServiceUsecase pecaService;

    public PecaControllerApi(PecaServiceUsecase pecaService) {
        this.pecaService = pecaService;
    }

    @GetMapping
    @Override
    public ResponseEntity<List<PecaResponseDTO>> findAll() {
        return ResponseEntity.ok(pecaService.findAll());
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<PecaResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(pecaService.findById(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<PecaResponseDTO> create(@Valid @RequestBody PecaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pecaService.create(request));
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<PecaResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody PecaRequestDTO request) {
        return ResponseEntity.ok(pecaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        pecaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
