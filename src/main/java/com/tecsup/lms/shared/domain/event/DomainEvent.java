package com.tecsup.lms.shared.domain.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class DomainEvent {

    private final String eventId;

    private final String eventType;

    private final LocalDateTime ocurredOn;

    public DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = this.getClass().getSimpleName();
        this.ocurredOn = LocalDateTime.now();
    }


}
