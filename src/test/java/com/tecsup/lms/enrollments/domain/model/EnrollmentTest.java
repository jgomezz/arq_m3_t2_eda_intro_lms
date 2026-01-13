package com.tecsup.lms.enrollments.domain.model;

import com.tecsup.lms.enrollments.domain.event.StudentEnrolledEvent;
import com.tecsup.lms.shared.domain.event.DomainEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentTest {

    @Test
    void testEnrollmentCreation() {

        StudentEnrolledEvent event = new StudentEnrolledEvent(
                "enroll-123",
                "student-01",
                "Juan",
                "course-01"
        );

        List<DomainEvent> events = List.of(event);

        Enrollment enrollment = Enrollment.fromEvents(events);

        assertEquals("enroll-123", enrollment.getId());
        assertEquals("student-01", enrollment.getStudentId());
        assertEquals("Juan", enrollment.getStudentName());
        assertEquals("course-01", enrollment.getCourseId());

    }

}