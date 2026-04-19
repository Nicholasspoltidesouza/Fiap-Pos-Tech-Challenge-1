package com.postech.challenge.presentation.api.doc;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.postech.challenge.application.dto.ServicoRequestDTO;
import com.postech.challenge.application.dto.ServicoResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public abstract class ServicoControllerApiDoc {

    @Operation(summary = "Listar serviços", description = "Retorna todos os serviços cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de serviços retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ServicoResponseDTO.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "id": "33333333-3333-3333-3333-333333333331",
                                        "nome": "Troca de oleo",
                                        "descricao": "Substituicao do oleo do motor e filtro"
                                      }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<List<ServicoResponseDTO>> findAll();

    @Operation(summary = "Buscar serviço por ID", description = "Retorna um serviço com base no ID informado.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Serviço encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServicoResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": "33333333-3333-3333-3333-333333333331",
                                      "nome": "Troca de oleo",
                                      "descricao": "Substituicao do oleo do motor e filtro"
                                    }
                                    """))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Serviço nao encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Servico not found: 33333333-3333-3333-3333-333333333399",
                                      "path": "/api/servicos/33333333-3333-3333-3333-333333333399",
                                      "timestamp": "2026-04-19T18:10:00Z"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<ServicoResponseDTO> findById(
            @Parameter(description = "ID do serviço", required = true) UUID id);

    @Operation(summary = "Criar serviço", description = "Cria um novo serviço.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Serviço criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServicoResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": "33333333-3333-3333-3333-333333333350",
                                      "nome": "Revisao eletrica",
                                      "descricao": "Verificacao do sistema eletrico geral"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<ServicoResponseDTO> create(
            @RequestBody(
                    description = "Dados para criacao de serviço",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServicoRequestDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "nome": "Revisao eletrica",
                                      "descricao": "Verificacao do sistema eletrico geral"
                                    }
                                    """)))
            ServicoRequestDTO request);

    @Operation(summary = "Atualizar serviço", description = "Atualiza os dados de um serviço existente.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Serviço atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServicoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Serviço nao encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<ServicoResponseDTO> update(
            @Parameter(description = "ID do serviço", required = true) UUID id,
            @RequestBody(
                    description = "Dados para atualizacao de serviço",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServicoRequestDTO.class)))
            ServicoRequestDTO request);

    @Operation(summary = "Excluir serviço", description = "Exclui um serviço pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço excluido com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Serviço nao encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<Void> delete(
            @Parameter(description = "ID do serviço", required = true) UUID id);
}
