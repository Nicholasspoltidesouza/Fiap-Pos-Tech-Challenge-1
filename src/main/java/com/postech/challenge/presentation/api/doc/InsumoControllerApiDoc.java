package com.postech.challenge.presentation.api.doc;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.postech.challenge.application.dto.InsumoRequestDTO;
import com.postech.challenge.application.dto.InsumoResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public abstract class InsumoControllerApiDoc {

    @Operation(summary = "Listar insumos", description = "Retorna todos os insumos cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de insumos retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = InsumoResponseDTO.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "id": "66666666-6666-6666-6666-666666666661",
                                        "nome": "Oleo 5W30 1L",
                                        "precoUnitario": 45.90,
                                        "quantidadeEstoque": 120
                                      }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<List<InsumoResponseDTO>> findAll();

    @Operation(summary = "Buscar insumo por ID", description = "Retorna um insumo com base no ID informado.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Insumo encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InsumoResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": "66666666-6666-6666-6666-666666666661",
                                      "nome": "Oleo 5W30 1L",
                                      "precoUnitario": 45.90,
                                      "quantidadeEstoque": 120
                                    }
                                    """))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Insumo nao encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Insumo not found: 66666666-6666-6666-6666-666666666699",
                                      "path": "/api/insumos/66666666-6666-6666-6666-666666666699",
                                      "timestamp": "2026-04-19T18:10:00Z"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<InsumoResponseDTO> findById(
            @Parameter(description = "ID do insumo", required = true) UUID id);

    @Operation(summary = "Criar insumo", description = "Cria um novo insumo.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Insumo criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InsumoResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": "66666666-6666-6666-6666-666666666667",
                                      "nome": "Lubrificante spray",
                                      "precoUnitario": 19.90,
                                      "quantidadeEstoque": 45
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<InsumoResponseDTO> create(
            @RequestBody(
                    description = "Dados para criacao de insumo",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InsumoRequestDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "nome": "Lubrificante spray",
                                      "precoUnitario": 19.90,
                                      "quantidadeEstoque": 45
                                    }
                                    """)))
            InsumoRequestDTO request);

    @Operation(summary = "Atualizar insumo", description = "Atualiza os dados de um insumo existente.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Insumo atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InsumoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Insumo nao encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<InsumoResponseDTO> update(
            @Parameter(description = "ID do insumo", required = true) UUID id,
            @RequestBody(
                    description = "Dados para atualizacao de insumo",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InsumoRequestDTO.class)))
            InsumoRequestDTO request);

    @Operation(summary = "Excluir insumo", description = "Exclui um insumo pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Insumo excluido com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Insumo nao encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<Void> delete(
            @Parameter(description = "ID do insumo", required = true) UUID id);
}
