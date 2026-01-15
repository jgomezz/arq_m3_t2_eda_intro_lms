package com.tecsup.lms.enrollments.application.command;

import com.tecsup.lms.enrollments.domain.event.LessonCompletedEvent;
import com.tecsup.lms.enrollments.domain.event.StudentEnrolledEvent;
import com.tecsup.lms.enrollments.domain.model.Enrollment;
import com.tecsup.lms.shared.infrastructure.eventsourcing.MemoryEventStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    /**
     * Agregar una lección a una inscripción
     * @param enrollmentId
     * @param lessonId
     */
    public void addLesson(String enrollmentId, String lessonId) {

        // Obtener todos los eventos para el enrollmentId
        var events = eventStore.getEvents(enrollmentId);

        // Reconstruir el estado actual de la inscripción
        var enrollment = Enrollment.fromEvents(events);

        // calcular nuevo progreso
        int newProgress = enrollment.getProgressPercentage() + 10;
        // Simulación de progreso

        log.info("Enrollment {} - New progress after completing lesson {}: {}%",
                enrollmentId, lessonId, newProgress);

        // Crear evento de lección completada
        var event = new LessonCompletedEvent(
                enrollmentId,
                lessonId,
                newProgress
        );

        // Almacenar el evento en el Event Store
        eventStore.save(enrollmentId, event);
    }
}
