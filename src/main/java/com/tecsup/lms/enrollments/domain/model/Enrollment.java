package com.tecsup.lms.enrollments.domain.model;

import com.tecsup.lms.enrollments.domain.event.LessonCompletedEvent;
import com.tecsup.lms.enrollments.domain.event.StudentEnrolledEvent;
import com.tecsup.lms.shared.domain.event.DomainEvent;
import lombok.Getter;

import java.util.List;


/**
 *  Se encarga de representar la inscripción de un estudiante en un curso.
 *
 */
@Getter
public class Enrollment {

    //
    private String enrollmentId;
    private String studentId;
    private String studentName;
    private String courseId;
    private int progressPercentage;

    public Enrollment() {
        this.progressPercentage = 0;
    }

    /**
     * Construye una Enrollment a partir de una lista de eventos
     * @param events
     * @return
     */
    public static Enrollment fromEvents (List<DomainEvent> events) {

        Enrollment enrollment = new Enrollment();

        for(DomainEvent event: events) {
            enrollment.apply(event);
        }

        return enrollment;
    }

    /**
     * Aplica un evento para modificar el estado de la Enrollment
     * @param event
     */
    private void apply(DomainEvent event) {
        if (event instanceof StudentEnrolledEvent e) {
            this.enrollmentId = e.getEnrollmentId();
            this.studentId = e.getStudentId();
            this.studentName = e.getStudentName();
            this.courseId = e.getCourseId();
            this.progressPercentage = 0;

        } else if (event instanceof LessonCompletedEvent e) {
            this.progressPercentage = e.getNewProgressPercentage();

        }

    }

}
