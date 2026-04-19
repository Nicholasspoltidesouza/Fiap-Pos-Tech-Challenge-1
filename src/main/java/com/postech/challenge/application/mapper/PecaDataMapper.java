package com.postech.challenge.application.mapper;

import org.springframework.stereotype.Component;

import com.postech.challenge.application.dto.PecaRequestDTO;
import com.postech.challenge.application.dto.PecaResponseDTO;
import com.postech.challenge.infrastructure.persistence.entity.PecaEntity;

@Component
public class PecaDataMapper {

    public PecaResponseDTO toResponse(PecaEntity peca) {
        return new PecaResponseDTO(
                peca.getId(),
                peca.getNome(),
                peca.getPrecoUnitario(),
                peca.getQuantidadeEstoque()
        );
    }

    public PecaEntity toEntity(PecaRequestDTO request) {
        PecaEntity peca = new PecaEntity();
        peca.setNome(request.nome());
        peca.setPrecoUnitario(request.precoUnitario());
        peca.setQuantidadeEstoque(request.quantidadeEstoque());
        return peca;
    }

    public void updateEntity(PecaEntity peca, PecaRequestDTO request) {
        peca.setNome(request.nome());
        peca.setPrecoUnitario(request.precoUnitario());
        peca.setQuantidadeEstoque(request.quantidadeEstoque());
    }
}
