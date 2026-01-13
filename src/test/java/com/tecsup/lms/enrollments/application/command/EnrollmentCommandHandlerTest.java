package com.tecsup.lms.enrollments.application.command;

import com.tecsup.lms.shared.infrastructure.eventsourcing.MemoryEventStore;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Slf4j
class EnrollmentCommandHandlerTest {

    private EnrollmentCommandHandler handler;
    private MemoryEventStore eventStore;
    private ApplicationEventPublisher publisher;

    @BeforeEach
    void init(){
        publisher = mock(ApplicationEventPublisher.class);
        eventStore = new MemoryEventStore(publisher);
        handler = new EnrollmentCommandHandler(eventStore);
    }
    @Test
    void enrollStudent() {
        EnrollStudentCommand command
                = new EnrollStudentCommand("student-21",
                                            "Maria",
                                            "course-100");

        String enrollmentId = handler.enrollStudent(command);
        log.info("Enrollment ID: {}", enrollmentId);
        assertNotNull(enrollmentId);

        // Verificar que el evento se haya almacenado
        var events = eventStore.getEvents(enrollmentId);
        assertEquals(1, events.size());


    }


}