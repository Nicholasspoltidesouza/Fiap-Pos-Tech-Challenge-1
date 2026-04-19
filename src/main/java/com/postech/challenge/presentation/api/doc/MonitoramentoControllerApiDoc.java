package com.postech.challenge.presentation.api.doc;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.postech.challenge.application.dto.TempoMedioServicoResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public abstract class MonitoramentoControllerApiDoc {

    @Operation(
            summary = "Tempo medio por servico",
            description = "Retorna o tempo medio de execucao, em minutos, para cada servico com ordens concluidas.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dados de monitoramento retornados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TempoMedioServicoResponseDTO.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "servicoId": "33333333-3333-3333-3333-333333333331",
                                        "servicoNome": "Troca de oleo",
                                        "quantidadeOrdensConcluidas": 12,
                                        "tempoMedioMinutos": 95.5
                                      }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissao para acessar o recurso")
    })
    public abstract ResponseEntity<List<TempoMedioServicoResponseDTO>> findTempoMedioExecucaoPorServico();
}
