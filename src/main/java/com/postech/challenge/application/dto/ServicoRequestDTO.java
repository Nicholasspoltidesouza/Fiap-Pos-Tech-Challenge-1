package com.postech.challenge.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação ou atualização de serviço")
public record ServicoRequestDTO(

        @Schema(description = "Nome do serviço", example = "Troca de óleo")
        String nome,

        @Schema(description = "Descrição detalhada do serviço", example = "Substituição do óleo e filtro")
        String descricao
) {
}
