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

import com.postech.challenge.application.dto.InsumoRequestDTO;
import com.postech.challenge.application.dto.InsumoResponseDTO;
import com.postech.challenge.application.usecase.InsumoServiceUsecase;
import com.postech.challenge.presentation.api.doc.InsumoControllerApiDoc;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/insumos")
@Tag(name = "Insumos", description = "Endpoints para gerenciamento de insumos")
public class InsumoControllerApi extends InsumoControllerApiDoc {

    private final InsumoServiceUsecase insumoService;

    public InsumoControllerApi(InsumoServiceUsecase insumoService) {
        this.insumoService = insumoService;
    }

    @GetMapping
    @Override
    public ResponseEntity<List<InsumoResponseDTO>> findAll() {
        return ResponseEntity.ok(insumoService.findAll());
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<InsumoResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(insumoService.findById(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<InsumoResponseDTO> create(@RequestBody InsumoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(insumoService.create(request));
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<InsumoResponseDTO> update(@PathVariable UUID id, @RequestBody InsumoRequestDTO request) {
        return ResponseEntity.ok(insumoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        insumoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
