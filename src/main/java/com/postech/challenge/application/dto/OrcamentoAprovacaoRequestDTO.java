package com.postech.challenge.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para aprovação ou rejeição de orçamento")
public record OrcamentoAprovacaoRequestDTO(
        @Schema(description = "Indica se o orçamento foi aprovado", example = "true")
        boolean aprovado
) {
}
