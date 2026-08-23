package com.postech.challenge.infrastructure.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

import com.postech.challenge.application.observability.ObservabilidadeServico;
import com.postech.challenge.application.gateway.NotificacaoOrdemServicoGateway;
import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;
import com.postech.challenge.infrastructure.web.CorrelationIdFilter;

@Primary
@Component
public class HttpNotificacaoGatewayImpl implements NotificacaoOrdemServicoGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpNotificacaoGatewayImpl.class);

    private final RestClient restClient;
    private final EmailNotificacaoGatewayImpl emailFallback;
    private final ObservabilidadeServico observabilidadeServico;
    private final String notifyUrl;

    public HttpNotificacaoGatewayImpl(
            EmailNotificacaoGatewayImpl emailFallback,
            ObservabilidadeServico observabilidadeServico,
            @Value("${notificacao.http.url:http://localhost:8091/notify}") String notifyUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
        this.emailFallback = emailFallback;
        this.observabilidadeServico = observabilidadeServico;
        this.notifyUrl = notifyUrl;
    }

    @Override
    public void notificarAtualizacaoStatus(OrdemServicoEntity ordemServico) {
        NotificationPayload payload = new NotificationPayload(
                ordemServico.getId().toString(),
                ordemServico.getStatus().name(),
                ordemServico.getCliente() != null ? ordemServico.getCliente().getCpfCnpj() : null,
                false,
                MDC.get(CorrelationIdFilter.MDC_KEY));
        try {
            restClient.post()
                    .uri(notifyUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
            LOGGER.info(
                    "Serverless notification accepted. orderId={}, status={}",
                    ordemServico.getId(),
                    ordemServico.getStatus());
        } catch (RuntimeException ex) {
            observabilidadeServico.registrarErroIntegracao();
            LOGGER.warn(
                    "Serverless notification failed, falling back to email. orderId={}, reason={}",
                    ordemServico.getId(),
                    ex.getMessage());
            emailFallback.notificarAtualizacaoStatus(ordemServico);
        }
    }

    public record NotificationPayload(
            String orderId,
            String status,
            String cpfCnpj,
            boolean failed,
            String correlationId) {
    }
}
