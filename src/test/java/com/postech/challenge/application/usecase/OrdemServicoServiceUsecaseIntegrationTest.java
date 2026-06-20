package com.postech.challenge.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.postech.challenge.application.dto.AcompanhamentoOrdemServicoResponseDTO;
import com.postech.challenge.application.dto.OrdemServicoCreateByClienteRequestDTO;
import com.postech.challenge.application.dto.OrdemServicoResponseDTO;
import com.postech.challenge.infrastructure.persistence.entity.ClienteEntity;
import com.postech.challenge.infrastructure.persistence.entity.InsumoEntity;
import com.postech.challenge.infrastructure.persistence.entity.PecaEntity;
import com.postech.challenge.infrastructure.persistence.entity.ServicoEntity;
import com.postech.challenge.infrastructure.persistence.repository.ClienteRepository;
import com.postech.challenge.infrastructure.persistence.repository.InsumoRepository;
import com.postech.challenge.infrastructure.persistence.repository.PecaRepository;
import com.postech.challenge.infrastructure.persistence.repository.ServicoRepository;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
@Transactional
class OrdemServicoServiceUsecaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.sql.init.mode", () -> "never");
    }

    @Autowired
    private OrdemServicoServiceUsecase ordemServicoServiceUsecase;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private PecaRepository pecaRepository;

    @Autowired
    private InsumoRepository insumoRepository;

    @Test
    void shouldCreateOrderByCpfCnpjAndExecuteMainLifecycle() {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setNome("Cliente Fluxo Principal");
        cliente.setCpfCnpj("52998224725");
        ClienteEntity clienteSalvo = clienteRepository.save(cliente);

        ServicoEntity servico = new ServicoEntity();
        servico.setNome("Troca de oleo");
        servico.setDescricao("Troca completa");
        ServicoEntity servicoSalvo = servicoRepository.save(servico);

        PecaEntity peca = new PecaEntity();
        peca.setNome("Filtro de oleo");
        peca.setPrecoUnitario(BigDecimal.valueOf(80));
        peca.setQuantidadeEstoque(2);
        PecaEntity pecaSalva = pecaRepository.save(peca);

        InsumoEntity insumo = new InsumoEntity();
        insumo.setNome("Oleo sintetico");
        insumo.setPrecoUnitario(BigDecimal.valueOf(40));
        insumo.setQuantidadeEstoque(3);
        InsumoEntity insumoSalvo = insumoRepository.save(insumo);

        OrdemServicoCreateByClienteRequestDTO request = new OrdemServicoCreateByClienteRequestDTO(
                clienteSalvo.getCpfCnpj(),
                "ABC1D23",
                "Toyota",
                "Corolla",
                2022,
                List.of(servicoSalvo.getId()),
                List.of(insumoSalvo.getId()),
                List.of(pecaSalva.getId()));

        OrdemServicoResponseDTO ordemCriada = ordemServicoServiceUsecase.createByClienteCpfCnpj(request);
        assertNotNull(ordemCriada.id());
        assertEquals("RECEBIDA", ordemCriada.status());
        assertEquals(BigDecimal.valueOf(230), ordemCriada.valorOrcamento());

        OrdemServicoResponseDTO emDiagnostico = ordemServicoServiceUsecase.iniciarDiagnostico(ordemCriada.id());
        assertEquals("EM_DIAGNOSTICO", emDiagnostico.status());

        OrdemServicoResponseDTO aguardandoAprovacao = ordemServicoServiceUsecase.enviarOrcamento(ordemCriada.id());
        assertEquals("AGUARDANDO_APROVACAO", aguardandoAprovacao.status());

        OrdemServicoResponseDTO emExecucao = ordemServicoServiceUsecase.aprovarOrcamento(ordemCriada.id(), true);
        assertEquals("EM_EXECUCAO", emExecucao.status());

        PecaEntity pecaAtualizada = pecaRepository.findById(pecaSalva.getId()).orElseThrow();
        InsumoEntity insumoAtualizado = insumoRepository.findById(insumoSalvo.getId()).orElseThrow();
        assertEquals(1, pecaAtualizada.getQuantidadeEstoque());
        assertEquals(2, insumoAtualizado.getQuantidadeEstoque());

        OrdemServicoResponseDTO finalizada = ordemServicoServiceUsecase.finalizar(ordemCriada.id());
        assertEquals("FINALIZADA", finalizada.status());

        OrdemServicoResponseDTO entregue = ordemServicoServiceUsecase.entregar(ordemCriada.id());
        assertEquals("ENTREGUE", entregue.status());

        AcompanhamentoOrdemServicoResponseDTO acompanhamento = ordemServicoServiceUsecase.consultarAcompanhamento(
                ordemCriada.id(),
                "529.982.247-25");
        assertEquals("ENTREGUE", acompanhamento.status());
        assertEquals("52998224725", acompanhamento.clienteCpfCnpj());
    }
}
