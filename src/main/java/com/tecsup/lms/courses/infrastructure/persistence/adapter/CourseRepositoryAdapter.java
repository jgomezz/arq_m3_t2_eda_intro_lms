package com.tecsup.lms.courses.infrastructure.persistence.adapter;

import com.tecsup.lms.courses.domain.model.Course;
import com.tecsup.lms.courses.domain.repository.CourseRepository;
import com.tecsup.lms.courses.infrastructure.persistence.repository.JpaCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseRepositoryAdapter implements CourseRepository {

    private final JpaCourseRepository jpaRepository;

    @Override
    public Course save(Course course) {
        return jpaRepository.save(course);
    }
}
