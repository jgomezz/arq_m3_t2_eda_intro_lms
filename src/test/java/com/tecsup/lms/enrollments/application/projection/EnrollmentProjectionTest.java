package com.tecsup.lms.enrollments.application.projection;

import com.tecsup.lms.enrollments.application.query.EnrollmentQueryRepository;
import com.tecsup.lms.enrollments.domain.event.LessonCompletedEvent;
import com.tecsup.lms.enrollments.domain.event.StudentEnrolledEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentProjectionTest {

    private EnrollmentProjection projection;
    private EnrollmentQueryRepository repository;

    @BeforeEach
    void init() {
        repository = new EnrollmentQueryRepository();
        projection = new EnrollmentProjection(repository);
    }

    @Test
    void onStudentEnrolled() {

        StudentEnrolledEvent event = new StudentEnrolledEvent(
                "enroll-123",
                "student-456",
                "course-789",
                "John Doe"
        );

        this.projection.onStudentEnrolled(event);

        var readModelOpt = repository.findById("enroll-123");
        assertTrue(readModelOpt.isPresent());
        var readModel = readModelOpt.get();
        assertEquals("enroll-123", readModel.getEnrollmentId());
        assertEquals("student-456", readModel.getStudentId());

    }

    @Test
    void onLessonCompleted() {
        // Primero, inscribir al estudiante
        StudentEnrolledEvent enrollEvent = new StudentEnrolledEvent(
                "enroll-123",
                "student-456",
                "course-789",
                "John Doe"
        );
        this.projection.onStudentEnrolled(enrollEvent);

        // Simular la finalización de una lección
        LessonCompletedEvent lessonEvent = new LessonCompletedEvent(
                "enroll-123",
                "lesson-001",
                20 // Nuevo progreso
        );
        this.projection.onLessonCompleted(lessonEvent);

        // Simular la finalización de una lección
        LessonCompletedEvent lessonEvent2 = new LessonCompletedEvent(
                "enroll-123",
                "lesson-002",
                15 // Nuevo progreso
        );
        this.projection.onLessonCompleted(lessonEvent2);

        var readModelOpt = repository.findById("enroll-123");
        assertTrue(readModelOpt.isPresent());
        var readModel = readModelOpt.get();
        assertEquals(35, readModel.getProgressPercentage());
    }
}