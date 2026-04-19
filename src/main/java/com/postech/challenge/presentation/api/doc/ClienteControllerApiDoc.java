package com.postech.challenge.presentation.api.doc;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.postech.challenge.application.dto.ClienteRequestDTO;
import com.postech.challenge.application.dto.ClienteResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public abstract class ClienteControllerApiDoc {

    @Operation(summary = "Listar clientes", description = "Retorna todos os clientes cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de clientes retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ClienteResponseDTO.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "id": "11111111-1111-1111-1111-111111111111",
                                        "nome": "Joao Silva",
                                        "cpfCnpj": "123.456.789-00"
                                      }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<List<ClienteResponseDTO>> findAll();

    @Operation(summary = "Buscar cliente por ID", description = "Retorna um cliente com base no ID informado.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cliente encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClienteResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": "11111111-1111-1111-1111-111111111111",
                                      "nome": "Joao Silva",
                                      "cpfCnpj": "123.456.789-00"
                                    }
                                    """))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente nao encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Cliente not found: 11111111-1111-1111-1111-111111111999",
                                      "path": "/api/clientes/11111111-1111-1111-1111-111111111999",
                                      "timestamp": "2026-04-19T18:10:00Z"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<ClienteResponseDTO> findById(
            @Parameter(description = "ID do cliente", required = true) UUID id);

    @Operation(summary = "Criar cliente", description = "Cria um novo cliente.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Cliente criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClienteResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": "11111111-1111-1111-1111-111111111120",
                                      "nome": "Novo Cliente",
                                      "cpfCnpj": "999.888.777-66"
                                    }
                                    """))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados invalidos ou CPF/CNPJ ja cadastrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 400,
                                      "error": "Bad Request",
                                      "message": "CPF/CNPJ already registered: 123.456.789-00",
                                      "path": "/api/clientes",
                                      "timestamp": "2026-04-19T18:12:00Z"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<ClienteResponseDTO> create(
            @RequestBody(
                    description = "Dados para criacao de cliente",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClienteRequestDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "nome": "Novo Cliente",
                                      "cpfCnpj": "999.888.777-66"
                                    }
                                    """)))
            ClienteRequestDTO request);

    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cliente atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClienteResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": "11111111-1111-1111-1111-111111111111",
                                      "nome": "Joao Silva Atualizado",
                                      "cpfCnpj": "123.456.789-00"
                                    }
                                    """))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados invalidos ou CPF/CNPJ ja cadastrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente nao encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<ClienteResponseDTO> update(
            @Parameter(description = "ID do cliente", required = true) UUID id,
            @RequestBody(
                    description = "Dados para atualizacao de cliente",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClienteRequestDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "nome": "Joao Silva Atualizado",
                                      "cpfCnpj": "123.456.789-00"
                                    }
                                    """)))
            ClienteRequestDTO request);

    @Operation(summary = "Excluir cliente", description = "Exclui um cliente pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente excluido com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente nao encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<Void> delete(
            @Parameter(description = "ID do cliente", required = true) UUID id);
}
