package com.postech.challenge.infrastructure.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.postech.challenge.application.gateway.NotificacaoOrdemServicoGateway;
import com.postech.challenge.infrastructure.persistence.entity.OrdemServicoEntity;

@Component
public class EmailNotificacaoGatewayImpl implements NotificacaoOrdemServicoGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotificacaoGatewayImpl.class);

    private final JavaMailSender mailSender;
    private final String remetente;
    private final String destinatario;

    public EmailNotificacaoGatewayImpl(
            JavaMailSender mailSender,
            @Value("${notificacao.email.remetente:oficina@oficina.com}") String remetente,
            @Value("${notificacao.email.destinatario:notificacoes@oficina.com}") String destinatario) {
        this.mailSender = mailSender;
        this.remetente = remetente;
        this.destinatario = destinatario;
    }

    @Override
    public void notificarAtualizacaoStatus(OrdemServicoEntity ordemServico) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(remetente);
        message.setTo(destinatario);
        message.setSubject("Atualizacao da Ordem de Servico " + ordemServico.getId());
        message.setText(buildBody(ordemServico));

        try {
            mailSender.send(message);
            LOGGER.info(
                    "Email de atualizacao de status enviado. ordemId={}, status={}",
                    ordemServico.getId(),
                    ordemServico.getStatus());
        } catch (MailException ex) {
            LOGGER.warn(
                    "Falha ao enviar email de atualizacao de status. ordemId={}, motivo={}",
                    ordemServico.getId(),
                    ex.getMessage());
        }
    }

    private String buildBody(OrdemServicoEntity ordemServico) {
        return """
                Ola,

                A ordem de servico %s teve seu status atualizado para: %s.
                Cliente (CPF/CNPJ): %s
                Valor do orcamento: %s

                Equipe da Oficina.
                """.formatted(
                ordemServico.getId(),
                ordemServico.getStatus(),
                ordemServico.getCliente().getCpfCnpj(),
                ordemServico.getValorOrcamento());
    }
}
