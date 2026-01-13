
## Implementación de Event Sourcing : Enrollment 

1.- Crear Event Store  
     El Event Store es un almacén de eventos.

<img src="images/event_sourcing_step_1.png" alt="Event Store" width="400"/>

Localización: 

<img src="images/event_sourcing_step_2.png" alt="Event Store" width="400"/>

EventStore.java
```
import com.tecsup.lms.shared.domain.event.DomainEvent;

import java.util.List;

public interface EventStore {

    void save(String aggregateId, DomainEvent event);

    List<DomainEvent> getEvents(String aggregateId);

}
```

MemoryEventStore.java
```

import com.tecsup.lms.shared.domain.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class MemoryEventStore  implements EventStore{

    // final es necesario
    private final Map<String, List<DomainEvent>> stores = new ConcurrentHashMap<>();

    private final ApplicationEventPublisher publisher;

    public MemoryEventStore(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void save(String aggregateId, DomainEvent event) {

        // Agregar el evento  al stores
        this.stores.computeIfAbsent(aggregateId, key -> new ArrayList<>())
                .add(event);

        // Publicar el evento
        publisher.publishEvent(event);

    }

    @Override
    public List<DomainEvent> getEvents(String aggregateId) {
        return new ArrayList<>(stores.getOrDefault(aggregateId, List.of()));
    }

}

```
MemoryEventStoreTest.java
```

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
    void ini(){
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
```

2.- Crear Eventos de Enrollment
    Evento que se van a producir en el pasado

Se crea en un nuevo modulo : enrollments

<img src="images/event_sourcing_step_3.png" alt="Event Store" width="400"/>

Localización:

<img src="images/event_sourcing_step_4.png" alt="Event Store" width="400"/>

StudentEnrolledEvent.java
```
import com.tecsup.lms.shared.domain.event.DomainEvent;
import lombok.Getter;

@Getter
public class StudentEnrolledEvent extends DomainEvent {

    private final String enrollmentId;
    private final String studentId;
    private final String studentName;
    private final String courseId;

    public StudentEnrolledEvent(String enrollmentId, String studentId, String studentName, String courseId) {
        super();
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseId = courseId;
    }
}
```


3.- Crear Aggregate
    Para reconstruir los eventos

4.- Command Handler
    Procesa la solicitud de cambio y genera eventos