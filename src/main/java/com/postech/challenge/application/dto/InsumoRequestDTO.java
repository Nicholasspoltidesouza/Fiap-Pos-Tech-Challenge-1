package com.postech.challenge.application.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Dados para criação ou atualização de insumo")
public record InsumoRequestDTO(

        @NotBlank(message = "nome is required")
        @Schema(description = "Nome do insumo", example = "Óleo 5W30 1L")
        String nome,

        @NotNull(message = "precoUnitario is required")
        @PositiveOrZero(message = "precoUnitario must be zero or positive")
        @Schema(description = "Preço unitário do insumo", example = "45.90")
        BigDecimal precoUnitario,

        @NotNull(message = "quantidadeEstoque is required")
        @PositiveOrZero(message = "quantidadeEstoque must be zero or positive")
        @Schema(description = "Quantidade disponível em estoque", example = "120")
        Integer quantidadeEstoque
) {
}
