package com.postech.challenge.application.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação de ordem de serviço por CPF/CNPJ do cliente")
public record OrdemServicoCreateByClienteRequestDTO(

        @Schema(description = "CPF ou CNPJ do cliente", example = "12345678909")
        String clienteCpfCnpj,

        @Schema(description = "Placa do veículo", example = "ABC1D23")
        String veiculoPlaca,

        @Schema(description = "Marca do veículo", example = "Toyota")
        String veiculoMarca,

        @Schema(description = "Modelo do veículo", example = "Corolla")
        String veiculoModelo,

        @Schema(description = "Ano do veículo", example = "2022")
        Integer veiculoAno,

        @Schema(description = "Lista de IDs dos serviços solicitados")
        List<UUID> servicosSolicitadosIds,

        @Schema(description = "Lista de IDs dos insumos solicitados")
        List<UUID> insumosSolicitadosIds,

        @Schema(description = "Lista de IDs das peças solicitadas")
        List<UUID> pecasSolicitadasIds
) {
}
