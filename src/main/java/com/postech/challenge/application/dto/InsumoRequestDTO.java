package com.postech.challenge.application.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação ou atualização de insumo")
public record InsumoRequestDTO(

        @Schema(description = "Nome do insumo", example = "Óleo 5W30 1L")
        String nome,

        @Schema(description = "Preço unitário do insumo", example = "45.90")
        BigDecimal precoUnitario,

        @Schema(description = "Quantidade disponível em estoque", example = "120")
        Integer quantidadeEstoque
) {
}
