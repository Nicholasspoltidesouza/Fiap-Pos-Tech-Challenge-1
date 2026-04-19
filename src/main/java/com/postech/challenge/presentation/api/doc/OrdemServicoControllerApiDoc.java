package com.postech.challenge.presentation.api.doc;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.postech.challenge.application.dto.OrdemServicoRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public abstract class OrdemServicoControllerApiDoc {

    @Operation(summary = "Listar ordens de servico", description = "Retorna todas as ordens de servico cadastradas.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de ordens retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrdemServicoResponseDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<List<OrdemServicoResponseDTO>> findAll();

    @Operation(summary = "Buscar ordem de servico por ID", description = "Retorna uma ordem de servico com base no ID informado.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ordem encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrdemServicoResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ordem nao encontrada",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<OrdemServicoResponseDTO> findById(
            @Parameter(description = "ID da ordem de servico", required = true) UUID id);

    @Operation(summary = "Criar ordem de servico", description = "Cria uma nova ordem de servico.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Ordem criada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrdemServicoResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "clienteId": "11111111-1111-1111-1111-111111111111",
                                      "veiculoId": "22222222-2222-2222-2222-222222222221",
                                      "status": "ABERTA",
                                      "dataAbertura": "2026-04-19T10:30:00",
                                      "dataFinalizacao": null,
                                      "servicosSolicitadosIds": ["33333333-3333-3333-3333-333333333331"],
                                      "insumosSolicitadosIds": ["66666666-6666-6666-6666-666666666661"]
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente, veiculo, servico ou insumo nao encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<OrdemServicoResponseDTO> create(
            @RequestBody(
                    description = "Dados para criacao de ordem de servico",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrdemServicoRequestDTO.class)))
            OrdemServicoRequestDTO request);

    @Operation(summary = "Atualizar ordem de servico", description = "Atualiza os dados de uma ordem de servico existente.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ordem atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrdemServicoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ordem, cliente, veiculo, servico ou insumo nao encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<OrdemServicoResponseDTO> update(
            @Parameter(description = "ID da ordem de servico", required = true) UUID id,
            @RequestBody(
                    description = "Dados para atualizacao de ordem de servico",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrdemServicoRequestDTO.class)))
            OrdemServicoRequestDTO request);

    @Operation(summary = "Excluir ordem de servico", description = "Exclui uma ordem de servico pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ordem excluida com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ordem nao encontrada",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<Void> delete(
            @Parameter(description = "ID da ordem de servico", required = true) UUID id);
}
