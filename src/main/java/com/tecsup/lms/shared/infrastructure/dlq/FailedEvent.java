package com.tecsup.lms.shared.infrastructure.dlq;

import com.tecsup.lms.shared.domain.event.DomainEvent;
import lombok.AllArgsConstructor;

/**
 * FailedEvent
 */
@AllArgsConstructor
public class FailedEvent {

    private final DomainEvent event;

    private final String reason;

    private final long timestamp;

}
