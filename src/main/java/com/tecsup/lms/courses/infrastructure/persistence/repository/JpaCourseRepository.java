package com.tecsup.lms.courses.infrastructure.persistence.repository;

import com.tecsup.lms.courses.domain.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCourseRepository extends JpaRepository<Course, Long> {
}