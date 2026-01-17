package com.tecsup.lms.enrollments.application.projection;

import com.tecsup.lms.enrollments.application.query.EnrollmentQueryRepository;
import com.tecsup.lms.enrollments.application.query.EnrollmentReadModel;
import com.tecsup.lms.enrollments.domain.event.LessonCompletedEvent;
import com.tecsup.lms.enrollments.domain.event.StudentEnrolledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class EnrollmentProjection {

    private final EnrollmentQueryRepository repository;

    @EventListener
    public void onStudentEnrolled(StudentEnrolledEvent event) {

        log.info("📥 Processing StudentEnrolledEvent for student: {}",
                event.getStudentName());

        var readModel = new EnrollmentReadModel(
                event.getEnrollmentId(),
                event.getStudentId(),
                event.getCourseId(),
                event.getStudentName(),
                0
        );
        repository.save(readModel);
    }

    @EventListener
    public void onLessonCompleted(LessonCompletedEvent event) {

        log.info("📥 Processing LessonCompletedEvent for enrollment ID: {}",
                event.getEnrollmentId());

        EnrollmentReadModel readModel = repository.findById(event.getEnrollmentId()).orElseThrow();

        // Actualiza el progreso
        readModel.setProgressPercentage(readModel.getProgressPercentage() +
                event.getNewProgressPercentage());

        // Guarda el read model actualizado
        repository.save(readModel);

    }

}
