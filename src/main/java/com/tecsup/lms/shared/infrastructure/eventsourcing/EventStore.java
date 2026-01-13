package com.tecsup.lms.shared.infrastructure.eventsourcing;

import com.tecsup.lms.shared.domain.event.DomainEvent;

import java.util.List;

public interface EventStore {

    void save(String aggregateId, DomainEvent event);

    List<DomainEvent> getEvents(String aggregateId);

}
