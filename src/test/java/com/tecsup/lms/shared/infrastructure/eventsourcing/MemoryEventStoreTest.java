package com.tecsup.lms.shared.infrastructure.eventsourcing;

import com.tecsup.lms.shared.domain.event.DomainEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


class TestEvent extends DomainEvent {

    private final String data;

    public TestEvent(String data) {
        super();
        this.data = data;
    }
}


public class MemoryEventStoreTest {

    private MemoryEventStore eventStore;
    private ApplicationEventPublisher publisher;

    @BeforeEach
    void init(){
        publisher = mock(ApplicationEventPublisher.class);
        eventStore = new MemoryEventStore(publisher);
    }

    @Test
    void save() {

        String aggregateId = "matricula-1";
        TestEvent event = new TestEvent("datos de matricula de estudiante 1");
        TestEvent event2 = new TestEvent("datos de matricula de estudiante 2");

        // Guardar el evento
        eventStore.save(aggregateId, event);
        eventStore.save(aggregateId, event2);

        // Recuperar los eventos
        var events = eventStore.getEvents(aggregateId);

        assertEquals(2, events.size());

        //assertEquals(event, events.get(0));

    }
}