package com.postech.challenge.application.mapper;

import org.springframework.stereotype.Component;

import com.postech.challenge.application.dto.ServicoRequestDTO;
import com.postech.challenge.application.dto.ServicoResponseDTO;
import com.postech.challenge.infrastructure.persistence.entity.ServicoEntity;

@Component
public class ServicoDataMapper {

    public ServicoResponseDTO toResponse(ServicoEntity servico) {
        return new ServicoResponseDTO(
                servico.getId(),
                servico.getNome(),
                servico.getDescricao()
        );
    }

    public ServicoEntity toEntity(ServicoRequestDTO request) {
        ServicoEntity servico = new ServicoEntity();
        servico.setNome(request.nome());
        servico.setDescricao(request.descricao());
        return servico;
    }

    public void updateEntity(ServicoEntity servico, ServicoRequestDTO request) {
        servico.setNome(request.nome());
        servico.setDescricao(request.descricao());
    }
}
