package com.postech.challenge.presentation.api.doc;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.postech.challenge.application.dto.VeiculoRequestDTO;
import com.postech.challenge.application.dto.VeiculoResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public abstract class VeiculoControllerApiDoc {

    @Operation(summary = "Listar veiculos", description = "Retorna todos os veiculos cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de veiculos retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VeiculoResponseDTO.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "id": "22222222-2222-2222-2222-222222222221",
                                        "marca": "Toyota",
                                        "modelo": "Corolla",
                                        "placa": "ABC1D23",
                                        "ano": 2020,
                                        "clienteId": "11111111-1111-1111-1111-111111111111"
                                      }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<List<VeiculoResponseDTO>> findAll();

    @Operation(summary = "Buscar veiculo por ID", description = "Retorna um veiculo com base no ID informado.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Veiculo encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VeiculoResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veiculo nao encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<VeiculoResponseDTO> findById(
            @Parameter(description = "ID do veiculo", required = true) UUID id);

    @Operation(summary = "Criar veiculo", description = "Cria um novo veiculo.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Veiculo criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VeiculoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente nao encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<VeiculoResponseDTO> create(
            @RequestBody(
                    description = "Dados para criacao de veiculo",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VeiculoRequestDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "marca": "Honda",
                                      "modelo": "Civic",
                                      "placa": "DEF2G34",
                                      "ano": 2019,
                                      "clienteId": "11111111-1111-1111-1111-111111111111"
                                    }
                                    """)))
            VeiculoRequestDTO request);

    @Operation(summary = "Atualizar veiculo", description = "Atualiza os dados de um veiculo existente.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Veiculo atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VeiculoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veiculo ou cliente nao encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<VeiculoResponseDTO> update(
            @Parameter(description = "ID do veiculo", required = true) UUID id,
            @RequestBody(
                    description = "Dados para atualizacao de veiculo",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VeiculoRequestDTO.class)))
            VeiculoRequestDTO request);

    @Operation(summary = "Excluir veiculo", description = "Exclui um veiculo pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Veiculo excluido com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veiculo nao encontrado",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<Void> delete(
            @Parameter(description = "ID do veiculo", required = true) UUID id);
}
