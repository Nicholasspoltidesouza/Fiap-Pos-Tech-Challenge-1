package com.postech.challenge.application.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação ou atualização de peça")
public record PecaRequestDTO(

        @Schema(description = "Nome da peça", example = "Filtro de óleo")
        String nome,

        @Schema(description = "Preço unitário da peça", example = "59.90")
        BigDecimal precoUnitario,

        @Schema(description = "Quantidade disponível em estoque", example = "25")
        Integer quantidadeEstoque
) {
}
