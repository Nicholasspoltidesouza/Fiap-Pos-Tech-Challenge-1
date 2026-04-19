package com.postech.challenge.presentation.api.doc;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.postech.challenge.application.dto.PecaRequestDTO;
import com.postech.challenge.application.dto.PecaResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public abstract class PecaControllerApiDoc {

    @Operation(summary = "Listar peças", description = "Retorna todas as peças cadastradas.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de peças retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PecaResponseDTO.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "id": "55555555-5555-5555-5555-555555555551",
                                        "nome": "Filtro de óleo",
                                        "precoUnitario": 59.90,
                                        "quantidadeEstoque": 25
                                      }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<List<PecaResponseDTO>> findAll();

    @Operation(summary = "Buscar peça por ID", description = "Retorna uma peça com base no ID informado.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Peça encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PecaResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": "55555555-5555-5555-5555-555555555551",
                                      "nome": "Filtro de óleo",
                                      "precoUnitario": 59.90,
                                      "quantidadeEstoque": 25
                                    }
                                    """))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Peça nao encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Peca not found: 55555555-5555-5555-5555-555555555599",
                                      "path": "/api/pecas/55555555-5555-5555-5555-555555555599",
                                      "timestamp": "2026-04-19T18:10:00Z"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<PecaResponseDTO> findById(
            @Parameter(description = "ID da peça", required = true) UUID id);

    @Operation(summary = "Criar peça", description = "Cria uma nova peça.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Peça criada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PecaResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": "55555555-5555-5555-5555-555555555560",
                                      "nome": "Pastilha de freio",
                                      "precoUnitario": 149.90,
                                      "quantidadeEstoque": 30
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<PecaResponseDTO> create(
            @RequestBody(
                    description = "Dados para criacao de peça",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PecaRequestDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "nome": "Pastilha de freio",
                                      "precoUnitario": 149.90,
                                      "quantidadeEstoque": 30
                                    }
                                    """)))
            PecaRequestDTO request);

    @Operation(summary = "Atualizar peça", description = "Atualiza os dados de uma peça existente.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Peça atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PecaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Peça nao encontrada",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<PecaResponseDTO> update(
            @Parameter(description = "ID da peça", required = true) UUID id,
            @RequestBody(
                    description = "Dados para atualizacao de peça",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PecaRequestDTO.class)))
            PecaRequestDTO request);

    @Operation(summary = "Excluir peça", description = "Exclui uma peça pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Peça excluida com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Peça nao encontrada",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<Void> delete(
            @Parameter(description = "ID da peça", required = true) UUID id);
}
