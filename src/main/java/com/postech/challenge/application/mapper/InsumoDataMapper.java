package com.postech.challenge.application.mapper;

import org.springframework.stereotype.Component;

import com.postech.challenge.application.dto.InsumoRequestDTO;
import com.postech.challenge.application.dto.InsumoResponseDTO;
import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;

@Component
public class InsumoDataMapper {

    public InsumoResponseDTO toResponse(InsumoEntity insumo) {
        return new InsumoResponseDTO(
                insumo.getId(),
                insumo.getNome(),
                insumo.getPrecoUnitario(),
                insumo.getQuantidadeEstoque()
        );
    }

    public InsumoEntity toEntity(InsumoRequestDTO request) {
        InsumoEntity insumo = new InsumoEntity();
        insumo.setNome(request.nome());
        insumo.setPrecoUnitario(request.precoUnitario());
        insumo.setQuantidadeEstoque(request.quantidadeEstoque());
        return insumo;
    }

    public void updateEntity(InsumoEntity insumo, InsumoRequestDTO request) {
        insumo.setNome(request.nome());
        insumo.setPrecoUnitario(request.precoUnitario());
        insumo.setQuantidadeEstoque(request.quantidadeEstoque());
    }
}
