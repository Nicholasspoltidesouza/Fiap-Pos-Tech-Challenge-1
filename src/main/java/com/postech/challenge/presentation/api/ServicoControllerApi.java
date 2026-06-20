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

import com.postech.challenge.application.dto.ServicoRequestDTO;
import com.postech.challenge.application.dto.ServicoResponseDTO;
import com.postech.challenge.application.usecase.ServicoServiceUsecase;
import com.postech.challenge.presentation.api.doc.ServicoControllerApiDoc;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/servicos")
@Tag(name = "Servicos", description = "Endpoints para gerenciamento de servicos")
public class ServicoControllerApi extends ServicoControllerApiDoc {

    private final ServicoServiceUsecase servicoService;

    public ServicoControllerApi(ServicoServiceUsecase servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping
    @Override
    public ResponseEntity<List<ServicoResponseDTO>> findAll() {
        return ResponseEntity.ok(servicoService.findAll());
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ServicoResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(servicoService.findById(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<ServicoResponseDTO> create(@Valid @RequestBody ServicoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoService.create(request));
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ServicoResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody ServicoRequestDTO request) {
        return ResponseEntity.ok(servicoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        servicoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
