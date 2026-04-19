package com.postech.challenge.application.mapper;

import org.springframework.stereotype.Component;

import com.postech.challenge.application.dto.VeiculoRequestDTO;
import com.postech.challenge.application.dto.VeiculoResponseDTO;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;
import com.postech.challenge.infrastructure.persistence.entity.VeiculoEntity;

@Component
public class VeiculoDataMapper {

    public VeiculoResponseDTO toResponse(VeiculoEntity veiculo) {
        return new VeiculoResponseDTO(
                veiculo.getId(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getPlaca(),
                veiculo.getAno(),
                veiculo.getCliente().getId()
        );
    }

    public VeiculoEntity toEntity(VeiculoRequestDTO request, ClienteEntity cliente) {
        VeiculoEntity veiculo = new VeiculoEntity();
        veiculo.setMarca(request.marca());
        veiculo.setModelo(request.modelo());
        veiculo.setPlaca(request.placa());
        veiculo.setAno(request.ano());
        veiculo.setCliente(cliente);
        return veiculo;
    }

    public void updateEntity(VeiculoEntity veiculo, VeiculoRequestDTO request, ClienteEntity cliente) {
        veiculo.setMarca(request.marca());
        veiculo.setModelo(request.modelo());
        veiculo.setPlaca(request.placa());
        veiculo.setAno(request.ano());
        veiculo.setCliente(cliente);
    }
}
