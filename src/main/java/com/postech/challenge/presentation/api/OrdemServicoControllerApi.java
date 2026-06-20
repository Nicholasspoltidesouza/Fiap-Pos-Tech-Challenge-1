package com.postech.challenge.presentation.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postech.challenge.application.dto.AtualizarPecasOrdemServicoRequestDTO;
import com.postech.challenge.application.dto.OrcamentoAprovacaoRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoCreateByClienteRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoResponseDTO;
import com.postech.challenge.application.usecase.OrdemServicoServiceUsecase;
import com.postech.challenge.presentation.api.doc.OrdemServicoControllerApiDoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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

    @GetMapping("/ativas")
    @Operation(
            summary = "Listar ordens de servico ativas",
            description = "Lista as ordens de servico nao encerradas, ordenadas por status "
                    + "(Em Execucao > Aguardando Aprovacao > Em Diagnostico > Recebida) e mais antigas primeiro. "
                    + "Exclui logicamente as ordens Finalizadas e Entregues.")
    public ResponseEntity<List<OrdemServicoResponseDTO>> listarAtivas() {
        return ResponseEntity.ok(ordemServicoService.listarAtivas());
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<OrdemServicoResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ordemServicoService.findById(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<OrdemServicoResponseDTO> create(@Valid @RequestBody OrdemServicoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordemServicoService.create(request));
    }

    @PostMapping("/cliente")
    public ResponseEntity<OrdemServicoResponseDTO> createByClienteCpfCnpj(
            @Valid @RequestBody OrdemServicoCreateByClienteRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordemServicoService.createByClienteCpfCnpj(request));
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<OrdemServicoResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody OrdemServicoRequestDTO request) {
        return ResponseEntity.ok(ordemServicoService.update(id, request));
    }

    @PatchMapping("/{id}/pecas")
    public ResponseEntity<OrdemServicoResponseDTO> adicionarPecas(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarPecasOrdemServicoRequestDTO request) {
        return ResponseEntity.ok(ordemServicoService.adicionarPecas(id, request.pecasIds()));
    }

    @PatchMapping("/{id}/diagnostico/iniciar")
    public ResponseEntity<OrdemServicoResponseDTO> iniciarDiagnostico(@PathVariable UUID id) {
        return ResponseEntity.ok(ordemServicoService.iniciarDiagnostico(id));
    }

    @PatchMapping("/{id}/orcamento/enviar")
    public ResponseEntity<OrdemServicoResponseDTO> enviarOrcamento(@PathVariable UUID id) {
        return ResponseEntity.ok(ordemServicoService.enviarOrcamento(id));
    }

    @PatchMapping("/{id}/orcamento/aprovacao")
    public ResponseEntity<OrdemServicoResponseDTO> aprovarOrcamento(
            @PathVariable UUID id,
            @RequestBody OrcamentoAprovacaoRequestDTO request) {
        return ResponseEntity.ok(ordemServicoService.aprovarOrcamento(id, request.aprovado()));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<OrdemServicoResponseDTO> finalizar(@PathVariable UUID id) {
        return ResponseEntity.ok(ordemServicoService.finalizar(id));
    }

    @PatchMapping("/{id}/entregar")
    public ResponseEntity<OrdemServicoResponseDTO> entregar(@PathVariable UUID id) {
        return ResponseEntity.ok(ordemServicoService.entregar(id));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ordemServicoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
