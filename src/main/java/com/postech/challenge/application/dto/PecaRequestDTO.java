package com.postech.challenge.application.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Dados para criação ou atualização de peça")
public record PecaRequestDTO(

        @NotBlank(message = "nome is required")
        @Schema(description = "Nome da peça", example = "Filtro de óleo")
        String nome,

        @NotNull(message = "precoUnitario is required")
        @PositiveOrZero(message = "precoUnitario must be zero or positive")
        @Schema(description = "Preço unitário da peça", example = "59.90")
        BigDecimal precoUnitario,

        @NotNull(message = "quantidadeEstoque is required")
        @PositiveOrZero(message = "quantidadeEstoque must be zero or positive")
        @Schema(description = "Quantidade disponível em estoque", example = "25")
        Integer quantidadeEstoque
) {
}
