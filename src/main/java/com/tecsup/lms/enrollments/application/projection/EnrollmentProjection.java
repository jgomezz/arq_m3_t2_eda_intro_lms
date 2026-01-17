package com.tecsup.lms.enrollments.application.projection;

import com.tecsup.lms.enrollments.application.query.EnrollmentQueryRepository;
import com.tecsup.lms.enrollments.application.query.EnrollmentReadModel;
import com.tecsup.lms.enrollments.domain.event.StudentEnrolledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EnrollmentProjection {

    private final EnrollmentQueryRepository repository;

    @EventListener
    public void onStudentEnrolled(StudentEnrolledEvent event) {
        var readModel = new EnrollmentReadModel(
                event.getEnrollmentId(),
                event.getStudentId(),
                event.getCourseId(),
                event.getStudentName(),
                0
        );
        repository.save(readModel);
    }

}
