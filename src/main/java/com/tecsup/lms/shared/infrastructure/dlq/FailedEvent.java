package com.tecsup.lms.shared.infrastructure.dlq;

import com.tecsup.lms.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FailedEvent
 */
@Getter // Importante para la serializacion/deserialización
@AllArgsConstructor
public class FailedEvent {

    private final DomainEvent event;

    private final String reason;

    private final long timestamp;

}
