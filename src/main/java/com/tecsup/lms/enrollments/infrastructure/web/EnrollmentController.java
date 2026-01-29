package com.tecsup.lms.enrollments.infrastructure.web;

import com.tecsup.lms.enrollments.application.command.EnrollStudentCommand;
import com.tecsup.lms.enrollments.application.command.EnrollmentCommandHandler;
import com.tecsup.lms.enrollments.application.saga.EnrollmentSagaHandler;
import com.tecsup.lms.enrollments.domain.model.Enrollment;
import com.tecsup.lms.enrollments.infrastructure.dto.EnrollmentRequest;
import com.tecsup.lms.enrollments.infrastructure.dto.EnrollmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentCommandHandler enrollmentCommandHandler;


    /**
     *  Enroll a student in a course
     */
    @PostMapping
    public ResponseEntity<EnrollmentResponse>
        enrollStudent(@RequestBody EnrollmentRequest request) {

        EnrollStudentCommand command = new EnrollStudentCommand(
                request.getStudentId(),
                request.getStudentName(),
                request.getCourseId()
        );


        String enrollmentId = enrollmentCommandHandler.enrollStudent(command);

        return ResponseEntity.ok(EnrollmentResponse
                                .builder()
                                .enrollmentId(enrollmentId)
                                .build());
    }

    @PostMapping("/{enrollmentId}/lessons/{lessonId}")
    public ResponseEntity<Void> addLesson(@PathVariable String enrollmentId,
                                          @PathVariable String lessonId) {

        enrollmentCommandHandler.addLesson(enrollmentId, lessonId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{enrollmentId}/progress")
    public ResponseEntity<Void> getEnrollmentProgress(@PathVariable String enrollmentId) {
        // Lógica para obtener el progreso de la inscripción

        Enrollment enrollment = enrollmentCommandHandler.getEnrollmentProgress(enrollmentId);

        log.info("Enrollment {} - Current progress: {}%",
                enrollmentId, enrollment.getProgressPercentage());

        return ResponseEntity.ok().build();
    }

    // ========================================
    // SAGA
    // ========================================

    private final EnrollmentSagaHandler sagaHandler;

    @PostMapping("/request")
    public ResponseEntity<EnrollmentResponse> requestEnrollment(
            @RequestBody EnrollmentRequest request) {

        // Iniciar saga
        String enrollmentId = this.sagaHandler.requestEnrollment(request.getStudentId(),
                request.getStudentName(),
                request.getCourseId(),
                request.getAmount());

        EnrollmentResponse response = EnrollmentResponse.builder()
                                    .enrollmentId(enrollmentId)
                                    .status("PENDING")
                                    .message("Enrollment request is being processed")
                                    .build();

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }


}








