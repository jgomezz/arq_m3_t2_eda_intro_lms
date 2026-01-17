package com.tecsup.lms.shared.domain.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.tecsup.lms.shared.infrastructure.config.RabbitMQConfig.EXCHANGE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher {

    private final RabbitTemplate template;

    public void publish(String routingKey, DomainEvent event) {

        log.info("Publicando en RabbitMQ: {} [{}]", event.getEventType(), event.getEventId());

        template.convertAndSend(EXCHANGE_NAME
                , routingKey
                , event);

    }
}
