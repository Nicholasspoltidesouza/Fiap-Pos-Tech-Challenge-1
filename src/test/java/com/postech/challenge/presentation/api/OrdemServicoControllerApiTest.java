package com.postech.challenge.presentation.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.postech.challenge.application.dto.AtualizarPecasOrdemServicoRequestDTO;
import com.postech.challenge.application.dto.OrcamentoAprovacaoRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoCreateByClienteRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoResponseDTO;
import com.postech.challenge.application.usecase.OrdemServicoServiceUsecase;

@ExtendWith(MockitoExtension.class)
class OrdemServicoControllerApiTest {

    @Mock
    private OrdemServicoServiceUsecase ordemServicoServiceUsecase;

    @Test
    void shouldFindAll() {
        OrdemServicoControllerApi controller = new OrdemServicoControllerApi(ordemServicoServiceUsecase);
        List<OrdemServicoResponseDTO> ordens = List.of(buildOrdemServicoResponse());
        when(ordemServicoServiceUsecase.findAll()).thenReturn(ordens);

        ResponseEntity<List<OrdemServicoResponseDTO>> response = controller.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ordens, response.getBody());
    }

    @Test
    void shouldFindById() {
        OrdemServicoControllerApi controller = new OrdemServicoControllerApi(ordemServicoServiceUsecase);
        UUID id = UUID.randomUUID();
        OrdemServicoResponseDTO dto = buildOrdemServicoResponse();
        when(ordemServicoServiceUsecase.findById(id)).thenReturn(dto);

        ResponseEntity<OrdemServicoResponseDTO> response = controller.findById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void shouldCreate() {
        OrdemServicoControllerApi controller = new OrdemServicoControllerApi(ordemServicoServiceUsecase);
        OrdemServicoRequestDTO request = new OrdemServicoRequestDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "RECEBIDA",
                LocalDateTime.now(),
                null,
                List.of(UUID.randomUUID()),
                List.of(UUID.randomUUID()),
                List.of(UUID.randomUUID()));
        OrdemServicoResponseDTO dto = buildOrdemServicoResponse();
        when(ordemServicoServiceUsecase.create(request)).thenReturn(dto);

        ResponseEntity<OrdemServicoResponseDTO> response = controller.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void shouldCreateByClienteCpfCnpj() {
        OrdemServicoControllerApi controller = new OrdemServicoControllerApi(ordemServicoServiceUsecase);
        OrdemServicoCreateByClienteRequestDTO request = new OrdemServicoCreateByClienteRequestDTO(
                "52998224725",
                "ABC1D23",
                "Toyota",
                "Corolla",
                2022,
                List.of(UUID.randomUUID()),
                List.of(UUID.randomUUID()),
                List.of(UUID.randomUUID()));
        OrdemServicoResponseDTO dto = buildOrdemServicoResponse();
        when(ordemServicoServiceUsecase.createByClienteCpfCnpj(request)).thenReturn(dto);

        ResponseEntity<OrdemServicoResponseDTO> response = controller.createByClienteCpfCnpj(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void shouldUpdate() {
        OrdemServicoControllerApi controller = new OrdemServicoControllerApi(ordemServicoServiceUsecase);
        UUID id = UUID.randomUUID();
        OrdemServicoRequestDTO request = new OrdemServicoRequestDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "EM_EXECUCAO",
                LocalDateTime.now(),
                null,
                List.of(UUID.randomUUID()),
                List.of(UUID.randomUUID()),
                List.of(UUID.randomUUID()));
        OrdemServicoResponseDTO dto = buildOrdemServicoResponse();
        when(ordemServicoServiceUsecase.update(id, request)).thenReturn(dto);

        ResponseEntity<OrdemServicoResponseDTO> response = controller.update(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void shouldAdicionarPecas() {
        OrdemServicoControllerApi controller = new OrdemServicoControllerApi(ordemServicoServiceUsecase);
        UUID id = UUID.randomUUID();
        List<UUID> pecasIds = List.of(UUID.randomUUID());
        AtualizarPecasOrdemServicoRequestDTO request = new AtualizarPecasOrdemServicoRequestDTO(pecasIds);
        OrdemServicoResponseDTO dto = buildOrdemServicoResponse();
        when(ordemServicoServiceUsecase.adicionarPecas(id, pecasIds)).thenReturn(dto);

        ResponseEntity<OrdemServicoResponseDTO> response = controller.adicionarPecas(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void shouldIniciarDiagnostico() {
        OrdemServicoControllerApi controller = new OrdemServicoControllerApi(ordemServicoServiceUsecase);
        UUID id = UUID.randomUUID();
        OrdemServicoResponseDTO dto = buildOrdemServicoResponse();
        when(ordemServicoServiceUsecase.iniciarDiagnostico(id)).thenReturn(dto);

        ResponseEntity<OrdemServicoResponseDTO> response = controller.iniciarDiagnostico(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void shouldEnviarOrcamento() {
        OrdemServicoControllerApi controller = new OrdemServicoControllerApi(ordemServicoServiceUsecase);
        UUID id = UUID.randomUUID();
        OrdemServicoResponseDTO dto = buildOrdemServicoResponse();
        when(ordemServicoServiceUsecase.enviarOrcamento(id)).thenReturn(dto);

        ResponseEntity<OrdemServicoResponseDTO> response = controller.enviarOrcamento(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void shouldAprovarOrcamento() {
        OrdemServicoControllerApi controller = new OrdemServicoControllerApi(ordemServicoServiceUsecase);
        UUID id = UUID.randomUUID();
        OrcamentoAprovacaoRequestDTO request = new OrcamentoAprovacaoRequestDTO(true);
        OrdemServicoResponseDTO dto = buildOrdemServicoResponse();
        when(ordemServicoServiceUsecase.aprovarOrcamento(id, true)).thenReturn(dto);

        ResponseEntity<OrdemServicoResponseDTO> response = controller.aprovarOrcamento(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void shouldFinalizar() {
        OrdemServicoControllerApi controller = new OrdemServicoControllerApi(ordemServicoServiceUsecase);
        UUID id = UUID.randomUUID();
        OrdemServicoResponseDTO dto = buildOrdemServicoResponse();
        when(ordemServicoServiceUsecase.finalizar(id)).thenReturn(dto);

        ResponseEntity<OrdemServicoResponseDTO> response = controller.finalizar(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void shouldEntregar() {
        OrdemServicoControllerApi controller = new OrdemServicoControllerApi(ordemServicoServiceUsecase);
        UUID id = UUID.randomUUID();
        OrdemServicoResponseDTO dto = buildOrdemServicoResponse();
        when(ordemServicoServiceUsecase.entregar(id)).thenReturn(dto);

        ResponseEntity<OrdemServicoResponseDTO> response = controller.entregar(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void shouldDelete() {
        OrdemServicoControllerApi controller = new OrdemServicoControllerApi(ordemServicoServiceUsecase);
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = controller.delete(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(ordemServicoServiceUsecase).delete(id);
    }

    private OrdemServicoResponseDTO buildOrdemServicoResponse() {
        return new OrdemServicoResponseDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "RECEBIDA",
                LocalDateTime.now(),
                null,
                BigDecimal.valueOf(250),
                null,
                null,
                List.of(UUID.randomUUID()),
                List.of(UUID.randomUUID()),
                List.of(UUID.randomUUID()));
    }
}
