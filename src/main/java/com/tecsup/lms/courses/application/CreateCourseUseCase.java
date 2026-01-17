package com.tecsup.lms.courses.application;


import com.tecsup.lms.courses.domain.event.CourseCreatedEvent;
import com.tecsup.lms.courses.domain.model.Course;
import com.tecsup.lms.courses.domain.repository.CourseRepository;
import com.tecsup.lms.shared.domain.event.RabbitMQEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static com.tecsup.lms.shared.infrastructure.config.RabbitMQConfig.COURSE_CREATED_RK;

@Slf4j
@RequiredArgsConstructor
public class CreateCourseUseCase {

    private final CourseRepository repository;

    // private final EventPublisher eventPublisher;
    private final RabbitMQEventPublisher eventPublisher;

    public Course createCourse(String title, String description, String instructor) {

        Course course = Course.builder()
                .title(title)
                .description(description)
                .instructor(instructor)
                .status(Course.CourseStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .build();

        Course saved = repository.save(course);
        log.info("Course created: {}", saved.getId());

        // Publicar el evento
        eventPublisher.publish(
                COURSE_CREATED_RK,
                new CourseCreatedEvent(
                        saved.getId().toString(),
                        saved.getTitle(),
                        saved.getInstructor()
                )
        );

        return saved;
    }

}
