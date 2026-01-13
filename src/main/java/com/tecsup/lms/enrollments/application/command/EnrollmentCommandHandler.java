package com.tecsup.lms.enrollments.application.command;

import com.tecsup.lms.enrollments.domain.event.StudentEnrolledEvent;
import com.tecsup.lms.shared.infrastructure.eventsourcing.MemoryEventStore;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnrollmentCommandHandler {

    private final MemoryEventStore eventStore;

    public String enrollStudent(EnrollStudentCommand command) {

        // Generea el ID de inscripción
        String enrollmentId = "enrollment-" + System.currentTimeMillis(); // Simulación de ID de inscripción

        // Crear evento de inscripción
        StudentEnrolledEvent event = new StudentEnrolledEvent(
                enrollmentId,
                command.getStudentId(),
                command.getStudentName(),
                command.getCourseId()
        );

        // Almacenar el evento en el Event Store
        eventStore.save(enrollmentId, event);

        return enrollmentId;
    }

}
