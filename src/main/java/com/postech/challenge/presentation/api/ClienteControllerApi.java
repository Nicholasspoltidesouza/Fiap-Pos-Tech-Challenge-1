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

import com.postech.challenge.application.dto.ClienteRequestDTO;
import com.postech.challenge.application.dto.ClienteResponseDTO;
import com.postech.challenge.application.usecase.ClienteServiceUsecase;
import com.postech.challenge.presentation.api.doc.ClienteControllerApiDoc;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes")
public class ClienteControllerApi extends ClienteControllerApiDoc {


    private final ClienteServiceUsecase clienteService;

    public ClienteControllerApi(ClienteServiceUsecase clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    @Override
    public ResponseEntity<List<ClienteResponseDTO>> findAll() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ClienteResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<ClienteResponseDTO> create(@RequestBody ClienteRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.create(request));
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ClienteResponseDTO> update(@PathVariable UUID id, @RequestBody ClienteRequestDTO request) {
        return ResponseEntity.ok(clienteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
