package com.tecsup.lms.shared.infrastructure.event;

import com.tecsup.lms.shared.domain.event.DomainEvent;
import com.tecsup.lms.shared.infrastructure.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    //private final ApplicationEventPublisher publisher;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    public void publish(DomainEvent event) {
        log.info("Publicando: {} [{}]", event.getEventType(), event.getEventId());

        //publisher.publishEvent(event);
        String key = event.getKey(); // devuelva el course Id

        kafkaTemplate.send(
                KafkaConfig.COURSE_EVENTS_TOPIC,
                key,
                event
        );

        // La key sirve para identificar a que particion va el mensaje
        // HASH(key) % N_PARTICIONES = particion


    }
}