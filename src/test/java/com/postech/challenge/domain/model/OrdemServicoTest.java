package com.postech.challenge.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.postech.challenge.domain.exception.TransicaoStatusInvalidaException;

class OrdemServicoTest {

    @Test
    void shouldExecuteFullLifecycle() {
        OrdemServico ordem = novaOrdem(StatusOrdemServico.RECEBIDA);

        ordem.iniciarDiagnostico();
        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, ordem.getStatus());

        ordem.enviarOrcamento();
        assertEquals(StatusOrdemServico.AGUARDANDO_APROVACAO, ordem.getStatus());
        assertNotNull(ordem.getDataEnvioOrcamento());
        assertEquals(BigDecimal.valueOf(230), ordem.getValorOrcamento());

        ordem.aprovarOrcamento(true);
        assertEquals(StatusOrdemServico.EM_EXECUCAO, ordem.getStatus());
        assertEquals(Boolean.TRUE, ordem.getOrcamentoAprovado());

        ordem.finalizar();
        assertEquals(StatusOrdemServico.FINALIZADA, ordem.getStatus());
        assertNotNull(ordem.getDataFinalizacao());

        ordem.entregar();
        assertEquals(StatusOrdemServico.ENTREGUE, ordem.getStatus());
    }

    @Test
    void shouldReturnToRecebidaWhenOrcamentoRejected() {
        OrdemServico ordem = novaOrdem(StatusOrdemServico.AGUARDANDO_APROVACAO);

        ordem.aprovarOrcamento(false);

        assertEquals(StatusOrdemServico.RECEBIDA, ordem.getStatus());
        assertEquals(Boolean.FALSE, ordem.getOrcamentoAprovado());
    }

    @Test
    void shouldThrowWhenIniciarDiagnosticoFromInvalidStatus() {
        OrdemServico ordem = novaOrdem(StatusOrdemServico.EM_EXECUCAO);

        TransicaoStatusInvalidaException exception = assertThrows(
                TransicaoStatusInvalidaException.class,
                ordem::iniciarDiagnostico);

        assertTrue(exception.getMessage().contains("Cannot iniciar diagnostico"));
    }

    @Test
    void shouldThrowWhenEnviarOrcamentoFromInvalidStatus() {
        OrdemServico ordem = novaOrdem(StatusOrdemServico.RECEBIDA);

        assertThrows(TransicaoStatusInvalidaException.class, ordem::enviarOrcamento);
    }

    @Test
    void shouldThrowWhenFinalizarFromInvalidStatus() {
        OrdemServico ordem = novaOrdem(StatusOrdemServico.RECEBIDA);

        assertThrows(TransicaoStatusInvalidaException.class, ordem::finalizar);
    }

    @Test
    void shouldThrowWhenEntregarFromInvalidStatus() {
        OrdemServico ordem = novaOrdem(StatusOrdemServico.EM_EXECUCAO);

        assertThrows(TransicaoStatusInvalidaException.class, ordem::entregar);
    }

    @Test
    void shouldRecalculateOrcamentoWhenUpdatingPecas() {
        OrdemServico ordem = OrdemServico.reconstituir(
                UUID.randomUUID(),
                StatusOrdemServico.EM_DIAGNOSTICO,
                LocalDateTime.now(),
                null,
                null,
                null,
                null,
                2,
                List.of(BigDecimal.valueOf(100)));

        ordem.atualizarPecas(List.of(BigDecimal.valueOf(120), BigDecimal.valueOf(30)));

        assertEquals(BigDecimal.valueOf(450), ordem.getValorOrcamento());
    }

    @Test
    void shouldCalculateOrcamentoWithNullPrices() {
        BigDecimal valor = OrdemServico.calcularOrcamento(1, null);

        assertEquals(BigDecimal.valueOf(150), valor);
    }

    @Test
    void shouldExposeStatusPriorityAndClosedFlag() {
        assertTrue(StatusOrdemServico.EM_EXECUCAO.prioridadeListagem()
                > StatusOrdemServico.RECEBIDA.prioridadeListagem());
        assertTrue(StatusOrdemServico.FINALIZADA.isEncerrada());
        assertTrue(StatusOrdemServico.ENTREGUE.isEncerrada());
        assertNull(novaOrdem(StatusOrdemServico.RECEBIDA).getOrcamentoAprovado());
    }

    private OrdemServico novaOrdem(StatusOrdemServico status) {
        return OrdemServico.reconstituir(
                UUID.randomUUID(),
                status,
                LocalDateTime.now(),
                null,
                null,
                null,
                null,
                1,
                List.of(BigDecimal.valueOf(80)));
    }
}
