package com.tecsup.lms.courses.application;

import com.tecsup.lms.courses.domain.event.CoursePublishedEvent;
import com.tecsup.lms.courses.domain.model.Course;
import com.tecsup.lms.courses.domain.repository.CourseRepository;
import com.tecsup.lms.shared.domain.event.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class PublishCourseUseCase {

    private final CourseRepository repository;

    private final EventPublisher eventPublisher;

    @Transactional
    public Course publishCourse(Long courseId, double price) {

        Course course = repository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setStatus(Course.CourseStatus.PUBLISHED);
        Course saved = repository.save(course);

        log.info("Course published: {}", saved.getId());

        // Publicar el evento
        eventPublisher.publish(
                new CoursePublishedEvent(
                        saved.getId().toString(),
                        saved.getTitle(),
                        price
                )
        );

        return saved;
    }

}
