package com.tecsup.lms.courses.application;

import com.tecsup.lms.courses.domain.event.CoursePublishedEvent;
import com.tecsup.lms.courses.domain.model.Course;
import com.tecsup.lms.courses.domain.repository.CourseRepository;
import com.tecsup.lms.shared.domain.event.EventPublisher;
import com.tecsup.lms.shared.domain.event.RabbitMQEventPublisher;
import com.tecsup.lms.shared.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import static com.tecsup.lms.shared.infrastructure.config.RabbitMQConfig.COURSE_CREATED_RK;

@Slf4j
@RequiredArgsConstructor
public class PublishCourseUseCase {

    private final CourseRepository repository;

    // private final EventPublisher eventPublisher;
    private final RabbitMQEventPublisher eventPublisher;

    @Transactional
    public Course publishCourse(Long courseId, double price) {

        Course course = repository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setStatus(Course.CourseStatus.PUBLISHED);
        Course saved = repository.save(course);

        log.info("Course published: {}", saved.getId());

        // Publicar el evento
        eventPublisher.publish(
                RabbitMQConfig.COURSE_PUBLISHED_RK,
                new CoursePublishedEvent(
                        saved.getId().toString(),
                        saved.getTitle(),
                        price
                )
        );

        return saved;
    }

}
